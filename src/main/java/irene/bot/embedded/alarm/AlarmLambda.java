package irene.bot.embedded.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedLambda;
import irene.bot.embedded.alarm.model.Status;
import irene.bot.embedded.model.LexEvent;
import irene.bot.embedded.model.LexResponse;
import irene.bot.embedded.model.SessionAttributes;
import irene.bot.embedded.sensing.model.Humidity;
import irene.bot.util.ApplicationPropertiesUtil;

import java.util.Map;

public class AlarmLambda extends AbstractEmbeddedLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlarmLambda.class);
    private static final String ALARM_PATH = "alarm";
    private static final String ALARM_CALLBACK_PROPERTY = "alarm.notification.callback";
    private final String ALARM_CALLBACK_URL = ApplicationPropertiesUtil.getProperty(ALARM_CALLBACK_PROPERTY, this.getClass());

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final SessionAttributes sessionAttributes = lexEvent.getSessionAttributes();
            final Status status = buildStatusObject(sessionAttributes);
            log.info("Status built for alarm triggering is: " + status);
            final String JSONresponse = embeddedEndpointPUT(ALARM_PATH, status);
            final Status receivedStatus = parseResponse(JSONresponse, Status.class);
            log.info("Retrieved status: " + receivedStatus);
            if(receivedStatus.isEnabled()){
                msg = String.format("Alarm has been set successfully");
            }else{
                msg = String.format("Alarm has not been set. Error is: "+ receivedStatus.getMessage());
            }
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to set the alarm right now.";
        }
        return sendReplyToLex(msg, FULFILLED, CLOSE, PLAIN_TEXT);
    }

    private Status buildStatusObject(SessionAttributes sessionAttributes){
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
}
