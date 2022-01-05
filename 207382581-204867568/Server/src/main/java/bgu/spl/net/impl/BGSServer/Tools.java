package bgu.spl.net.impl.BGSServer;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class Tools {
    private static AtomicInteger conId=new AtomicInteger(0);
    private static AtomicInteger messageId=new AtomicInteger(0);

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

    public static int getMessageId() {
        return messageId.get();
    }

    public static int incrementAndGetMessageId(){return  messageId.incrementAndGet();}

    public static void incrementMessageId(){
        int f=messageId.get();
        while (!messageId.compareAndSet(f,f+1))
            f=messageId.get();
    }

    /**
     * get birthday in format DD-MM-YYYY and return age
     * */
    public static int calculateAge(String birthday){
        //DD-MM-YYYY
        int birthdayYear = Integer.valueOf(birthday.split("-")[2]);
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        return currentYear-birthdayYear;
    }
}
