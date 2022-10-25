package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.messages.Error;
import bgu.spl.net.srv.messages.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private boolean shouldTerminate = false;
    private String clientName;  //contains the username if logged in or an empty string if not
    private int connectionId;
    private Connections<Message> connections;
    private DataBase dataBase;

    public BidiMessagingProtocolImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
        clientName = "";
    }

    @Override
    public void process(Message message) {
        if (message != null) {
            switch (message.getOpcode()) {
                case 1:
                    register((RegisterReq) message);
                    break;
                case 2:
                    login((LoginReq) message);
                    break;
                case 3:
                    logout((LogoutReq) message);
                    break;
                case 4:
                    follow((FollowReq) message);
                    break;
                case 5:
                    post((PostReq) message);
                    break;
                case 6:
                    pm((PMReq) message);
                    break;
                case 7:
                    logStat((LogStatReq) message);
                    break;
                case 8:
                    stat((StatsReq) message);
                    break;
                case 12:
                    block((Block) message);
                    break;

            }
        }
    }





    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private boolean register(RegisterReq message) {
        Client client = new Client(message.getUserName(), message.getPassword() , message.getbDay());
        if (dataBase.addClient(client)) {
            if (connections==null)
                System.out.println("connections is null");
            if (!connections.send(connectionId, new Ack(message.getOpcode())))
                System.out.println("error while trying to send register ack");
            return true;
        } else {
            if (!connections.send(connectionId, new Error(message.getOpcode())))
                System.out.println("error while trying to send error");
            return false;
        }
    }

    private boolean login(LoginReq message) {
        if (dataBase.getClient(message.getUserName()) != null && message.isCaptchaGood() && //user is registered and captcha correct
                dataBase.getClient(message.getUserName()).getPassword().equals(message.getPassword()) &&      //password entered is correct
                clientName.isEmpty() &&                               //current session isn't logged in
                dataBase.getClient(message.getUserName()).login(connectionId)) {        //login if user wasn't
            if (!connections.send(connectionId, new Ack(message.getOpcode())))
                System.out.println("error while trying to send login ack");
            clientName = message.getUserName(); // update this protocol's loggedClientName to be the userName
            Notification noti = dataBase.getClient(message.getUserName()).pullNotification();
            while (noti != null) { //pull all the notifications this client received while being logged out
                if (!connections.send(connectionId, noti))
                    System.out.println("error while trying to send notification");
                noti = dataBase.getClient(message.getUserName()).pullNotification();
            }
            return true;
        } else {
            if (!connections.send(connectionId, new Error(message.getOpcode())))
                System.out.println("error while trying to send the login error");
            return false;
        }
    }

    private boolean logout(LogoutReq message) {
        if (clientName != null && !clientName.isEmpty() &&  // if there is a loggedClientName
                dataBase.getClient(clientName).logout()) {
            if (!connections.send(connectionId, new Ack(message.getOpcode())))
                System.out.println("error while trying to send logout ack");
            connections.disconnect(connectionId);
            clientName = "";
            shouldTerminate = true;
            return true;
        } else {
            if (!connections.send(connectionId, new Error(message.getOpcode()))) //send an error message
                System.out.println("error while trying to send the logout error");
            return false;
        }
    }

    private boolean follow(FollowReq message) {
        if (clientName != null && !clientName.isEmpty() && // check clientName's validity
                !dataBase.getClient(clientName).isBlocked(dataBase.getClient(message.getUsername()))) { //check if one of them isn't blocking the other
            if (message.getFollowOrUnF().equals("0") ) {        // checking follow or unfollow- if follow...
                if ((dataBase.getClient(message.getUsername()) != null) &&       //if it isn't null
                        (dataBase.getClient(clientName).addFollowsList(message.getUsername())) && //try to add current user to the followList (return true if not already there)
                        dataBase.getClient(message.getUsername()).addFollowedByList(clientName)) {//try to add the user to the current user's list (return true if not already there)
                    // send a special ack that contains the name of the userName you followed/unfollowed
                    if (!connections.send(connectionId, new FollowAck(message.getOpcode() , message.getUsername())))
                        System.out.println("error while trying to send un/follow ack");
                    return true;
                }
            }
            else { //unfollow request
                if (message.getFollowOrUnF().equals("1")) {
                    if ((dataBase.getClient(message.getUsername()) != null) &&
                            (dataBase.getClient(clientName).removeFromFollowsList(message.getUsername()) &&
                                    dataBase.getClient(message.getUsername()).removeFromFollowedByList(clientName))){
                        // send a special ack that contains the name of the userName you followed/unfollowed
                        if (!connections.send(connectionId, new FollowAck(message.getOpcode() , message.getUsername())))
                            System.out.println("error while trying to send un/follow ack");
                    }
                        return true;
                }
                else System.out.println("illegal follow code received: " + message.getFollowOrUnF());

            }
        }
        else if (!connections.send(connectionId, new Error(message.getOpcode())))
            System.out.println("error while trying to send un/follow error");
        return false;
    }

    private boolean post(PostReq message) {

        if (clientName != null && !clientName.isEmpty() && dataBase.getClient(clientName).isLogged() ) {
            Client poster = dataBase.getClient(clientName);
            //add the message to the poster's queue of sent posts
            dataBase.getClient(clientName).sentPost(message);
            //send the message to users that were tagged in the content
            List<String> taggedUsers = new LinkedList<>();
            for (String username : findValidTags(message.getContent())) {
                boolean userBlock = false;
//               findValidTags returns an array of usernames that were tagged in the content
//               search if one of the usernames has blocked me or i him, if yes, don't send them
                for (Client blockingClient: poster.getBlockedBy()) {
                    if (blockingClient.getUserName().equals(username)) {
                        userBlock = true;
                        break;
                    }
                }
                if (!userBlock) {
                    for (Client blockedClient : poster.getBlockedUsers()) {
                        if (blockedClient.getUserName().equals(username)) {
                            userBlock = true;
                            break;
                        }
                    }
                }
                if ((!taggedUsers.contains(username)) && !userBlock &&
                        (!sendByUserName(username, new Notification((byte) 1, clientName, message.getContent()))))
                    System.out.println("error while trying to post");
                taggedUsers.add(username);
            }
            //send the message to users following the poster
            for (String userName : dataBase.getClient(clientName).getFollowers())
                if ((!taggedUsers.contains(userName)) &&
                        (!sendByUserName(userName, new Notification((byte) 1, clientName, message.getContent()))))
                    System.out.println("error while trying to post");
            if (!connections.send(connectionId, new Ack(message.getOpcode())))
                System.out.println("error while trying to send post ack");
            return true;
        } else {
            if (!connections.send(connectionId, new Error(message.getOpcode())))
                System.out.println("error while trying to send  error");
            return false;
        }
    }

    private boolean pm(PMReq message) {
        if ((clientName != null && !clientName.isEmpty())) {
            Client sender = dataBase.getClient(clientName);
            Client receiver = dataBase.getClient(message.getUsername());
            String censoredContent = censor(message.getContent());
            message.censorContent(censoredContent);
            if (sender.isLogged() && receiver.isFollowingMe(sender) &&
                    (sendByUserName(message.getUsername(), new Notification((byte) 0, clientName,
                            message.getContent() + message.getDateTime())))) {
                dataBase.getClient(clientName).sentPM(message);
                if (!connections.send(connectionId, new Ack(message.getOpcode())))
                    System.out.println("error while trying to send pm ack");
                return true;
            } else {
                if (!connections.send(connectionId, new Error(message.getOpcode())))
                    System.out.println("error while trying to send pm error");
                return false;
            }
        }
        else return false;
    }

    private boolean logStat(LogStatReq message) {
        if (clientName != null && !clientName.isEmpty() &&
                dataBase.getClient(clientName) != null && dataBase.getClient(clientName).isLogged())
        {
            Client poster = dataBase.getClient(clientName);
            List<Client> loggedUsersList = dataBase.loggedUsers();
//          remove all the logged users who blocked me OR blocked by me
            for (Client blockedClient: poster.getBlockedBy()) {
                if (loggedUsersList.contains(blockedClient))
                    loggedUsersList.remove(blockedClient);
            }
            for (Client blockedClient: poster.getBlockedUsers()) {
                if (loggedUsersList.contains(blockedClient))
                    loggedUsersList.remove(blockedClient);
            }
            int numOfSendings = loggedUsersList.size();
            for (int i=0 ; i < numOfSendings ; i++) {
                Client currClient = loggedUsersList.get(i);
                if (!connections.send(connectionId, new LogStatAck( message.getOpcode(),currClient.getAge(), currClient.howManyPosts() , currClient.howManyFollowers() , currClient.howManyFollowing()))) {
                    System.out.println("error trying to send LogStat ack");
                    return false;
                }
            }
            return true;
        }
        else {
            if (!connections.send(connectionId, new Error(message.getOpcode())))
                System.out.println("error sending logStat error");
            return false;
        }
    }

    private boolean stat(StatsReq message) {
        boolean isValid = false;
        if ((clientName != null && !clientName.isEmpty()) &&
                message.getUsernameArr() != null &&  !(message.getUsernameArr().length==0)){
            isValid = true;
        }
        for (String username : message.getUsernameArr()) {
            Client currClient = dataBase.getClient(username);
            if (currClient.isBlocked(dataBase.getClient(clientName)))
                isValid = false;
        }
        if(isValid){
            int numOfSendings = message.getUsernameArr().length;
            for (int i=0 ; i < numOfSendings ; i++) {
                Client currClient = dataBase.getClient(message.getUsernameArr()[i]);
                if (!connections.send(connectionId, new StatsAck( message.getOpcode(),currClient.getAge(),
                        currClient.howManyPosts() , currClient.howManyFollowers() , currClient.howManyFollowing()))) {
                    System.out.println("error trying to send LogStat ack");
                    return false;
                }
            }
            return true;
        }
        else {
            if (!connections.send(connectionId, new Error(message.getOpcode())))
                System.out.println("error trying to send stat error");
            return false;
        }
    }

//    blockedClient is following client, thus client if followed by blockedClient

    public boolean block(Block message){
        if(clientName!=null && !clientName.isEmpty() && dataBase.getClient(message.getUsername())!=null &&
            !dataBase.getClient(clientName).isBlocked(dataBase.getClient(message.getUsername()))){ //if one of them is blocking the other
//         if one of them follows the other, stop the follow
            Client client = dataBase.getClient(clientName);
            Client blockedClient = dataBase.getClient(message.getUsername());
            if (client.isFollowingMe(blockedClient)) {
                blockedClient.getFollows().remove(client.getUserName());
                client.getFollowers().remove(blockedClient.getUserName());
            }
            if (blockedClient.isFollowingMe(client)) {
                client.getFollows().remove(blockedClient.getUserName());
                blockedClient.getFollowers().remove(client.getUserName());
            }

//          add blockedClient to client's list of blocked users, and vise versa
            client.getBlockedUsers().add(blockedClient);
            blockedClient.getBlockedBy().add(client);

            if (!connections.send(connectionId, new Ack(message.getOpcode())))
                System.out.println("error while trying to send pm ack");
            return true;
        }
        else {
            if (!connections.send(connectionId, new Error(message.getOpcode())))
                System.out.println("error trying to send block error");
            return false;
        }
    }


    public String censor(String content){
        if (dataBase.getForbiddenWords() != null) {
            int numOfForbiddenWords = dataBase.getForbiddenWords().size();
//            go through the content and filter if the content contains any words that need to be filtered
            for (int i = 0; i < numOfForbiddenWords; i++) {
                String word = dataBase.getForbiddenWords().poll();
                String wordWithSpace = word + " ";
                if (content.contains(wordWithSpace) ||
                        (content.contains(word) && content.lastIndexOf(word) == (content.length()-1)))
                { // the second case is if word is last and thus has no space after it
                    content = content.replace(word , "<filtered>");
                }
                dataBase.getForbiddenWords().add(word);
            }
        }
        return content;
    }


    private String[] findTags(String str) {
        if (str != null && !str.isEmpty()) {
            //a regex defining a string between @ and a space
            Pattern pat = Pattern.compile("@(.+?) ");
            Matcher matcher = pat.matcher(str);
            LinkedList<String> list = new LinkedList<>();
            while (matcher.find())
                list.add(matcher.group().substring(1, matcher.group().length() - 1));
            //if there's a tag not ending with a space (may happen at the end of a line)
            if (str.lastIndexOf("@") > str.lastIndexOf(" ") && !(str.substring(str.lastIndexOf("@"))).substring(1).isEmpty())
                list.add((str.substring(str.lastIndexOf("@"))).substring(1));
            return list.toArray(new String[0]);
        }
        return new String[0];
    }

    private String[] findValidTags(String[] tags) {
        if (tags != null) {
            LinkedList<String> validTags = new LinkedList<>();
            for (String tag : tags) {
                if (dataBase.getClient(tag) != null) {
                    validTags.add(tag);
                }
            }
            return validTags.toArray(new String[0]);
        }
        return new String[0];
    }

    private String[] findValidTags(String str) {
        return findValidTags(findTags(str));
    }

    private boolean sendByUserName(String userName, Notification notification) {
        Client client = dataBase.getClient(userName);
        if (client == null || notification == null)
            return false;
        //if the client is online, send him the message. if that doesn't work, leave a message
        if (!client.isLogged() || !connections.send(client.getConnectionID(), notification))
            client.leaveAMessage(notification);
        return true;
    }
}
