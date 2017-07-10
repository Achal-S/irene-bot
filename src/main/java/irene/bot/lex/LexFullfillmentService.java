package irene.bot.lex;

import com.google.gson.Gson;
import irene.bot.lex.model.*;

public class LexFullfillmentService {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LexFullfillmentService.class);
    protected static final String PLAIN_TEXT = "PlainText";


    public LexResponse lexCloseIntent(final String msg, final FullfillmentState fullfillmentState) {
        log.info("Building lex close intent response: " + msg);
        final LexResponse lexResponse = new LexResponse();
        final DialogAction dialogAction = new DialogAction();
        final Message message = new Message();

        message.setContentType(PLAIN_TEXT);
        message.setContent(msg);

        dialogAction.setFulfillmentState(fullfillmentState.toString());
        dialogAction.setType(DialogActionType.CLOSE.toString());
        dialogAction.setMessage(message);

        lexResponse.setDialogAction(dialogAction);
        return lexResponse;
    }


    public LexResponse lexConfirmIntent(final String msg, final String intentName, final Slots slots) {
        log.info("Building lex confirm intent response: " + msg);
        final LexResponse lexResponse = new LexResponse();
        final DialogAction dialogAction = new DialogAction();
        final Message message = new Message();

        message.setContentType(PLAIN_TEXT);
        message.setContent(msg);

        dialogAction.setType(DialogActionType.CONFIRMINTENT.toString());
        dialogAction.setMessage(message);
        dialogAction.setIntentName(intentName);
        dialogAction.setSlots(slots);

        lexResponse.setDialogAction(dialogAction);
        return lexResponse;
    }

    public LexResponse lexElicitIntent(final String msg) {
        log.info("Building lex elicit intent response: " + msg);
        final LexResponse lexResponse = new LexResponse();
        final DialogAction dialogAction = new DialogAction();
        final Message message = new Message();

        message.setContentType(PLAIN_TEXT);
        message.setContent(msg);

        dialogAction.setType(DialogActionType.ELICITINTENT.toString());
        dialogAction.setMessage(message);

        lexResponse.setDialogAction(dialogAction);
        return lexResponse;
    }

    public LexResponse lexDelegate(final Slots slots) {
        log.info("Building lex delegate response");
        final LexResponse lexResponse = new LexResponse();
        final DialogAction dialogAction = new DialogAction();

        dialogAction.setType(DialogActionType.DELEGATE.toString());
        dialogAction.setSlots(slots);

        lexResponse.setDialogAction(dialogAction);
        return lexResponse;
    }

    public LexResponse lexElicitSlot(final String msg, final String slotToElicit, final Slots slots, final String intentName) {
        log.info("Building lex elicit response with message: " + msg);
        final LexResponse lexResponse = new LexResponse();
        final DialogAction dialogAction = new DialogAction();
        final Message message = new Message();

        message.setContentType(PLAIN_TEXT);
        message.setContent(msg);

        dialogAction.setIntentName(intentName);
        dialogAction.setType(DialogActionType.ELICITSLOT.toString());
        dialogAction.setMessage(message);
        dialogAction.setSlotToElicit(slotToElicit);
        dialogAction.setSlots(slots);

        lexResponse.setDialogAction(dialogAction);

        return lexResponse;
    }

    public void prettyLogLexResponse(LexResponse lexResponse){
        Gson gson = new Gson();
        log.info(gson.toJson(lexResponse));
    }
}
