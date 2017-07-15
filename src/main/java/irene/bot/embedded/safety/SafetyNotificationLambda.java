package irene.bot.embedded.safety;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.swagger.client.ApiException;
import io.swagger.client.model.ChannelAccount;
import io.swagger.client.model.ResourceResponse;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.AbstractNotificationLambda;
import irene.bot.embedded.model.Status;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.map.GeocodingService;
import irene.bot.messaging.MessageProcessorService;
import irene.bot.text.TextingService;
import irene.bot.util.ApplicationPropertiesUtil;

import java.io.IOException;


public class SafetyNotificationLambda extends AbstractNotificationLambda implements RequestHandler<Status, String> {

    private static final String POSITION_PATH = "position";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SafetyNotificationLambda.class);

    private MessageProcessorService messageProcessorService = new MessageProcessorService();
    private TextingService textingService = new TextingService();
    private GeocodingService geocodingService = new GeocodingService();


    @Override
    public String handleRequest(final Status status, final Context context) {
        try {
            log.info("Received status for alarm notification:" + status);
            final String JSONresponse = embeddedEndpointGET(POSITION_PATH);
            final Position position = parseResponse(JSONresponse, Position.class);

            if (position.isSuccess()) {
                final String mapUrl = getMapUrl(position);
                String msg = String.format("Hi, I am Irene the motorbike bot. The bike seems to be on the ground at these coordinates: [%f, %f]. Check them out on a map: %s", position.getLatitude(), position.getLongitude(), mapUrl);
                String textResult =  textingService.sendTextNotification(msg, status.getTextNotification());
                sendMessage(status);
                return textResult;
            } else {
                return "Position tracking failed: " + position.getMessage();
            }
        } catch (Exception e) {
            log.error(e);
            return "Error during text notification: " + e.getMessage();
        }
    }


    private String sendMessage(Status status) throws IOException, IllegalAccessException, NoSuchFieldException, ApiException {
        ChannelAccount fromAccount = new ChannelAccount();
        fromAccount.setId(this.getIdFromChannel(status.getChannel()));
        fromAccount.setName(this.getUserNameFromChannel(status.getChannel()));

        ChannelAccount toAccount = new ChannelAccount();
        toAccount.setId(status.getId());
        toAccount.setName(status.getName());

        log.info("Sending message to conversation: "+status.getConversationId());
        log.info("Sending message to id: "+this.getIdFromChannel(status.getChannel()));
        log.info("Sending message to name: "+this.getUserNameFromChannel(status.getChannel()));

        ResourceResponse resourceResponse = messageProcessorService.sendMessageToConversation(status.getChannel(), fromAccount, toAccount, status.getServiceUrl(), "Hey! Safety mode has been triggered, I have sent a text message to: " + status.getTextNotification() + " Don't scare me, I hope everything is fine", status.getConversationId());
        return resourceResponse.getId();
    }

    private String getMapUrl(final Position position) {
        return this.geocodingService.getMapURL(position.getLatitude(), position.getLongitude());
    }
}
