package irene.bot.messaging;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.GsonBuilder;
import io.swagger.client.model.Activity;
import irene.bot.messaging.model.MessageProcessingInput;
import irene.bot.messaging.model.MessageProcessingResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.DateTime;


public class MessagingReceivedLambda implements RequestHandler<MessageProcessingInput, MessageProcessingResult> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MessagingReceivedLambda.class);
    private static final String MESSAGE = "message";
    private MessageProcessorService messageProcessorService = new MessageProcessorService();

    private String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    @Override
    public MessageProcessingResult handleRequest(final MessageProcessingInput input, final Context context) {
        MessageProcessingResult messageProcessingResult;
        try {
            String decodedInput = decode(input.getBody());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
            log.debug("Received raw message from user " + decodedInput);
            Activity activity = gsonBuilder.create().fromJson(decodedInput, Activity.class);
            log.info("Received message from user " + activity);

            if (activity.getType().equals(MESSAGE)) {
                String id = messageProcessorService.processMessage(activity);
                messageProcessingResult = new MessageProcessingResult(true, id);
            }else{
                log.warn("Received message with unsupported type: "+activity.getType());
                messageProcessingResult = new MessageProcessingResult(false, null);
            }
        } catch (Exception e) {
            log.error(e);
            messageProcessingResult = new MessageProcessingResult(false, null);
        }
        return messageProcessingResult;
    }
}
