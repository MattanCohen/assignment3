package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class BGSClientInformation {

    String userName;
    String password;
    String birthday;

    // userNames that follow this handler
    ConcurrentLinkedDeque<String> followersList;
    // users that got blocked by handler
    ConcurrentLinkedDeque<String> blockedList;

    // number of sent posts
    AtomicInteger numOfPosts=new AtomicInteger(0);
    // number of users following
    AtomicInteger numOfFollows= new AtomicInteger(0);

    public void incrementNumOfPosts(){numOfPosts.incrementAndGet();}

    public void incrementNumOfFollows(){numOfFollows.incrementAndGet();}

    public void decrementNumOfFollows(){numOfFollows.decrementAndGet();}

    public BGSClientInformation (String userName,
                                 String password,
                                 String birthday){
        this.userName=userName;
        this.password=password;
        this.birthday=birthday;

        followersList =new ConcurrentLinkedDeque<String>();
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
        if (followersList.contains(toFollow))
            return false;
        followersList.add(toFollow);
        return true;
    }
    /**
     * @return if user is not followed return false
     * */
    public boolean removeFollow(String toFollow){
        if (!followersList.contains(toFollow))
            return false;
        followersList.remove(toFollow);
        return true;
    }

    public boolean isFollower(String toFollow){
        return (followersList.contains(toFollow));
    }

    /**
     * @return if user is already blocked return false
     * */
    public void addBlocked(String toBlock){
        if (!blockedList.contains(toBlock)) {
            blockedList.add(toBlock);
            try{
                followersList.remove(toBlock);
            }
            catch (Exception e){}
        }
    }


    public boolean blocked(String toFollow){
        return (blockedList.contains(toFollow));
    }

    public ConcurrentLinkedDeque<String> getBlockedList() {
        return blockedList;
    }

    public ConcurrentLinkedDeque<String> getFollowersList() {
        return followersList;
    }

    public AtomicInteger getNumOfFollows() {
        return numOfFollows;
    }

    public AtomicInteger getNumOfPosts() {
        return numOfPosts;
    }
}
