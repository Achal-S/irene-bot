package irene.bot.lex;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.lex.model.FullfillmentState;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.lex.model.MiscIntentType;

public class MiscLambda implements RequestHandler<LexEvent, LexResponse> {

    protected final LexFullfillmentService lexFullfillmentService = new LexFullfillmentService();

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        MiscIntentType miscIntentType = MiscIntentType.fromString(lexEvent.getCurrentIntent().getName());
        String msg = "Sorry, I don't understand that";

        switch(miscIntentType){
            case STOP:
                msg = "Ok, talk to you later darling.";
                break;
            case CANCEL:
                msg ="Ok, talk to you later darling.";
                break;
            case GREETING:
                msg ="Hello darling, I missed you!";
                break;
            case HELP:
                msg = "I am Irene - the motorbike chatbot - and I can provide to you several services.\n" +
                        "First of all you can ask me the position of the motorbike (\"where are you?\") or basic\n" +
                        "sensor readings like temperature, pressure and humidity.\n" +
                        "In addition, I can set an alarm on the motorbike which will alert you if the bike is touched or moved.\n" +
                        "Furthermore, I provide a safety mode in which I can alert with a text a mobile number you indicate in case of incident during a trip.\n" +
                        "Finally, being an expert, I can help you troubleshoot the mechanical problem you may have on the motorbike.\n" +
                        "Enjoy!";
                break;
        }
        return lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
    }
}
