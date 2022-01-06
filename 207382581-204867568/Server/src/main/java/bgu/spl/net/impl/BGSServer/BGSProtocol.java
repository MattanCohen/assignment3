package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSClientInformation;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.LinkedList;

/*
Opcode Operation
1 Register request (REGISTER)
2 Login request (LOGIN)
3 Logout request (LOGOUT)
4 Follow / Unfollow request (FOLLOW)
5 Post request (POST)
6 PM request (PM)
7 Logged in States request (LOGSTAT)
8 Stats request (STAT)
9 Notification (NOTIFICATION)
10 Ack (ACK)
11 Error (ERROR)
12 Block (BLOCK)
 */
public class BGSProtocol implements BidiMessagingProtocol<String> {

    int conId=0;
    BGSConnections bgsConnections =new BGSConnections();
    private boolean shouldTerminate = false;
    private ConnectionHandler protocolHandler;



    /**
     * upon receiving register, make protocol
     * @param connectionId
     * @param connections
     */
    @Override
    public void start(int connectionId, Connections<String> connections) {
        conId = connectionId;
        this.bgsConnections = (BGSConnections)connections;
    }

    public void addHandler(ConnectionHandler handler){
        this.protocolHandler = handler;
    }



    // if msg is "login" and user wasn't logged in (check via connections), add it to connections with conId.incrementAndGet
    // if msg is register, connections.register
    @Override
    public void process(String msg) {

//        shouldTerminate = (msg=="LOGOUT");
        String [] message=msg.split(" ");
        String userName="";
        String error="ERROR ";
        try{
            userName= bgsConnections.conIdToUsername(conId);
        }catch (NullPointerException n){
            /* supposed to be null in login or register*/
            userName=null;
            if (message[0] == "LOGIN"){
                handleLogin(message, userName, error);
            }
            if (message[0] == "REGISTER"){
                handleRegister(message, userName, error);
            }
        }
        switch (message[0]) {
            case ("LOGOUT"): {
                handleLogout(message, userName, error);
            }
            case ("FOLLOW"): {
                handleFollow(message, userName, error);
            }
            case ("POST"): {
                handlePost(message, userName, error);
            }
            case ("PM"): {
                handlePM(message, userName, error);
            }
            case ("LOGSTAT"): {
                handleLogStat(message, userName, error);
            }
            case ("STAT"): {
                handleStat(message, userName, error);
            }
            case ("BLOCK"): {
                handleBlock(message, userName, error);
            }
            // user already logged in, commands need to send error
            case ("LOGIN"): {
                bgsConnections.send(conId, error + "LOGIN");

            }
            case ("REGISTER"): {
                bgsConnections.send(conId, error + "REGISTER");
            }

        }
    }

    /**
     *
     * @param message = [OPCODE] [USERNAME] [PASSWORD] [BIRTHDAY "DD-MM-YYYY"]
     * @param userName
     * @param error
     */
    private void handleRegister(String[] message, String userName, String error) {
        error += "REGISTER";
        try{
            // if user is already registered
            if (!bgsConnections.registerUser(conId,userName,message[2],message[3],protocolHandler)){
                bgsConnections.send(conId,error);
                return;
            }
        }finally {
            bgsConnections.send(conId,"ACK 1");
        }
    }

    /**
     *
     * @param message - [OPCODE] [USERNAME] [CONTENT - contains time and date]
     * @param userName
     * @param error
     */
    private void handlePM(String[] message, String userName, String error) {
        try{
            error+="PM";
            // check if sender isn't logged on
            if(!bgsConnections.isLogged(conId)){
                bgsConnections.send(conId,error);
                return;
            }

            String targetUserName = message[1];
            // if receiver user isn't registered
            try{
                bgsConnections.getRegisteredUsers().get(targetUserName);
            }
            catch(NullPointerException e) {
                bgsConnections.send(conId, error);
                return;
            }

            String content = "";
            for (int i=2; i<message.length; i++){
                String wordToAdd = message[i];
                if (Tools.shouldFilter(wordToAdd))
                    wordToAdd = Tools.filtered();
                content += wordToAdd + " ";
            }
            // add date to content string and format to notification
            String contentToNotification = "NOTIFICATION 0 " + userName +" "+ message[2]+"_"+content;
            if (bgsConnections.isLogged(targetUserName))
                bgsConnections.getRegisteredUsers().get(targetUserName).send(contentToNotification);
            else{
                bgsConnections.sendAFKMessage(targetUserName,contentToNotification);
            }

            BGSClientInformation senderInfo=bgsConnections.getUserInformation(conId);

            // update global numOfPosts because current user sent a message
            senderInfo.incrementNumOfPosts();
        }
        finally {
            bgsConnections.send(conId,"ACK 6");
        }
    }

    private void handleLogout(String[] message, String userName, String error) {
        try{
            error += "LOGOUT";
            // if user isn't logged on
            if(!bgsConnections.isLogged(conId)){
                bgsConnections.send(conId,error);
                return;
            }
                // remove conId from all records
                bgsConnections.logOut(conId);
                shouldTerminate = true;
        }
        finally {
            // send ack for user
            bgsConnections.send(conId,"ACK 3");
        }
    }


    /**
     *
     * @param message = [OPCODE] [USERNAME] [PASSWORD] [CAPTCHA]
     * @param userName
     * @param error
     */
    private void handleLogin(String[] message, String userName, String error) {
        try{

            error += "LOGIN";
        /*
        -send error if one of the following :
                - user isn't registered
                - password doesn't match
                - captcha != 0
                - username is logged in
        -else : call log in in connections to
                - add conId to loggedIn users in connections
                - receive messages while afk (notifications)
        -send ACK
         */
            if (cantLogIn(userName,message)){
                bgsConnections.send(conId,error);
                return;
            }
            bgsConnections.logIn(conId,userName);
        }
        finally {
            bgsConnections.send(conId,"ACK 2");
        }
    }

    /**
     * user can log in iff it is registered, has the right password, got the currect captcha and isn't logged in
     *
     * @param userName - supposed to be unique userName to check if can log in
     * @param message - the message formatted for LOGIN :  [LOGIN] [USERNAME] [PASSWORD] [CAPTCHA]
     * @return true iff user can't login
     */
    private boolean cantLogIn(String userName, String[] message) {
        return (!bgsConnections.isRegistered(userName) | // check if isn't registered or
                !bgsConnections.checkPassword(userName, message[2]) | // password don't match or
                message[3] != "1" | //  if captcha isn't 1 or
                bgsConnections.isLogged(userName)); // check if user is already logged in
    }

    /**
     *
     * @param message - [OPCODE] [CONTENT - contains time and date]
     * @param userName
     * @param error
     */
    private void handlePost (String[] message, String userName, String error){
        try{
            error += "POST";/*check for validating
             * return error if user isnt logged in
             * */
            if (!bgsConnections.isLogged(conId))
                bgsConnections.send(conId,error);

            BGSClientInformation senderInfo=bgsConnections.getUserInformation(conId);

            // update global numOfPosts because current user sent a message
            senderInfo.incrementNumOfPosts();

            //after decoding string is beautiful so message[1:len-1] is content
            String content="";

            // this list will hold all users to send the message (tagged or followed)
            LinkedList<String> usersToSendTo=new LinkedList<>();

        /*
        OVERALL FOR THE WHOLE MESSAGE :  - for every word in the message:
                                                 - if the word is a tag:
                                                          - if the tagged userName is registered AND didn't block sender:
                                                                    -remember to send tagged userName the message after
                                                 -if the word should be filtered:
                                                          - replace message [i] to be the representation of filtered word (Tools.filtered())
                                         - send the content to those who are tagged in the message and those who follow the sender
        */

            // add tagged users to list to send
            for (int i=1; i<message.length; i++){
                String toAdd = message[i];
                //if a word is a tag
                if (toAdd.charAt(0) == '@'){
                    //remove @ from the name and save it as string for tagged user name
                    String taggedUserName = toAdd.substring(1);

                    //  CHECK IF TAGGED USERNAME IS REGISTERED
                    if (bgsConnections.isRegistered(taggedUserName)){
                        //  CHECK IF TAGGED USERNAME AND USERNAME HAS BLOCKED EACH OTHER
                        //if username didn't block taggedUsername
                        if (!senderInfo.blocked(taggedUserName)) {
                            //add it to users to send
                            usersToSendTo.add(taggedUserName);
                        }
                    }
                }
                // check if message should be filtered
                if (Tools.shouldFilter(toAdd))
                    toAdd = Tools.filtered();
                // anyways add to content the word
                content += toAdd + " ";
            }

            // add followers to to send
            for (String name : bgsConnections.getUsersInformation().get(userName).getFollowersList()){
                // MAKE SURE FOLLOWER ISNT TAGGED
                if (!usersToSendTo.contains(name))
                    usersToSendTo.add(name);
            }

            // format content in NOTIFICATION bulk
            String contentToNotification = "NOTIFICATION 1 " + userName + " " + content;

            //send the users followed and tagged
            for (String name : usersToSendTo){
                // if the user is logged in send the message
                if (bgsConnections.isLogged(name)) {
                    bgsConnections.getRegisteredUsers().get(name).send(contentToNotification);
                }
                else {
                    bgsConnections.sendAFKMessage(name,contentToNotification);
                    }
            }
        }
        finally {
            bgsConnections.send(conId,"ACK 5");
        }
    }

    /**
     * @param message [LOGSTAT]
     * @return for each logged on user (split with spaces):
     * ACK 7 [age] [NumPosts] [NumFollowers] [NumFollowing] ACK 7
     * */
    public void handleLogStat(String[] message, String userName, String error) {
        String ACK = "";
        try {
            error += "LOGSTAT";
            if (!bgsConnections.isLogged(conId)) {
                bgsConnections.send(conId, error);
                return;
            }

            LinkedList<BGSClientInformation> clientsInfo = new LinkedList<>();

            // get all client information of logged in users (that user didn't block)
            for (int connectId : bgsConnections.getLoggedOnUsers().keySet()) {
                // get information of client
                BGSClientInformation cInfo = bgsConnections.getUserInformation(connectId);
                // if client didn't block user add it
                if (!cInfo.blocked(userName)) {
                    clientsInfo.add(cInfo);
                }
            }
            // create string with details
            for (BGSClientInformation cInfo : clientsInfo) {
                //                          message[1] = "LOGSTAT"
                ACK += createStatRow(cInfo, message[0]) + " ";
            }
        } finally {
            // cut last char which is a space
//            ACK = ACK.substring(0, ACK.length() - 1);

            bgsConnections.send(conId, ACK);
        }
    }

    /**
     *
     * @param message - [STAT] [USERS_LIST] usersList seperated by the unique character '|'.
     * @param userName
     * @param error
     *
     * set ACK = ""
     * set usersList = USERS_LIST - {blocked/}
     * for each userToStat in usersList :
     *                             - ACK += "ACK" + '8' + userToStat_age + userToStat_numOfPosts + userToStat_NumOfFollowers
     *
     * @post send ACK
     */
    public void handleStat(String[] message, String userName, String error){
        String ACK = "";
        try{
            error+="STAT";
            if(!bgsConnections.isLogged(conId)){
                bgsConnections.send(conId,error);
                return;
            }

            // save all the user's informations in USERS_LIST on message that aren't blocked by userName
            LinkedList<BGSClientInformation> clientsInfo = new LinkedList<>();
            // get from message the list of users seperated (split) by '|'
            String [] usersList = message[1].split("|");
            // for every said user
            for (String userToStat : usersList){
                if(bgsConnections.isRegistered(userToStat)){
                    // get its information
                    BGSClientInformation toStatInfo = bgsConnections.getUserInformation(userToStat);
                    //if he didn't block userName
                    if (!toStatInfo.blocked(userName))
                        // add its information to clientsInfo
                        clientsInfo.add(toStatInfo);
                }
            }

            // create string with details for each
            for (BGSClientInformation cInfo : clientsInfo) {
                ACK += createStatRow(cInfo, message[0]) + " ";
            }
        }
        finally {
            // cut last char which is a space
//            ACK = ACK.substring(0, ACK.length() - 1);
            bgsConnections.send(conId, ACK);
        }
    }

    public void handleFollow(String[] message, String userName, String error){
        error+="FOLLOW";
        String targetUserName = message[2];
        try {
            // send error if user isn't logged on
            if(!bgsConnections.isLogged(conId)){
                bgsConnections.send(conId,error);
                return;
            }
            // check if targetUserName is registered
            if (!bgsConnections.isRegistered(targetUserName)){
                bgsConnections.send(conId, error);
                return;
            }

//             if follow
            if (message[1] == "0") {
                BGSClientInformation targetInfo = bgsConnections.getUserInformation(targetUserName);
                // check if user is already following target user
                if (targetInfo.follows(userName)) {
                    bgsConnections.send(conId, error);
                    return;
                } else {
                    // user is now following targetUser so add user to target user's followersList
                    targetInfo.addFollow(userName);
                    // user is now following a new user so increment num of follows
                    bgsConnections.getUserInformation(conId).incrementNumOfFollows();
                }
            }
            // if unfollow message[1]=="1"
            else {
                BGSClientInformation cInfo = bgsConnections.getUserInformation(conId);
                // if user isnt following targetUserName send error
                if (!cInfo.follows(targetUserName)) {
                    bgsConnections.send(conId, error);
                    return;
                } else {
                    // make sure user doesn't follow targetUserName
                    bgsConnections.getUserInformation(conId).removeFollow(targetUserName);
                    // user is now following one less user
                    cInfo.decrementNumOfFollows();
                }
            }
        }
        finally {
            // send fitting ack
            bgsConnections.send(conId, "ACK 4 " + targetUserName);
        }
    }

    /**
     *
     * @param message - [OPCODE] [USERNAME]
     * @param userName
     * @param error
     */
    private void handleBlock(String[] message, String userName, String error) {
        error += "BLOCK";

        try {
            String targetUserName = message[1];
            // check if targetUserName doesnt exists as registered user send erro
            if (bgsConnections.isRegistered(targetUserName)) {
                // targetUserName doesn't exist
                bgsConnections.send(conId, error);
            }

            // mutual block will occur:
            //                                 user    blocks
            bgsConnections.getUserInformation(conId).addBlocked(targetUserName);
            bgsConnections.getUserInformation(targetUserName).addBlocked(userName);

        }finally {
            bgsConnections.send(conId, "ACK 12");
        }
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    /**
     * extract from BGSClientInformation data and create a row with the following foramt:
     * ACK STAT/LOGSTAT-opCode age numPosts numFollows numFollowing
     *
     * @param cInfo - userToStat_BGSClientInformation
     * @param opCodeString - "7" (logStat) or "8" (stat)
     * @return "ACK" + '8' + cInfo_age + cInfo_numOfPosts + cInfo_NumOfFollowers
     */
    public String createStatRow(BGSClientInformation cInfo, String opCodeString) {
        String messageOpCode = String.valueOf(Convertor.extractOpcodeAsShortFromString(opCodeString));
        String age = String.valueOf(Tools.calculateAge(cInfo.getBirthday()));
        String numPosts = String.valueOf(cInfo.getNumOfPosts().get());
        String numFollows = String.valueOf(cInfo.getNumOfFollows().get());
        String numFollowing = String.valueOf(cInfo.getFollowersList().size());
        return "ACK " + messageOpCode + " " + age + " " + numPosts + " " + numFollows + " " + numFollowing;
    }


}


