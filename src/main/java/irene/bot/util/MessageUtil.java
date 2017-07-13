package irene.bot.util;

import java.util.concurrent.ThreadLocalRandom;

public class MessageUtil {

    private static String[] greetings = {"darling", "sweetie", "babe", "honey", "sweetheart", "sugar", "cutie", "buddy", "sweet thing", "cherie", "honey bunny", "pumpkin"};
    private static String[] emojis = {";)", ":*", ":)", ":D"};


    public static String getRandomGreeting(){
        int randomNum = ThreadLocalRandom.current().nextInt(1, greetings.length);
        return greetings[randomNum];
    }

    public static String getRandomEmoji(){
        int randomNum = ThreadLocalRandom.current().nextInt(1, emojis.length);
        return emojis[randomNum];
    }

    public static String getErrorEmoji(){
        return ":(";
    }
}
