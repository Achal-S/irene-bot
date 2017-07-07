package irene.bot.embedded.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.model.Status;
import irene.bot.lex.model.*;
import irene.bot.util.ApplicationPropertiesUtil;

import java.io.IOException;

public class AlarmLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlarmLambda.class);
    private static final String ALARM_PATH = "alarm";
    private static final String ALARM_CALLBACK_PROPERTY = "alarm.notification.callback";
    private static final String MANAGE_ALARM_STATUS = "ManageAlarmStatus";
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
                    lexResponse = this.processConfirmationStatusNone();
                    break;
                case CONFIRMED:
                    lexResponse = this.processConfirmationStatusConfirmed(lexEvent);
                    break;
                case DENIED:
                    lexResponse = this.processConfirmationStatusDenied();
                    break;
                default:
                    msg = String.format("Sorry there has been a problem. Alarm has not been set/unset.");
                    lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
            }

        } catch (Exception e) {
            log.error(e);
            msg = String.format("Alarm has not been set. Error is: " + e.getMessage());
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
        }
        return lexResponse;

    }

    private LexResponse processConfirmationStatusNone() throws IOException {
        final Status status = getAlarmStatus();
        String msg;
        if (status.isEnabled()) {
            msg = "Alarm is enabled, do you want to turn it off?";
        } else {
            msg = "Alarm is disabled, do you want to turn it on?";
        }
        return lexFullfillmentService.lexConfirmIntent(msg, MANAGE_ALARM_STATUS, new Slots());
    }

    private LexResponse processConfirmationStatusConfirmed(LexEvent lexEvent) throws IOException {
        final Status status = getAlarmStatus();
        LexResponse lexResponse;
        if (status.isEnabled()) {
            lexResponse = this.turnAlarmOff(lexEvent);
        } else {
            lexResponse = this.turnAlarmOn(lexEvent);
        }
        return lexResponse;

    }

    private LexResponse processConfirmationStatusDenied() throws IOException {
        LexResponse lexResponse = lexFullfillmentService.lexCloseIntent("Ok.", FullfillmentState.FULFILLED);
        return lexResponse;

    }

    private LexResponse turnAlarmOn(LexEvent lexEvent) throws IOException {
        final Status status = buildStatusObject(lexEvent.getSessionAttributes());
        status.setEnabled(true);
        String msg;
        LexResponse lexResponse;

        final Status receivedStatus = sendAlarmStatus(status);
        log.info("Retrieved status for alarm: " + receivedStatus);
        if (receivedStatus.isEnabled()) {
            msg = String.format("Alarm has been set successfully.");
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
        } else {
            msg = String.format("Alarm has not been set. Error is: " + receivedStatus.getMessage());
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
            msg = String.format("Alarm has been unset successfully.");
            lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
        } else {
            msg = String.format("Alarm has not been unset. Error is: " + receivedStatus.getMessage());
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
