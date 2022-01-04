package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BGSClientInformation {

    String userName;
    String password;
    String birthday;

    // userNames that handler is following
    ConcurrentLinkedDeque<String> followingList;
    // users that got blocked by handler
    ConcurrentLinkedDeque<String> blockedList;


    public BGSClientInformation (String userName,
                                 String password,
                                 String birthday){
        this.userName=userName;
        this.password=password;
        this.birthday=birthday;

        followingList=new ConcurrentLinkedDeque<String>();
        blockedList=new ConcurrentLinkedDeque<String>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    /**
     *
     * @return if user is already followed return false
     * */
    public boolean addFollow(String toFollow){
        if (followingList.contains(toFollow))
            return false;
        followingList.add(toFollow);
        return true;
    }
    /**
     * @return if user is not followed return false
     * */
    public boolean removeFollow(String toFollow){
        if (!followingList.contains(toFollow))
            return false;
        followingList.remove(toFollow);
        return true;
    }

    /**
     * @return if user is already blocked return false
     * */
    public void addBlocked(String toBlock){
        if (!blockedList.contains(toBlock)) {
            blockedList.add(toBlock);
        }
    }

}
