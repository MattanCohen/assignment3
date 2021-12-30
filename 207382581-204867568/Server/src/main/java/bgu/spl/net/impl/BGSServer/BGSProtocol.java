package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessagingProtocol;

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
public class BGSProtocol implements MessagingProtocol{
    @Override
    public Object process(Object msg) {
        return null;
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
