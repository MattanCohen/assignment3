package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSClientInformation;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

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
    BGSConnections allClientsServerConnections=new BGSConnections();
    private boolean shouldTerminate = false;

    /**
     * upon receiving register, make protocol
     * @param connectionId
     * @param connections
     */
    @Override
    public void start(int connectionId, Connections<String> connections) {
        conId=connectionId;
        this.allClientsServerConnections=(BGSConnections)connections;
    }


    // if msg is "login" and user wasn't logged in (check via connections), add it to connections with conId.incrementAndGet
    // if msg is register, connections.register
    @Override
    public Object process(Object msg) {
        String [] message=((String[])msg);
        String userName="";
        String error="ERROR ";
        try{
            userName=allClientsServerConnections.conIdToUsername(conId);
        }catch (NullPointerException n){ userName=null; /* supposed to be null in login or register*/}
        switch (message[0]){
            case("LOGIN"):{}
            case("LOGOUT"):{}
            case("FOLLOW"):{}
            case ("POST"):{
                error+="POST";
                /*check for validating
                 * return error if user isnt logged in
                 * */
                if (!allClientsServerConnections.isLogged(conId))
                    return error;

                //after decoding string is beautiful so message[1:len-1] is content
                String content="";
                LinkedList<String> usersToSendTo=new LinkedList<>();
                for (int i=1; i<message.length; i++){
                    //if a word is a tag
                    if (message[i].charAt(0)=='@'){
                        //remove @ from the name and save it as string for tagged user name
                        String taggedUserName=message[i].substring(1);
                        //  CHECK IF TAGGED USERNAME IS REGISTERED
                        if (allClientsServerConnections.getRegisteredUsers().contains(taggedUserName)){

                            //  CHECK IF TAGGED USERNAME HAS BLOCKED USERNAME
                            //save the information about tagged username
                            BGSClientInformation taggedBlockedInformation =
                                    allClientsServerConnections.getUserInformation(conId);
                            //get blocked list for tagged username
                            ConcurrentLinkedDeque<String> taggedBlockedList = taggedBlockedInformation.getBlockedList();
                            //if username isn't blocked by taggedUsername add it to users to send
                            if (!taggedBlockedList.contains(userName)) {
                                usersToSendTo.add(taggedUserName);
                            }
                        }
                    }
                    // add to contant word i
                    content+=message[i];
                }

                return content;
            }
            case("PM"):{}
            case("LOGSTAT"): {
                // complete logstat
                String ans="";
                LinkedList<BGSClientInformation> clientsInfo = new LinkedList<>();

                // iterate over connectionId's of logged on users
                for(int connectId:allClientsServerConnections.getLoggedOnUsers().keySet()){
                    // get information of client
                    BGSClientInformation cInfo = allClientsServerConnections.getUserInformation(connectId);
                    // client didn't block this user so it can be added
                    if(!cInfo.getBlockedList().contains(userName)){
                        clientsInfo.add(cInfo);
                    }
                }
                // create string with details
                for(BGSClientInformation cInfo:clientsInfo){
                    ans+=createStatRow(cInfo,message[0])+" ";
                }
                // cut last char which is a space
                ans = ans.substring(0,ans.length()-1);

                return ans;
            }
            case ("STAT"):{
                String ans="";
                LinkedList<BGSClientInformation> clientsInfo = new LinkedList<>();
               for(BGSClientInformation cInfo:allClientsServerConnections.getUsersInformation().values()){
                   // client didn't block this user so it can be added
                   if(!cInfo.getBlockedList().contains(userName)){
                       clientsInfo.add(cInfo);
                   }
               }
                // create string with details
                for(BGSClientInformation cInfo:clientsInfo){
                    ans+=createStatRow(cInfo,message[0])+" ";
                }
                // cut last char which is a space
                ans = ans.substring(0,ans.length()-1);

                return ans;
            }
            case("BLOCK"):{}


        }
        return " ACK OR ERROR OR NOTIFICATION ";
    }

    public void terminate(){

        try{
            allClientsServerConnections.disconnect(conId);

        }finally {
            shouldTerminate=true;
        }

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    /**
     * extract from BGSClientInformation data and create a row with the following foramt:
     * ACK STAT/LOGSTAT-opCode age numPosts numFollows numFollowing
     * */
    public String createStatRow(BGSClientInformation cInfo, String opCodeString){
        String messageOpCode = String.valueOf(Convertor.extractOpcodeAsShortFromString(opCodeString));
        String age = String.valueOf(Tools.calculateAge(cInfo.getBirthday()));
        String numPosts = String.valueOf(cInfo.getNumOfPosts().get());
        String numFollows = String.valueOf(cInfo.getNumOfFollows().get());
        String numFollowing= String.valueOf(cInfo.getFollowingList().size());
        return "ACK "+messageOpCode+" "+age+" "+numPosts+" "+numFollows+" "+numFollowing;
    }


}


