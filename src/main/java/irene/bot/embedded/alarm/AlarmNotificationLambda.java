package irene.bot.embedded.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.swagger.client.model.ChannelAccount;
import io.swagger.client.model.ResourceResponse;
import irene.bot.embedded.model.Status;
import irene.bot.messaging.MessageProcessorService;
import irene.bot.util.ApplicationPropertiesUtil;


public class AlarmNotificationLambda implements RequestHandler<Status, String> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlarmNotificationLambda.class);
    private static final String SKYPE_ID = "skype.id";
    private static final String SKYPE_NAME = "skype.name";

    private final String BOT_SKYPE_NAME = ApplicationPropertiesUtil.getProperty(SKYPE_ID, this.getClass());
    private final String BOT_SKYPE_ID = ApplicationPropertiesUtil.getProperty(SKYPE_NAME, this.getClass());

    private MessageProcessorService messageProcessorService = new MessageProcessorService();

    @Override
    public String handleRequest(final Status status, final Context context) {
        try {
            log.info("Received status for alarm notification:" + status);
            ChannelAccount fromAccount = new ChannelAccount();
            fromAccount.setId(BOT_SKYPE_ID);
            fromAccount.setName(BOT_SKYPE_NAME);

            ChannelAccount toAccount = new ChannelAccount();
            toAccount.setId(status.getId());
            toAccount.setName(status.getName());

            ResourceResponse resourceResponse = messageProcessorService.sendMessageToConversation(status.getChannel(), fromAccount, toAccount, status.getServiceUrl(), "Somebody touched me!!", status.getConversationId());
            return resourceResponse.getId();
        } catch (Exception e) {
            log.error(e);
            return "";
        }
    }
}
