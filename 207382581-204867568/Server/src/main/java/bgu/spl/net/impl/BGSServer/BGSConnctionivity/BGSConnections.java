package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Tools;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import com.sun.security.ntlm.Client;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BGSConnections implements Connections<String> {

    // map handler to userName
    ConcurrentHashMap <String, ConnectionHandler<String>> registeredUsers;

    // map logged on userNames to conId
    ConcurrentHashMap <Integer , String> loggedOnUsers;

    // map information to userName
    ConcurrentHashMap <String, BGSClientInformation> usersInformation;

    //map the messages sent while afk to username
    ConcurrentHashMap <String, ConcurrentLinkedDeque<String>> usersAwaitingMessages;



    // map time to messages posted by handler (pm or post)
    ConcurrentHashMap<String,String> messagesId;


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
        return false;
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
        try{
            registeredUsers.get(userName);
            BGSClientInformation info=new BGSClientInformation(userName,password,birthday);
            registeredUsers.put(userName,handler);
            usersInformation.put(userName,info);
            return true;
        }
        catch (NullPointerException e){
            return false;
        }
    }


    /**
     * add conId and username to loggedUsers
     * execute AFK messages
     * @param conId - unique connectionid
     * @param username - unique unsername
     * @return
     */
    public boolean logIn(Integer conId, String username){
       loggedOnUsers.put(conId,username);
       executeAFKMessages(username);
       return true;
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
    public BGSClientInformation getUserInformation(Integer conId) {
        BGSClientInformation b=new BGSClientInformation("","","");
        try{
            b=usersInformation.get(conId);
        } catch (Exception e){System.out.println("Exception occurred in getUserInformation in BGSConnections "+e.getMessage());}
        finally {
            return b;
        }
    }

    public int userNameToConId(String userName){
        try{
            for (int checkedId : loggedOnUsers.keySet())
                if (loggedOnUsers.get(checkedId) == userName) {
                    return checkedId;
                }
        }
        catch (NullPointerException e){}
        /*System.out.println("Exception occurred in userNameToConId in BGSConnections "+e.getMessage());*/
        return -1;
    }

    public BGSClientInformation getUserInformation(String userName) {
        for(BGSClientInformation cInfo: usersInformation.values()){
            if(cInfo.getUserName()==userName) {
                return cInfo;
            }
        }

        System.out.println("UserName has not been found in getUserInformation in BGSConnections");
        return null;
    }

    public ConcurrentHashMap<Integer, String> getLoggedOnUsers() {
        return loggedOnUsers;
    }

    public boolean isLogged(Integer conId){
        try{
            loggedOnUsers.get(conId);
            return true;
        } catch (Exception e){return false;}
    }

    public boolean isLogged(String userName){
        int conId = userNameToConId(userName);
        return isLogged(conId);
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
     * @param userName - the userName to send the message to
     * @param msg - the message to send
     */
    public void sendAFKMessage(String userName, String msg){
        try{
            synchronized (usersAwaitingMessages.get(userName)){
                usersAwaitingMessages.get(userName).addLast(msg);
            }
        }
        // if got null PTR EX then userName's awaiting messages is removed aka finished aka he logged in
        catch (NullPointerException e){
            registeredUsers.get(userName).send(msg);
        }
    }

    public void executeAFKMessages(String userName){
        try{
            // catch userName's awaiting messages's lock
            synchronized (usersAwaitingMessages.get(userName)){
                // while user's messages list isn't empty
                while (!usersAwaitingMessages.get(userName).isEmpty()){
                    // send the user the message awaiting
                    registeredUsers.get(userName).send(usersAwaitingMessages.get(userName).pollFirst());
                }
                // remove userName from usersAwaitingMessages
                usersAwaitingMessages.remove(userName);
            }
        }
        // if get got null PTR EX then return and do nothing because user has no awaiting messages
        catch (NullPointerException e){}
    }

    public boolean isRegistered(String userName) {
        try{
            registeredUsers.get(userName);
            return true;
        }
        catch (NullPointerException e){
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
