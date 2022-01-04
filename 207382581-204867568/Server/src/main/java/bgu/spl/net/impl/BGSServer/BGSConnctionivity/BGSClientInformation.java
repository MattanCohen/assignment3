package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import bgu.spl.net.srv.bidi.ConnectionHandler;
import org.graalvm.compiler.lir.aarch64.AArch64AtomicMove;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class BGSClientInformation {

    String userName;
    String password;
    String birthday;

    // userNames that handler is following
    ConcurrentLinkedDeque<String> followingList;
    // users that got blocked by handler
    ConcurrentLinkedDeque<String> blockedList;

    //
    AtomicInteger numOfPosts=new AtomicInteger(0);
    AtomicInteger numOfFollows= new AtomicInteger(0);

    public void incrementNumOfPosts(){numOfPosts.incrementAndGet();}

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

    public boolean follows(String toFollow){
        return (followingList.contains(toFollow));
    }

    /**
     * @return if user is already blocked return false
     * */
    public void addBlocked(String toBlock){
        if (!blockedList.contains(toBlock)) {
            blockedList.add(toBlock);
        }
    }


    public boolean blocked (String toFollow){
        return (blockedList.contains(toFollow));
    }

    public ConcurrentLinkedDeque<String> getBlockedList() {
        return blockedList;
    }

    public ConcurrentLinkedDeque<String> getFollowingList() {
        return followingList;
    }

    public AtomicInteger getNumOfFollows() {
        return numOfFollows;
    }

    public AtomicInteger getNumOfPosts() {
        return numOfPosts;
    }
}
