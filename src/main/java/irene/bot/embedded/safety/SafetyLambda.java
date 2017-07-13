package irene.bot.embedded.safety;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.model.Status;
import irene.bot.lex.model.*;
import irene.bot.util.ApplicationPropertiesUtil;
import irene.bot.util.MessageUtil;

import java.io.IOException;

public class SafetyLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SafetyLambda.class);
    private static final String SAFETY_PATH = "safety";
    private static final String SAFETY_CALLBACK_PROPERTY = "safety.notification.callback";
    private final String SAFETY_CALLBACK_URL = ApplicationPropertiesUtil.getProperty(SAFETY_CALLBACK_PROPERTY, this.getClass());
    private static final String MANAGE_SAFETY_STATUS = "ManageSafetyStatus";

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        LexResponse lexResponse;
        log.info("Current mobile is: " + lexEvent.getCurrentIntent().getSlots().getMobile());
        try {
            lexResponse = this.handleFullfillment(lexEvent);
        } catch (Exception e) {
            log.error(e);
            String msg = String.format("Safety mode has not been set. Error is: " + e.getMessage());
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;

    }

//    private LexResponse handleDialogHook(final LexEvent lexEvent) throws IOException {
//        log.info("Dialog hook handling");
//        if(lexEvent.getCurrentIntent().getConfirmationStatus().equals(ConfirmationStatus.CONFIRMED && && getSafetyModeStatus().isEnabled()){
//            String mobile = lexEvent.getCurrentIntent().getSlots().getMobile();
//            if(isMobileValid(cleanMobileNumber(mobile))) {
//
//            }else{
//                String msg = "Sorry number format seems not invalid. Please, insert a mobile number with international prefix without plus or dash (e.g., 013282073345) to send text notifications";
//                return lexFullfillmentService.lexElicitSlot(msg, "mobile", lexEvent.getCurrentIntent().getSlots(), MANAGE_SAFETY_STATUS);
//            }
//        }
//        return lexFullfillmentService.lexDelegate(lexEvent.getCurrentIntent().getSlots());
//    }


    private LexResponse handleFullfillment(final LexEvent lexEvent) throws IOException {
        log.info("Fullfillment handling");
        ConfirmationStatus confirmationStatus = ConfirmationStatus.fromString(lexEvent.getCurrentIntent().getConfirmationStatus());
        String msg;
        LexResponse lexResponse;
        switch (confirmationStatus) {
            case NONE:
                if (lexEvent.getCurrentIntent().getSlots().getMobile() == null && getSafetyModeStatus().isEnabled()) {
                    lexResponse = this.retrieveMobileSlot(lexEvent);
                } else {
                    lexResponse = this.processConfirmationStatusNone(lexEvent);
                }
                break;
            case CONFIRMED:
                lexResponse = this.processConfirmationStatusConfirmed(lexEvent);
                break;
            case DENIED:
                lexResponse = this.processConfirmationStatusDenied();
                break;
            default:
                msg = String.format("Sorry there has been a problem. Safety mode has not been set/unset.");
                lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;
    }

    private LexResponse retrieveMobileSlot(LexEvent lexEvent) throws IOException {
        String msg = "Please, " + MessageUtil.getRandomGreeting() + " insert a mobile number with international prefix without plus or dash (e.g., 013282073345) to send text notifications";
        return lexFullfillmentService.lexElicitSlot(msg, "mobile", lexEvent.getCurrentIntent().getSlots(), MANAGE_SAFETY_STATUS);
    }

    private LexResponse processConfirmationStatusNone(LexEvent lexEvent) throws IOException {
        String mobile = lexEvent.getCurrentIntent().getSlots().getMobile();
        if (isMobileValid(cleanMobileNumber(mobile))) {
            final Status status = getSafetyModeStatus();
            String msg;
            if (status.isEnabled()) {
                msg = "Safety mode is enabled, do you want to turn it off?";
            } else {
                msg = "Safety mode is disabled, do you want to turn it on?";
            }
            Slots slots = new Slots();
            slots.setMobile(cleanMobileNumber(mobile));
            return lexFullfillmentService.lexConfirmIntent(msg, MANAGE_SAFETY_STATUS, slots);
        } else {
            String msg = "Sorry " + MessageUtil.getRandomGreeting() + ", number format seems not invalid. Please, insert a mobile number with international prefix without plus or dash (e.g., 013282073345) to send text notifications";
            return lexFullfillmentService.lexElicitSlot(msg, "mobile", lexEvent.getCurrentIntent().getSlots(), MANAGE_SAFETY_STATUS);
        }
    }

    private LexResponse processConfirmationStatusConfirmed(LexEvent lexEvent) throws IOException {
        final Status status = getSafetyModeStatus();
        LexResponse lexResponse;
        if (status.isEnabled()) {
            lexResponse = this.turnSafetyModeOff(lexEvent);
        } else {
            lexResponse = this.turnSafetyModeOn(lexEvent);
        }
        return lexResponse;

    }

    private LexResponse processConfirmationStatusDenied() throws IOException {
        LexResponse lexResponse = lexFullfillmentService.lexCloseIntent("Ok " + MessageUtil.getRandomGreeting() + ". Talk to you later", FullfillmentState.FULFILLED);
        return lexResponse;

    }

    private LexResponse turnSafetyModeOn(LexEvent lexEvent) throws IOException {
        final Status status = buildStatusObject(lexEvent.getSessionAttributes(), lexEvent.getCurrentIntent().getSlots().getMobile());
        status.setEnabled(true);
        String msg;
        LexResponse lexResponse;

        final Status receivedStatus = sendSafetyModeStatus(status);
        log.info("Retrieved status for safety: " + receivedStatus);
        if (receivedStatus.isEnabled()) {
            msg = "Safety mode has been set successfully " + MessageUtil.getRandomEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
        } else {
            msg = "Sorry, some error occurred. Safety mode has not been set " + MessageUtil.getErrorEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;
    }


    private LexResponse turnSafetyModeOff(LexEvent lexEvent) throws IOException {
        final Status status = buildStatusObject(lexEvent.getSessionAttributes(), lexEvent.getCurrentIntent().getSlots().getMobile());
        status.setEnabled(false);
        String msg;
        LexResponse lexResponse;

        final Status receivedStatus = sendSafetyModeStatus(status);
        log.info("Retrieved status for safety: " + receivedStatus);
        if (!receivedStatus.isEnabled()) {
            msg = "Safety mode has been unset successfully " + MessageUtil.getRandomEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
        } else {
            msg = "Sorry, some error occurred. Safety mode has not been set " + MessageUtil.getErrorEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;
    }


    private Status buildStatusObject(SessionAttributes sessionAttributes, String mobile) {
        Status status = new Status();
        status.setCallBack(SAFETY_CALLBACK_URL);
        status.setTextNotification(mobile);
        status.setChannel(sessionAttributes.getChannel());
        status.setConversationId(sessionAttributes.getConversationId());
        status.setEnabled(true);
        status.setId(sessionAttributes.getId());
        status.setName(sessionAttributes.getName());
        status.setServiceUrl(sessionAttributes.getServiceUrl());
        return status;
    }

    private Status getSafetyModeStatus() throws IOException {
        final String JSONresponse = embeddedEndpointGET(SAFETY_PATH);
        return parseResponse(JSONresponse, Status.class);
    }

    private Status sendSafetyModeStatus(Status status) throws IOException {
        final String JSONresponse = embeddedEndpointPUT(SAFETY_PATH, status);
        return parseResponse(JSONresponse, Status.class);
    }

    private boolean isMobileValid(String mobile) {
        String regex = "[0-9]+";
        boolean validity = mobile.matches(regex) && mobile.length() == 12;
        log.info("checking mobile validity: " + mobile + " = " + validity);
        return validity;
    }

    private String cleanMobileNumber(String mobile) {
        log.info("cleaning mobile number: " + mobile);
        String result = mobile.replace("(", "");
        result = result.replace("(", "");
        result = result.replace(")", "");
        result = result.replace("-", "");
        result = result.replace("/", "");
        result = result.replace(" ", "");
        result = result.replace("+", "");
        return result;
    }
}
