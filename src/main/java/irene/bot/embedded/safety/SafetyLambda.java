package irene.bot.embedded.safety;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
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
    private static final String INVALID_DEFAULT_MOBILE = "2d7428a6-b58c-4008-8575-f05549f16316";
    private final String SAFETY_CALLBACK_URL = ApplicationPropertiesUtil.getProperty(SAFETY_CALLBACK_PROPERTY, this.getClass());
    private static final String MANAGE_SAFETY_STATUS = "ManageSafetyStatus";

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        log.info("Current invocation source is: " + lexEvent.getInvocationSource());
        LexResponse lexResponse;

        log.info("Current mobile is: " + lexEvent.getCurrentIntent().getSlots().getMobile());
        try {
            if (lexEvent.getInvocationSource().equals(InvocationSourceType.DIALOGCODEHOOK.toString())) {
                lexResponse = this.handleDialogHook(lexEvent);
            } else {
                lexResponse = this.handleFullfillment(lexEvent);
            }
        } catch (Exception e) {
            log.error(e);
            String msg = String.format("Ouch...there has been an error. Safety mode has not been set. " + MessageUtil.getErrorEmoji());
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;

    }

    private LexResponse handleDialogHook(final LexEvent lexEvent) throws IOException {
        log.info("Dialog hook handling");
        ConfirmationStatus confirmationStatus = ConfirmationStatus.fromString(lexEvent.getCurrentIntent().getConfirmationStatus());
        if (confirmationStatus.equals(ConfirmationStatus.CONFIRMED) && !getSafetyModeStatus().isEnabled()) {
            log.info("Eliciting mobile because status is confirmed and safety is off");
            return retrieveMobileSlot("", lexEvent.getCurrentIntent().getSlots());
        } else {
            log.info("Normal fullfillment");
            return this.handleFullfillment(lexEvent);
        }
    }


    private LexResponse handleFullfillment(final LexEvent lexEvent) throws IOException {
        log.info("Fullfillment handling");
        ConfirmationStatus confirmationStatus = ConfirmationStatus.fromString(lexEvent.getCurrentIntent().getConfirmationStatus());
        String msg;
        LexResponse lexResponse;
        String mobile = lexEvent.getCurrentIntent().getSlots().getMobile();
        switch (confirmationStatus) {
            case NONE:
                if (mobile==null || mobile.equals(INVALID_DEFAULT_MOBILE)) {
                    lexResponse = this.processConfirmationStatusNone();
                    break;
                } else {
                    if (!isMobileValid(mobile)) {
                        lexResponse = this.retrieveMobileSlot("Sorry the number you entered is not correct. ", lexEvent.getCurrentIntent().getSlots());
                    } else {
                        lexResponse = this.processConfirmationStatusConfirmed(lexEvent);
                    }
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

    private LexResponse retrieveMobileSlot(String incipitMsg, Slots slots) throws IOException {
        String msg = incipitMsg + "Please, " + MessageUtil.getRandomGreeting() + " insert a mobile number with international prefix without plus or dash (e.g., 15705598299 for U.S.) to send text notifications.";
        return lexFullfillmentService.lexElicitSlot(msg, "mobile", slots, MANAGE_SAFETY_STATUS);
    }

    private LexResponse processConfirmationStatusNone() throws IOException {
        final Status status = getSafetyModeStatus();
        String msg;
        if (status.isEnabled()) {
            msg = "Safety mode is enabled, do you want to turn it off?";
        } else {
            msg = "Safety mode is disabled, do you want to turn it on?";
        }
        Slots slots = new Slots();
        slots.setMobile(INVALID_DEFAULT_MOBILE);
        return lexFullfillmentService.lexConfirmIntent(msg, MANAGE_SAFETY_STATUS, slots);
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
        final String mobile = lexEvent.getCurrentIntent().getSlots().getMobile();
        LexResponse lexResponse;

        if (this.isMobileValid(mobile)) {
            final Status status = buildStatusObject(lexEvent.getSessionAttributes(), cleanMobileNumber(lexEvent.getCurrentIntent().getSlots().getMobile()));
            status.setEnabled(true);
            String msg;

            final Status receivedStatus = sendSafetyModeStatus(status);
            log.info("Retrieved status for safety: " + receivedStatus);
            if (receivedStatus.isEnabled()) {
                msg = "Safety mode has been set successfully (alarm is disabled) " + MessageUtil.getRandomEmoji();
                lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
            } else {
                msg = "Sorry, some error occurred. Safety mode has not been set " + MessageUtil.getErrorEmoji();
                lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
            }
        } else {
            lexResponse = this.retrieveMobileSlot("Sorry the number you entered is not correct. ", lexEvent.getCurrentIntent().getSlots());
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
        if (StringUtils.isNullOrEmpty(mobile)) {
            return false;
        } else {
            String regex = "[0-9]+";
            boolean validity = mobile.matches(regex) && mobile.length() >= 11 && mobile.length() <= 12;
            log.info("checking mobile validity: " + mobile + " = " + validity);
            return validity;
        }
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
