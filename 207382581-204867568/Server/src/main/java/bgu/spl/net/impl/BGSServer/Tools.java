package bgu.spl.net.impl.BGSServer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Tools {
    private static AtomicInteger conId=new AtomicInteger(0);
    private static AtomicInteger messageId=new AtomicInteger(0);

    private static LinkedList<String> filteredWords= new LinkedList<>(
            Arrays.asList(
                    "gay", "douch", "trump", "lol",
                    "mom", "beyonce", "nigger", "richard",
                    "israel", "love", "pinnacolada"
            )
    );



    private static boolean created=false;
    private static HashMap<String,Byte> filteredWordsMap=new HashMap<>();

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

    /**
     *
     * @return filtered words: list of strings
     */
    public synchronized static HashMap<String,Byte> FilteredWords(){
        if (!created){
            for (String filteredWord : filteredWords){
                // add all strings in filteredWords but make sure its lowered case
                filteredWordsMap.put(filteredWord.toLowerCase(),(byte) 1);
            }
            created=true;
        }
        return filteredWordsMap;
    }

    public static boolean shouldFilter(String toCheck) {
        // incase filteredWords wasn't created, create it
        FilteredWords();
        // lowerCase toCheck
        toCheck = toCheck.toLowerCase();
        // return true <---> filteredWords contains toCheck <---> get isn't null
        try{
            filteredWordsMap.get(toCheck);
            return true;
        }
        // NullPointerException was thrown <---> get is null <---> filteredWords doesn't contain toCheck
        catch (NullPointerException e){
            return false;
        }
    }

    public static String filtered(){ return "<filtered>";}

}


//
//        //get filtered words
//        LinkedList<String> filteredWords=FilteredWords();
//        //for every filtered word
//        for (String filteredWord : filteredWords){
//            //if toCheck's length is the same as a filtered word
//            if (filteredWord.length()==toCheck.length()){
//                boolean wordShouldFilter = true;
//                //check if every char in toCheck and filteredWord is the same in not capital form
//                for (int i=0; i<filteredWord.length(); i++){
//                    // change wordShouldFilter to false   <--->  one letter in both words is different
//                    if ( Character.toUpperCase(filteredWord.charAt(i)) != Character.toUpperCase(toCheck.charAt(i)) ){
//                        wordShouldFilter=false;
//                        break;
//                    }
//                }
//                // if wordShouldFilter is true then all letters in both words are the same and word should be filtered
//                if (wordShouldFilter)
//                    return true;
//            }
//        }
//        return false;
//    }
