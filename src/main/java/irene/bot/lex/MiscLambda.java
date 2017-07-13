package irene.bot.lex;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.lex.model.FullfillmentState;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.lex.model.MiscIntentType;
import irene.bot.util.MessageUtil;

import static irene.bot.util.MessageUtil.getRandomEmoji;
import static irene.bot.util.MessageUtil.getRandomGreeting;

public class MiscLambda implements RequestHandler<LexEvent, LexResponse> {

    private final LexFullfillmentService lexFullfillmentService = new LexFullfillmentService();

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        MiscIntentType miscIntentType = MiscIntentType.fromString(lexEvent.getCurrentIntent().getName());
        String msg = "Sorry, I don't understand that.";

        switch (miscIntentType) {
            case STOP:
                msg = "Ok, talk to you later " + MessageUtil.getRandomGreeting() + " " + getRandomEmoji();
                break;
            case CANCEL:
                msg = "Ok, talk to you later " + MessageUtil.getRandomGreeting() + " " + getRandomEmoji();
                break;
            case GREETING:
                msg = "Hello " + MessageUtil.getRandomGreeting() +" "+ getRandomEmoji();
                break;
            case HOWAREYOU:
                msg = "I am great " + MessageUtil.getRandomGreeting() + "! Let's go for a ride... " + getRandomEmoji();
                break;
            case NAME:
                msg = "My name is Irene. Nice to meet you " + MessageUtil.getRandomGreeting() + " " + getRandomEmoji();
                break;
            case REAL:
                msg = "I am as real as the Cloud " + MessageUtil.getRandomGreeting() + " " + getRandomEmoji();
                break;
            case GENERIC:
                msg = "How can I help you, " + MessageUtil.getRandomGreeting() + "?";
                break;
            case BYE:
                msg = "Bye bye " + MessageUtil.getRandomGreeting() + " " + getRandomEmoji();
                break;
            case LOVE:
                msg = "I love you! You are the only one that can turn me on " + getRandomEmoji();
                break;
            case CAPABILITIES:
            case HELP:
                msg = "Well, I can help you in many ways.\n" +
                        "First you can ask me to localize the motorbike (e.g., \"where are you?\") or basic\n" +
                        "sensor readings like temperature, pressure and humidity (e.g., \"what is the temperature outside?\").\n" +
                        "I can set an alarm on the motorbike which will alert you if the bike is touched or moved (e.g., \"Please set the alarm\").\n" +
                        "Furthermore, I provide a safety mode (e.g., \"Please set the safety mode\") in which I can alert someone with a text in case of road accident.\n" +
                        "Finally, I can help you troubleshoot the mechanical problems you may have on the motorbike (e.g., \"I have a problem, can you help me?\") "+ getRandomEmoji();
                break;
        }
        return lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
    }
}
