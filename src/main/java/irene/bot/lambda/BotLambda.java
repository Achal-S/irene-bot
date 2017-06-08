package irene.bot.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.model.DialogAction;
import irene.bot.model.LexEvent;
import irene.bot.model.LexResponse;
import irene.bot.model.Message;

public class BotLambda implements RequestHandler<LexEvent, LexResponse> {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BotLambda.class);


    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        String msg = lexEvent.getCurrentIntent().getName()+" "+ lexEvent.getCurrentIntent().getConfirmationStatus();
        LexResponse lexResponse = new LexResponse();
        DialogAction dialogAction = new DialogAction();
        dialogAction.setFulfillmentState("Fulfilled");
        dialogAction.setType("Close");
        lexResponse.setDialogAction(dialogAction);
        Message message = new Message();
        message.setContentType("PlainText");
        message.setContent(msg);
        dialogAction.setMessage(message);
        return lexResponse;
    }
}
