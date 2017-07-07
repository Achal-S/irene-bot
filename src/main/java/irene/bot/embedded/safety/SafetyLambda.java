package irene.bot.embedded.safety;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.lex.LexFullfillmentService;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.lex.model.SessionAttributes;
import irene.bot.embedded.model.Status;
import irene.bot.util.ApplicationPropertiesUtil;

public class SafetyLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SafetyLambda.class);
    private static final String SAFETY_PATH = "safety";
    private static final String SAFETY_CALLBACK_PROPERTY = "safety.notification.callback";
    private final String SAFETY_CALLBACK_NUMBER = ApplicationPropertiesUtil.getProperty(SAFETY_CALLBACK_PROPERTY, this.getClass());

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final SessionAttributes sessionAttributes = lexEvent.getSessionAttributes();
            final Status status = buildStatusObject(sessionAttributes);
            log.info("Status built for alarm triggering is: " + status);
            final String JSONresponse = embeddedEndpointPUT(SAFETY_PATH, status);
            final Status receivedStatus = parseResponse(JSONresponse, Status.class);
            log.info("Retrieved status: " + receivedStatus);
            if (receivedStatus.isEnabled()) {
                msg = String.format("Alarm has been set successfully");
            } else {
                msg = String.format("Alarm has not been set. Error is: " + receivedStatus.getMessage());
            }
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to set the alarm right now.";
        }
        return null;
    }

    private Status buildStatusObject(SessionAttributes sessionAttributes) {
        Status status = new Status();
        status.setCallBack(SAFETY_CALLBACK_NUMBER);
        status.setChannel(sessionAttributes.getChannel());
        status.setConversationId(sessionAttributes.getConversationId());
        status.setEnabled(true);
        status.setId(sessionAttributes.getId());
        status.setName(sessionAttributes.getName());
        status.setServiceUrl(sessionAttributes.getServiceUrl());
        status.setTextNotification(sessionAttributes.getTextNotification());
        return status;
    }
}
