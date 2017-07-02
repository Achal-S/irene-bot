package irene.bot;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;
import irene.bot.messaging.MessageProcessorService;

import java.util.Map;

public class LexRuntimeService {

    private static final String BOT_NAME = "irene";
    private static final String BOT_ALIAS = "prod";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LexRuntimeService.class);

    public String sendToBot(final String msg, final Map<String, String> sessionAttributes) {
        log.info("Sending message to Lex runtime: " + msg);
        final AmazonLexRuntime amazonLexRuntime = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        final PostTextRequest postTextRequest = new PostTextRequest();
        postTextRequest.setInputText(msg);
        postTextRequest.setBotAlias(BOT_ALIAS);
        postTextRequest.setBotName(BOT_NAME);
        postTextRequest.setUserId(createUniqueIdForConversation(sessionAttributes));
        postTextRequest.setSessionAttributes(sessionAttributes);
        PostTextResult postTextResult = amazonLexRuntime.postText(postTextRequest);
        log.info("Message sent to Lex runtime with result: " + postTextResult);
        return postTextResult.getMessage();
    }

    private String createUniqueIdForConversation(Map<String, String> sessionAttributes){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sessionAttributes.get(MessageProcessorService.CHANNEL));
        stringBuilder.append("-");
        stringBuilder.append(sessionAttributes.get(MessageProcessorService.NAME));
        stringBuilder.append("-");
        stringBuilder.append(sessionAttributes.get(MessageProcessorService.CONVERSATION_ID));
        log.info("Setting message attributes: "+stringBuilder.toString());
        return stringBuilder.toString().replace(" ", "-");
    }
}
