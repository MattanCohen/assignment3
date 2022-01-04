package bgu.spl.net.impl.BGSServer;

import java.util.concurrent.atomic.AtomicInteger;

public class Tools {
    private static AtomicInteger conId=new AtomicInteger(0);

    public static int getConId() {
        return conId.get();
    }

    public static int incrementAndGetConId(){
        return conId.incrementAndGet();
    }

    public static void incrementConId(){
        int f=conId.get();
        while (!conId.compareAndSet(f,f+1)) {
            f=conId.get();
        }
    }



}
