package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {

    private ConcurrentHashMap<String, Client> clientsMap;
    private ConcurrentLinkedQueue<String> clientNames; // ordered alphaBetically
    private ConcurrentLinkedQueue <String> forbiddenWords;

    public DataBase() {
        this.clientsMap = new ConcurrentHashMap<String, Client>();
        this.clientNames = new ConcurrentLinkedQueue<>();

        this.forbiddenWords = new ConcurrentLinkedQueue<>();
        filterInit();
    }

    public void filterInit(){
        forbiddenWords.add("Ringel");
        forbiddenWords.add("HeferKitBagQuestion");
        forbiddenWords.add("refactor");
        forbiddenWords.add("OOP");
        forbiddenWords.add("ENUM");
        forbiddenWords.add("IsraeliFootballIsNotMeragesh");
        forbiddenWords.add("EranLevyOverrated");
        forbiddenWords.add("EranLevyFat");
    }
    public boolean shouldFilter(String string){
        return forbiddenWords.contains(string);
    }

    public ConcurrentLinkedQueue<String> getForbiddenWords() {
        return forbiddenWords;
    }

    public boolean addClient(Client client) {
        if (client != null)
            synchronized (this) {
                String name = client.getUserName();
                if (client != null && getClient(name) == null) {
                    clientNames.add(name);
                    if(clientsMap.put(name, client) == null)
                        return true;
                    System.out.println("returned false");
                    return false;
                }
            }
        else
            System.out.println("cannot add null");
        return false;
    }

    public synchronized List<Client> loggedUsers(){
        List<Client> loggedUsersList = new LinkedList<>();
        for (String username: clientNames) {
            Client client = clientsMap.get(username);
            if (client.isLogged())
                loggedUsersList.add(client);
        }
        return loggedUsersList;
    }

//    Getters
    public Client getClient(String clientName){return clientsMap.get(clientName);}

}