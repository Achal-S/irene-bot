package irene.bot.embedded;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;

public class LexRuntimeService {

    private static final String BOT_NAME = "irene";
    private static final String BOT_ALIAS = "prod";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LexRuntimeService.class);

    public String sendToBot(final String msg, final String userId) {
        log.info("Sending message to Lex runtime: " + msg);
        final AmazonLexRuntime amazonLexRuntime = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        final PostTextRequest postTextRequest = new PostTextRequest();
        postTextRequest.setInputText(msg);
        postTextRequest.setBotAlias(BOT_ALIAS);
        postTextRequest.setBotName(BOT_NAME);
        postTextRequest.setUserId(userId);
        PostTextResult postTextResult = amazonLexRuntime.postText(postTextRequest);
        log.info("Message sent to Lex runtime with result: " + postTextResult);
        return postTextResult.getMessage();
    }
}
