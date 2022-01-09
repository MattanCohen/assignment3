package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.BIDI.Convertor;
import bgu.spl.net.api.BIDI.BidiMessagingProtocol;
import bgu.spl.net.api.BIDI.Connections;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSClientInformation;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
import bgu.spl.net.api.BIDI.ConnectionHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

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
        System.out.println();
//        shouldTerminate = (msg=="LOGOUT");
        String [] message=msg.split("( )|((\0)+)");
        String userName="";
        String error="ERROR ";
        System.out.println("processing for client #"+conId);
//        if (!bgsConnections.isLogged(conId)){
//            /* supposed to be null in login or register*/
//            if (message[0].equals("LOGIN")){
//                handleLogin(message, userName, error);
//                return;
//            }
//            else if (message[0].equals("REGISTER")){
//                handleRegister(message, userName, error);
//                return;
//            }
//            protocolHandler.send(error + message[0]);
//            return;
//
//        }
//        else{
            if (message[0].equals("REGISTER")){
                userName = message[1];
                handleRegister(message, userName, error);
                return;
            }
            if (message[0].equals("LOGIN")){
                userName = message[1];
                handleLogin(message, userName, error);
                return;
            }
            userName= bgsConnections.conIdToUsername(conId);
            switch (message[0]) {
                case ("LOGOUT"): {
                    handleLogout(message, userName, error);
                    break;
                }
                case ("FOLLOW"): {
                    handleFollow(message, userName, error);
                    break;
                }
                case ("POST"): {
                    handlePost(message, userName, error);
                    break;
                }
                case ("PM"): {
                    handlePM(message, userName, error);
                    break;
                }
                case ("LOGSTAT"): {
                    handleLogStat(message, userName, error);
                    break;
                }
                case ("STAT"): {
                    handleStat(message, userName, error);
                    break;
                }
                case ("BLOCK"): {
                    handleBlock(message, userName, error);
                    break;
                }

            }
        }

//    }

    /**
     *
     * @param message = [OPCODE] [USERNAME] [PASSWORD] [BIRTHDAY "DD-MM-YYYY"]
     * @param userName
     * @param error
     */
    private void handleRegister(String[] message, String userName, String error) {
        error += "REGISTER";
        // if user is already registered
        if (!bgsConnections.registerUser(conId, userName, message[2], message[3], protocolHandler)) {
            protocolHandler.send(error);
            return;
        }
        protocolHandler.send("ACK 1");
    }
    /**
     *
     * @param message - [OPCODE] [USERNAME] [CONTENT - contains time and date]
     * @param userName
     * @param error
     */
    private void handlePM(String[] message, String userName, String error) {
            error+="PM";
            // check if sender isn't logged on
            if(!bgsConnections.isLogged(userName)){
                protocolHandler.send(error);
                return;
            }

            String targetUserName = message[1];
            // if receiver user isn't registered
            if (bgsConnections.getRegisteredUsers().get(targetUserName) == null){
                protocolHandler.send(error);
                return;
            }

            if (bgsConnections.getUserInformation(targetUserName).blocked(userName)){
                protocolHandler.send("ACK 6");
                return;
            }

            String content = "";
            for (int i=2; i<message.length; i++){
                String wordToAdd = message[i];
                if (Tools.shouldFilter(wordToAdd))
                    wordToAdd = Tools.filtered();
                content += wordToAdd + " ";
            }
            content = content.substring(0,content.length()-1);
            // add date to content string and format to notification
            String contentToNotification = "NOTIFICATION 0 " + userName +" "+content;
            ConnectionHandler targetHandler = bgsConnections.getRegisteredUsers().get(targetUserName);
            System.out.println();
            if (bgsConnections.isLogged(targetUserName))
                targetHandler.send(contentToNotification);
            else{
                bgsConnections.sendAFKMessage(targetUserName, contentToNotification);
            }

            BGSClientInformation senderInfo=bgsConnections.getUserInformation(userName);

            // update numOfPosts because current user sent a message
            senderInfo.incrementNumOfPosts();
            protocolHandler.send("ACK 6");
    }

    private void handleLogout(String[] message, String userName, String error) {
        error += "LOGOUT";
        if (!bgsConnections.isRegistered(userName) || !bgsConnections.isLogged(userName)){
            protocolHandler.send(error);
            return;
        }

            // remove conId from all records
            bgsConnections.logOut(conId);
            shouldTerminate = true;
        // send ack for user
        protocolHandler.send("ACK 3");
}


    /**
     *
     * @param message = [OPCODE] [USERNAME] [PASSWORD] [CAPTCHA]
     * @param userName
     * @param error
     */
    private void handleLogin(String[] message, String userName, String error) {
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
                protocolHandler.send(error);
                return;
            }
        ConcurrentLinkedDeque<String> awaitingMessages = bgsConnections.logIn(conId,userName);
            for (String msg : awaitingMessages)
                protocolHandler.send(msg);
            protocolHandler.send("ACK 2");
    }

    /**
     * user can log in iff it is registered, has the right password, got the currect captcha and isn't logged in
     *
     * @param userName - supposed to be unique userName to check if can log in
     * @param message - the message formatted for LOGIN :  [LOGIN] [USERNAME] [PASSWORD] [CAPTCHA]
     * @return true iff user can't login
     */
    private boolean cantLogIn(String userName, String[] message) {
        boolean notRegistered = !bgsConnections.isRegistered(userName);
        boolean passwordMissmatch = !bgsConnections.checkPassword(userName, message[2]);
        boolean capchaNotOne = !message[3].equals("1");
        boolean isLoggedOn = bgsConnections.isLogged(userName);
//        System.out.println("registered? "+notRegistered);
//        System.out.println("passwordMatch? "+passwordMissmatch);
//        System.out.println("captcha 1? "+capchaNotOne);
//        System.out.println("logged on? "+isLoggedOn);

        return (notRegistered | // check if isn't registered or
                passwordMissmatch | // password don't match or
                capchaNotOne | //  if captcha isn't 1 or
                isLoggedOn); // check if user is already logged in
    }

    /**
     *
     * @param message - [OPCODE] [CONTENT - contains time and date]
     * @param userName
     * @param error
     */
    private void handlePost (String[] message, String userName, String error){
            error += "POST";/*check for validating
             * return error if user isnt logged in
             * */
            if (!bgsConnections.isLogged(userName))
                protocolHandler.send(error);

            BGSClientInformation senderInfo=bgsConnections.getUserInformation(userName);

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

            content = content.substring(0,content.length()-1);
/*            // add date to content string and format to notification
            String contentToNotification = "NOTIFICATION 0 " + userName +"_"+content;
            System.out.println();
            ConnectionHandler targetHandler = bgsConnections.getRegisteredUsers().get(targetUserName);
            if (bgsConnections.isLogged(targetUserName))
                targetHandler.send(contentToNotification);
            else{
                bgsConnections.sendAFKMessage(targetHandler,contentToNotification);
            }

            BGSClientInformation senderInfo=bgsConnections.getUserInformation(userName);
             */

            // add followers to to send
            for (String name : bgsConnections.getUsersInformation().get(userName).getFollowersList()){
                // MAKE SURE FOLLOWER ISNT TAGGED
                if (!usersToSendTo.contains(name))
                    usersToSendTo.add(name);
            }

            // format content in NOTIFICATION bulk
            String contentToNotification = "NOTIFICATION 1 " + userName + " " + content;

            //send the users followed and tagged
            for (String targetUserName : usersToSendTo){
                ConnectionHandler targetHandler = bgsConnections.getRegisteredUsers().get(targetUserName);
                if (bgsConnections.isLogged(targetUserName))
                    targetHandler.send(contentToNotification);
                else{
                    bgsConnections.sendAFKMessage(targetUserName,contentToNotification);
                }
            }
            protocolHandler.send("ACK 5");
    }

    /**
     * @param message [LOGSTAT]
     * @return for each logged on user (split with spaces):
     * ACK 7 [age] [NumPosts] [NumFollowers] [NumFollowing] ACK 7
     * */
    public void handleLogStat(String[] message, String userName, String error) {
        String ACK = "";
        error += "LOGSTAT";
        if (!bgsConnections.isLogged(userName)) {
            protocolHandler.send( error);
            return;
        }

        LinkedList<BGSClientInformation> clientsInfo = new LinkedList<>();

        // get all client information of logged in users (that user didn't block)
        for (String user : bgsConnections.getLoggedOnUsers().values()) {
            // get information of client
            BGSClientInformation cInfo = bgsConnections.getUserInformation(user);
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
        if (ACK.equals("")){
            protocolHandler.send("ACK 7");
            return;
        }
        protocolHandler.send( ACK);

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
            error+="STAT";
            if(!bgsConnections.isLogged(userName)){
                protocolHandler.send(error);
                return;
            }

            // save all the user's informations in USERS_LIST on message that aren't blocked by userName
            LinkedList<BGSClientInformation> clientsInfo = new LinkedList<>();


            // get from message the list of users seperated (split) by '|'
            String [] usersList = message[1].split("(\\|)|((\0)+)");
            // for every said user
            for (String userToStat : usersList)
                if (userToStat != null && userToStat != "" && userToStat != " "){
                    if(bgsConnections.isRegistered(userToStat)){
                    // get its information
                    BGSClientInformation toStatInfo = bgsConnections.getUserInformation(userToStat);
                    //if he didn't block current userName
                    if (!toStatInfo.blocked(userName)) {
                        // add its information to clientsInfo
                        clientsInfo.add(toStatInfo);
                    }
                }
            }

            // create string with details for each
            for (BGSClientInformation cInfo : clientsInfo) {
                ACK += createStatRow(cInfo, message[0]) + " ";
            }

        // cut last char which is a space
//            ACK = ACK.substring(0, ACK.length() - 1);
        if (ACK.equals("")){
            protocolHandler.send("ACK 8");
            return;
        }
        protocolHandler.send(ACK);
    }

    public void handleFollow(String[] message, String userName, String error){
        error+="FOLLOW";
        String targetUserName = message[2];
        // send error if user isn't logged on
        if(!bgsConnections.isLogged(userName)){
            protocolHandler.send(error);
            return;
        }

        // check if targetUserName is registered
        if (!bgsConnections.isRegistered(targetUserName)){
            protocolHandler.send(error);
            return;
        }

        BGSClientInformation targetInfo = bgsConnections.getUserInformation(targetUserName);
        if (targetInfo.blocked(userName)){
            protocolHandler.send(error);
            return;
        }

//        System.out.print("followUnfollow = -");
//        for (char letter : message[1].toCharArray()){
//            System.out.print(letter+"-");
//        }
//        System.out.println();
//        System.out.println("message[1] == \"0\" ? "+ (message[1].equals("0")));
//        System.out.println("message[1] == \'0\' ? "+ (message[1].equals('0')));
//             if follow
        if (message[1].equals("0")) {
//            System.out.println("HandleFollow targetInfo is null? "+(targetInfo==null));
            // check if user is already following target user
            if (targetInfo.isFollower(userName)) {
                protocolHandler.send( error);
                return;
            } else {
                // user is now following targetUser so add user to target user's followersList
                targetInfo.addFollow(userName);
                // user is now following a new user so increment num of follows
                BGSClientInformation cInfo = bgsConnections.getUserInformation(userName);
                cInfo.incrementNumOfFollows();
            }
        }
        // if unfollow message[1]=="1"
        else {
//            System.out.println("447 unfollow = 1");
            if (!bgsConnections.isRegistered(targetUserName)){
                protocolHandler.send(error);
                return;
            }
            BGSClientInformation cInfo = bgsConnections.getUserInformation(userName);
            // if user isnt following targetUserName send error
            if (!targetInfo.isFollower(userName)) {
                protocolHandler.send(error);
                return;
            } else {
                // make sure user doesn't follow targetUserName
                targetInfo.removeFollow(userName);
                // user is now following one less user
                cInfo.decrementNumOfFollows();
            }
        }
        protocolHandler.send("ACK 4 " + targetUserName);

    }

    /**
     *
     * @param message - [OPCODE] [USERNAME]
     * @param userName
     * @param error
     */
    private void handleBlock(String[] message, String userName, String error) {
        error += "BLOCK";

        String targetUserName = message[1];
        // check if targetUserName doesnt exist as registered user send erro
        if (!bgsConnections.isRegistered(targetUserName)) {
            // targetUserName doesn't exist
            protocolHandler.send(error);
            return;
        }

        // mutual block will occur:
        //                                 user    blocks
        bgsConnections.getUserInformation(userName).addBlocked(targetUserName);
        bgsConnections.getUserInformation(targetUserName).addBlocked(userName);

        protocolHandler.send("ACK 12");
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


