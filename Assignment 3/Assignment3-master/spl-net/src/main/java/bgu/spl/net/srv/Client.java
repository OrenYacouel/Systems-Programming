package bgu.spl.net.srv;

import bgu.spl.net.srv.messages.Notification;
import bgu.spl.net.srv.messages.PMReq;
import bgu.spl.net.srv.messages.PostReq;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private String userName;
    private String password;
    private String bDay;
    private AtomicBoolean isLogged;
    private AtomicInteger connectionID;
    private ConcurrentLinkedQueue<String> follows;
    private ConcurrentLinkedQueue<String> followedBy;
    private ConcurrentLinkedQueue<PostReq> posts;
    private ConcurrentLinkedQueue<PMReq> pms;
    private ConcurrentLinkedQueue<Notification> missedNotifications;
    private ConcurrentLinkedQueue<Client> blockedUsers;
    private ConcurrentLinkedQueue<Client> blockedBy;

    public Client(String _userName, String _password , String _bDay) {
        this.userName = _userName;
        this.password = _password;
        this.bDay = _bDay;
        this.isLogged = new AtomicBoolean(false);
        connectionID = new AtomicInteger(-1);
        follows = new ConcurrentLinkedQueue<>();
        followedBy = new ConcurrentLinkedQueue<>();
        posts = new ConcurrentLinkedQueue<>();
        pms = new ConcurrentLinkedQueue<>();
        missedNotifications = new ConcurrentLinkedQueue<>();
        blockedUsers = new ConcurrentLinkedQueue<>();
        blockedBy = new ConcurrentLinkedQueue<>();
    }

    public synchronized boolean login(int connectionID) {
        if (!isLogged.get()) {
            this.connectionID.set(connectionID);
            return this.isLogged.compareAndSet(false, true);
        }
        return false;
    }

    public synchronized boolean logout() {
        if (isLogged.get()) {
            connectionID.set(-1);
            return this.isLogged.compareAndSet(true, false);
        }
        return false;
    }

    public boolean addFollowsList(String userName) {
        if (userName != null && !userName.isEmpty() && !follows.contains(userName)) {
            return follows.add(userName);
        }
        return false;
    }

    public boolean removeFromFollowsList(String userName) {
        if (userName != null && !userName.isEmpty() && follows.contains(userName))
            return follows.remove(userName);
        return false;
    }

    public boolean addFollowedByList(String userName) {
        if (userName != null && !userName.isEmpty() && !followedBy.contains(userName)) {
            return followedBy.add(userName);
        }
        return false;
    }

    public boolean removeFromFollowedByList(String userName) {
        if (userName != null && !userName.isEmpty() && followedBy.contains(userName))
            return followedBy.remove(userName);
        return false;
    }

    public boolean isBlocked(Client otherClient){
        if (blockedBy.contains(otherClient) || blockedUsers.contains(otherClient))
            return true;
        return false;
    }
    public void leaveAMessage(Notification notification) {
        missedNotifications.add(notification);
    }

    public void sentPost(PostReq post) {
        posts.add(post);
    }

    public void sentPM(PMReq pm) {
        pms.add(pm);
    }

    public boolean isFollowingMe(Client client){
        return followedBy.contains(client.getUserName());
    }
    public boolean isLogged() {
        return isLogged.get();
    }

    public short howManyPosts() {
        return (short) posts.size();
    }

    public short howManyFollowers() {
        return (short) followedBy.size();
    }

    public short howManyFollowing() {
        return (short) follows.size();
    }

//    Getters

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Notification pullNotification() {
        return missedNotifications.poll();
    }

    public int getConnectionID() {
        return connectionID.get();
    }

    public ConcurrentLinkedQueue<String> getFollowers() {
        return followedBy;
    }

    public ConcurrentLinkedQueue<String> getFollows() {
        return follows;
    }

    public String getbDay() {
        return bDay;
    }

    public short getAge(){
        int age= 2022 - Integer.valueOf(bDay.substring(6));
        return (short)age;
    }

    public ConcurrentLinkedQueue<Client> getBlockedBy() {
        return blockedBy;
    }

    public ConcurrentLinkedQueue<Client> getBlockedUsers() {
        return blockedUsers;
    }
}