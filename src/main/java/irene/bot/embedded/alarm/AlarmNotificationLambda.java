package irene.bot.embedded.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.swagger.client.model.ChannelAccount;
import io.swagger.client.model.ResourceResponse;
import irene.bot.embedded.AbstractNotificationLambda;
import irene.bot.embedded.model.Status;
import irene.bot.messaging.MessageProcessorService;
import irene.bot.util.ApplicationPropertiesUtil;


public class AlarmNotificationLambda extends AbstractNotificationLambda implements RequestHandler<Status, String> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlarmNotificationLambda.class);


    private MessageProcessorService messageProcessorService = new MessageProcessorService();

    @Override
    public String handleRequest(final Status status, final Context context) {
        try {
            log.info("Received status for alarm notification:" + status);
            ChannelAccount fromAccount = new ChannelAccount();
            fromAccount.setId(this.getIdFromChannel(status.getChannel()));
            fromAccount.setName(this.getUserNameFromChannel(status.getChannel()));

            ChannelAccount toAccount = new ChannelAccount();
            toAccount.setId(status.getId());
            toAccount.setName(status.getName());

            log.info("Sending message to conversation: "+status.getConversationId());
            log.info("Sending message to id: "+this.getIdFromChannel(status.getChannel()));
            log.info("Sending message to name: "+this.getUserNameFromChannel(status.getChannel()));

            ResourceResponse resourceResponse = messageProcessorService.sendMessageToConversation(status.getChannel(), fromAccount, toAccount, status.getServiceUrl(), "Somebody touched me!!", status.getConversationId());
            return resourceResponse.getId();
        } catch (Exception e) {
            log.error(e);
            return "";
        }
    }
}
