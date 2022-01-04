package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Tools;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class BGSConnections implements Connections<String> {

    // map handler to userName
    ConcurrentHashMap <Integer, ConnectionHandler<String>> registeredUsersToHandlers;
    // map logged on userNames to conId
    ConcurrentHashMap <Integer , String> loggedOnUsers;
    // map information to userName
    ConcurrentHashMap <Integer, BGSClientInformation> usersInformation;

    int messageId;
    // map messages to messages id posted by handler (pm or post)
    ConcurrentHashMap<Integer,String> messagesTime;
    // map time to messages id posted by handler (pm or post)
    ConcurrentHashMap<Integer,String> messagesId;


    /**
     * send is used by the connection handler.
     * when a connection handler wants to send a message to another client, we will have to check
     * if the other client is following the handler (that is sending).
     *
     * @param connectionId - the user IO in connections to send to
     * @param msg - the message we want to transfer (returned beautifully from protocol)
     * @return true if success in sending false otherwise
     */
    @Override
    public boolean send(int connectionId, String msg) {
        messageId= Tools.incrementAndGetMessageId();
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

    }


    /**
     * Remove connectionHandler from hashmap
     * */
    @Override
    public void disconnect(int connectionId) {
        // remove connectionId from all users that follow him
        // remove connectionId from all users that blocked him
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
        if (registeredUsersToHandlers.get(conid)!=null)
            return false;
        BGSClientInformation info=new BGSClientInformation(userName,password,birthday);
        registeredUsersToHandlers.put(conid,handler);
        usersInformation.put(conid,info);
        return true;
    }


    /**
     * request login
     * if username isn't registered
     *      or is already logged in
     *      or has wrong captcha,
     *      or has wrong password (through BGSInformation):  return false
     * else, add userName and conId to loggedOnUsers and return true
     *
     * @param username userName
     * @param password password
     * @param captcha captcha
     * @return
     */
    public boolean logIn(Integer conId,String username, String password, int captcha){
        // user doesn't exist
        if(registeredUsersToHandlers.get(conId)==null) {
            return false; }
        // user already logged in
        if (loggedOnUsers.get(conId)!=null) {
            return false;
        }
        BGSClientInformation cInfo = usersInformation.get(conId);
        // passwords don't match or capcha is 0
        if (password!=cInfo.getPassword() || captcha==0) {
            return false;
        }

       loggedOnUsers.put(conId,username);
       return true;
    }

    /**
     * request logoud
     * if username isn't registered or isn't logged in return false
     * else remove it from loggedOnUsers and return true
     * @param conId conId to logoud
     * @return true if loggedout false otherwise
     */
    public boolean logOut(Integer conId){
        if (registeredUsersToHandlers.get(conId)==null || loggedOnUsers.get(conId)==null)
            return  false;
        disconnect(conId);
        return true;
    }



}
