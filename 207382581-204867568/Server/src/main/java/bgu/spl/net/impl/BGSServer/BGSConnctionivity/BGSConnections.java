package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import bgu.spl.net.api.BIDI.Connections;
import bgu.spl.net.api.BIDI.ConnectionHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BGSConnections implements Connections<String> {

    // map handler to userName
    ConcurrentHashMap <String, ConnectionHandler<String>> registeredUsers = new ConcurrentHashMap<>();

    // map logged on userNames to conId
    ConcurrentHashMap <Integer , String> loggedOnUsers= new ConcurrentHashMap<>();

    // map information to userName
    ConcurrentHashMap <String, BGSClientInformation> usersInformation= new ConcurrentHashMap<>();

    //map the messages sent while afk to handler
    ConcurrentHashMap <String, ConcurrentLinkedDeque<String>> usersAwaitingMessages= new ConcurrentHashMap<>();



    // map time to messages posted by handler (pm or post)
    ConcurrentHashMap<String,String> messagesId= new ConcurrentHashMap<>();


    /**
     * send is used by the connection handler.
     *
     * @param connectionId - the user IO in connections to send to
     * @param msg - the message we want to transfer (returned beautifully from protocol)
     * @return true if success in sending false otherwise
     */
    @Override
    public boolean send(int connectionId, String msg) {
        try {
            String name = loggedOnUsers.get(connectionId);
            ConnectionHandler handler = registeredUsers.get(name);
            handler.send(msg);
        }
        catch (NullPointerException e){System.out.println("exception in send in BGSConnections "+e.getMessage()); }
        throw new NullPointerException();
    }


    /**
     * when a handler wants to broadcast a message, all following users
     * should receive said message.
     *
     * @param msg the message to broadcast to said users
     */
    @Override
    public void broadcast(String msg) {
        for (Integer connectionId : loggedOnUsers.keySet()){
            String name = loggedOnUsers.get(connectionId);
            ConnectionHandler handler = registeredUsers.get(name);
            handler.send(msg);
        }
    }


    /**
     * Remove connectionHandler from hashmap
     * */
    @Override
    public void disconnect(int connectionId) {
        loggedOnUsers.remove(connectionId);
    }
    /*

    // map handler to userName
    ConcurrentHashMap <String, ConnectionHandler<String>> registeredUsersToHandlers;
    // map conId to logged on userNames
    ConcurrentHashMap <String , Integer> loggedOnUsersToConId;
    // map information to userName
    ConcurrentHashMap <String, BGSClientInformation> usersInformation;
     */




    /**
     * request register
     * if userName is registered, return false.
     * else create BGSClientInformation for userName handler containing userName password
     * and birthday and map it's handler
     *
     * @param userName userName
     * @param password password
     * @param birthday birthday
     * @param handler handler to match
     * @return true    if userName isn't registered
     * @retyrn false   otherwise
     */
    public boolean registerUser(Integer conid, String userName, String password, String birthday, ConnectionHandler<String> handler){
        if (isRegistered(userName)){
            return false;
        }
        else{
            BGSClientInformation info=new BGSClientInformation(userName,password,birthday);
            registeredUsers.put(userName,handler);
            usersInformation.put(userName,info);
            ConcurrentLinkedDeque<String> usersMessages = new ConcurrentLinkedDeque<>();
            usersAwaitingMessages.put(userName, usersMessages);
            return true;
        }
    }


    /**
     * add conId and username to loggedUsers
     * execute AFK messages
     * @param conId - unique connectionid
     * @param username - unique unsername
     * @return
     */
//    public boolean logIn(Integer conId,String username){
//        loggedOnUsers.put(conId,username);
//        executeAFKMessages(getUserInformation(username));
//        return true;
//    }
    public ConcurrentLinkedDeque<String> logIn(Integer conId,String username){
        loggedOnUsers.put(conId,username);
        ConcurrentLinkedDeque<String> userAwaitingMessages = usersAwaitingMessages.get(username);
        userAwaitingMessages.remove(username);
        return userAwaitingMessages;
    }

    /**
     * request logoud
     * if username isn't registered or isn't logged in return false
     * else remove it from loggedOnUsers and return true
     *
     * @param conId conId to logoud
     * @return true if loggedout false otherwise
     */
    public boolean logOut(Integer conId) {
        try {
            registeredUsers.get(conId);
            loggedOnUsers.get(conId);
        }
        catch (NullPointerException e){
            return false;
        }

        disconnect(conId);
        return true;
    }


    public ConcurrentHashMap<String, BGSClientInformation> getUsersInformation() {
        return usersInformation;
    }


    /**
     * 2 ways to get userInformation
     * */
    public BGSClientInformation getUserInformation(String userName) {
        System.out.println("get "+ userName+" Information in BGSConnections");
        if (usersInformation.keySet().contains(userName)){
            System.out.println("usersInformation contains "+userName);
            return usersInformation.get(userName);
        }
        return null;
    }

    public int userNameToConId(String userName){
        for (int checkedId : loggedOnUsers.keySet())
            if (loggedOnUsers.contains(checkedId)){
                if (loggedOnUsers.get(checkedId).equals(userName)) {
                    return checkedId;
                }
            }
        return -1;
    }

    public ConcurrentHashMap<Integer, String> getLoggedOnUsers() {
        return loggedOnUsers;
    }

    public boolean isLogged(String userName){
        try{
            if ( loggedOnUsers.contains(userName)){
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            return false;
        }

    }


    public ConcurrentHashMap<String, ConnectionHandler<String>> getRegisteredUsers() {
        return registeredUsers;
    }

    /**
     * receives the name of the conId handler if logged on, or null if isn't logged on or doesn't exist
     *
     * @param conId connection ID to get username
     * @return username of conId or null if isn't logged on
     */
    public String conIdToUsername(int conId) throws NullPointerException {
        String s = loggedOnUsers.get(conId);
        return s;
    }

    /**
     * send an AFK message "msg" to "userName".
     * AFK messages (Away From the Keyboard messages) are recieved by logged out users.
     * if we want to send an AFK message, we should make sure the user has an awaiting
     * message (atleast one) and therefore his Dequeue of messages in usersAwaitingMessages
     * shouldn't return NullPointerException, meaning that if a NULL PTR EX occurred,
     * we were about to send an AFK message but the user has just logged in and activated
     * executeAFKMessages durring the time we tried sending. Therefore the function will
     * send normally the message if null occurred
     *
     * @param userName - the handler to send the message to
     * @param msg - the message to send
     */
    public synchronized void sendAFKMessage(String userName, String msg){
        try{
            if (usersAwaitingMessages.keySet().contains(userName)){
                    usersAwaitingMessages.get(userName).addLast(msg);
            }
            else{
                ConcurrentLinkedDeque<String> newHandlerQueue =  new ConcurrentLinkedDeque<String>();
                newHandlerQueue.add(msg);
                usersAwaitingMessages.put(userName,newHandlerQueue);
            }
        }
        catch (NullPointerException e){}
    }

    public synchronized void executeAFKMessages(String userName){
        try{
                for (String message : usersAwaitingMessages.get(userName)){
                    registeredUsers.get(userName).send(message);
                }
                usersAwaitingMessages.remove(userName);
        }
        // if get got null PTR EX then return and do nothing because user has no awaiting messages
        catch (NullPointerException e){}
    }

    public boolean isRegistered(String userName) {
//        System.out.print("checking if userName: -");
//        for (char letter : userName.toCharArray())
//            System.out.print(letter+"-");
//        System.out.println(" is registered.");
//        System.out.println();

        try {
            if (registeredUsers.get(userName) != null) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkPassword(String userName, String password) {
        try{
            return usersInformation.get(userName).getPassword().equals(password);
        }
        catch (NullPointerException e){
            return false;
        }
    }
}
