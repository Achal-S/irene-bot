package irene.bot.embedded.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.model.Status;
import irene.bot.lex.model.*;
import irene.bot.util.ApplicationPropertiesUtil;
import irene.bot.util.MessageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static irene.bot.util.MessageUtil.getErrorEmoji;
import static irene.bot.util.MessageUtil.getRandomEmoji;

public class AlarmLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlarmLambda.class);
    private static final String ALARM_PATH = "alarm";
    private static final String ALARM_CALLBACK_PROPERTY = "alarm.notification.callback";
    private static final String MANAGE_ALARM_STATUS = "ManageAlarmStatus";
    private static final String DEFAULT_BACK_OFF_MESSAGE = "Hey! Back off";
    private static final String BACK_OFF_MESSAGE = "backOffMessage";
    private final String ALARM_CALLBACK_URL = ApplicationPropertiesUtil.getProperty(ALARM_CALLBACK_PROPERTY, this.getClass());

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        ConfirmationStatus confirmationStatus = ConfirmationStatus.fromString(lexEvent.getCurrentIntent().getConfirmationStatus());
        String msg;
        LexResponse lexResponse;

        try {
            switch (confirmationStatus) {
                case NONE:
                    lexResponse = this.processConfirmationStatusNone(lexEvent);
                    break;
                case CONFIRMED:
                    lexResponse = this.processConfirmationStatusConfirmed(lexEvent);
                    break;
                case DENIED:
                    lexResponse = this.processConfirmationStatusDenied();
                    break;
                default:
                    msg = String.format("Sorry there has been a problem. Alarm has not been set/unset "+getErrorEmoji());
                    lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
            }

        } catch (Exception e) {
            log.error(e);
            msg = String.format("Sorry, some error occurred. Alarm has not been set "+getErrorEmoji());
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;

    }

    private LexResponse processConfirmationStatusNone(LexEvent lexEvent) throws IOException {
        Boolean desiredState = lexEvent.getCurrentIntent().getSlots().getDesiredState();

        if(desiredState==null) {
            final Slots slots = new Slots();
            String msg;
            final Status status = getAlarmStatus();
            if (status.isEnabled()) {
                msg = "Alarm is enabled, do you want to turn it off?";
                slots.setDesiredState(false);
            } else {
                msg = "Alarm is disabled, do you want to turn it on?";
                slots.setDesiredState(true);
            }
            slots.setBackOffMessage(DEFAULT_BACK_OFF_MESSAGE);
            return lexFullfillmentService.lexConfirmIntent(msg, MANAGE_ALARM_STATUS, slots);
        }else{
            return this.turnAlarmOn(lexEvent);
        }
    }

    private LexResponse processConfirmationStatusConfirmed(LexEvent lexEvent) throws IOException {
        LexResponse lexResponse;
        Boolean desiredState = lexEvent.getCurrentIntent().getSlots().getDesiredState();

        if (desiredState == null) {
            return lexFullfillmentService.lexCloseIntent("Sorry, I don't understand that.", FullfillmentState.FAILED);
        }

        if (desiredState) {
            lexResponse = lexFullfillmentService.lexElicitSlot("Please, enter a back-off message that I will pronounce when the alarm is triggered.", BACK_OFF_MESSAGE, lexEvent.getCurrentIntent().getSlots(), MANAGE_ALARM_STATUS);
        } else {
            lexResponse = this.turnAlarmOff(lexEvent);
        }
        return lexResponse;
    }


    private LexResponse processConfirmationStatusDenied() throws IOException {
        LexResponse lexResponse = lexFullfillmentService.lexCloseIntent("Ok, "+ MessageUtil.getRandomGreeting()+". Talk to you later "+getRandomEmoji(), FullfillmentState.FULFILLED);
        return lexResponse;

    }

    private LexResponse turnAlarmOn(LexEvent lexEvent) throws IOException {
        final Status status = buildStatusObject(lexEvent.getSessionAttributes());
        status.setBackOffMessage(lexEvent.getCurrentIntent().getSlots().getBackOffMessage());
        status.setEnabled(true);
        String msg;
        LexResponse lexResponse;

        final Status receivedStatus = sendAlarmStatus(status);
        log.info("Retrieved status for alarm: " + receivedStatus);
        if (receivedStatus.isEnabled()) {
            msg = "Alarm has been set successfully "+ MessageUtil.getRandomEmoji();;
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
        } else {
            msg = "There has been an error. Alarm has not been set "+getErrorEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;
    }


    private LexResponse turnAlarmOff(LexEvent lexEvent) throws IOException {
        final Status status = buildStatusObject(lexEvent.getSessionAttributes());
        status.setEnabled(false);
        String msg;
        LexResponse lexResponse;

        final Status receivedStatus = sendAlarmStatus(status);
        log.info("Retrieved status for alarm: " + receivedStatus);
        if (!receivedStatus.isEnabled()) {
            msg = "Alarm has been unset successfully "+ MessageUtil.getRandomEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
        } else {
            msg = "There has been an error. Alarm has not been set "+getErrorEmoji();
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;
    }


    private Status buildStatusObject(SessionAttributes sessionAttributes) {
        Status status = new Status();
        status.setCallBack(ALARM_CALLBACK_URL);
        status.setChannel(sessionAttributes.getChannel());
        status.setConversationId(sessionAttributes.getConversationId());
        status.setEnabled(true);
        status.setId(sessionAttributes.getId());
        status.setName(sessionAttributes.getName());
        status.setServiceUrl(sessionAttributes.getServiceUrl());
        return status;
    }

    private Status getAlarmStatus() throws IOException {
        final String JSONresponse = embeddedEndpointGET(ALARM_PATH);
        return parseResponse(JSONresponse, Status.class);
    }

    private Status sendAlarmStatus(Status status) throws IOException {
        final String JSONresponse = embeddedEndpointPUT(ALARM_PATH, status);
        return parseResponse(JSONresponse, Status.class);
    }
}
