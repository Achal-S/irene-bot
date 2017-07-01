package irene.bot.messaging;

import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Request;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ConversationsApi;
import io.swagger.client.model.Activity;
import io.swagger.client.model.ChannelAccount;
import io.swagger.client.model.ConversationAccount;
import io.swagger.client.model.ResourceResponse;
import irene.bot.embedded.LexRuntimeService;
import irene.bot.messaging.model.AuthenticationResponse;
import org.joda.time.DateTime;

import java.io.IOException;

public class MessageProcessorService {

    private static final String AUTHORIZATION = "Authorization";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MessageProcessorService.class);

    private AuthenticationService authenticationService = new AuthenticationService();
    private LexRuntimeService lexRuntimeService = new LexRuntimeService();

    public String processMessage(Activity activity) throws ApiException, NoSuchFieldException, IllegalAccessException, IOException {
        log.info("Processing message: " + activity.getText());
        String reply = lexRuntimeService.sendToBot(activity.getText(), activity.getFrom().getId());
        return this.sendMessageToConversation(activity.getChannelId(), activity.getRecipient(), activity.getFrom(), activity.getServiceUrl(), reply, activity.getConversation().getId()).getId();
    }


    private ResourceResponse sendMessageToConversation(String channelId, ChannelAccount fromAccount, ChannelAccount toAccount, String serviceUrl, String text, String conversationId) throws ApiException, NoSuchFieldException, IllegalAccessException, IOException {
        Activity echo = new Activity();
        echo.setFrom(fromAccount);
        echo.setType("message");
        echo.setText(text);
        echo.setRecipient(toAccount);
        echo.setChannelId(channelId);

        ConversationAccount conversationAccount = new ConversationAccount();
        conversationAccount.setId(conversationId);
        echo.setConversation(conversationAccount);

        ConversationsApi conversationsApi = new ConversationsApi(instantiateApiClient(serviceUrl));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
        return conversationsApi.conversationsSendToConversation(echo, conversationId);
    }

    private ApiClient instantiateApiClient(String urlBasePath) throws NoSuchFieldException, IllegalAccessException, IOException {
        log.info("Starting authetication process");
        ApiClient apiClient = new ApiClient();
        AuthenticationResponse authenticationResponse = authenticationService.authenticate();
        apiClient.getHttpClient().networkInterceptors().add(chain -> {
            Request request = chain.request();
            request = request.newBuilder().addHeader(AUTHORIZATION, authenticationResponse.getTokenType() + " " + authenticationResponse.getAccessToken()).build();
            return chain.proceed(request);
        });
        apiClient.setBasePath(urlBasePath);
        return apiClient;
    }
}
