package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessagingProtocol;

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
