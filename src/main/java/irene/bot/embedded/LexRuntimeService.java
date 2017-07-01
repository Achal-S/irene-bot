package irene.bot.embedded;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;

public class LexRuntimeService {

    private static final String BOT_NAME = "irene";
    private static final String BOT_ALIAS = "prod";

    public String sendToBot(String msg, String userId){
        AmazonLexRuntime amazonLexRuntime = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        PostTextRequest postTextRequest = new PostTextRequest();
        postTextRequest.setInputText(msg);
        postTextRequest.setBotAlias(BOT_ALIAS);
        postTextRequest.setBotName(BOT_NAME);
        postTextRequest.setUserId(userId);
        PostTextResult postTextResult  = amazonLexRuntime.postText(postTextRequest);
        return postTextResult.getMessage();
    }
}
