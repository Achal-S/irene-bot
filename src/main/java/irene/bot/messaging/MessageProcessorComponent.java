package irene.bot.messaging;

import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Request;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ConversationsApi;
import io.swagger.client.model.Activity;
import io.swagger.client.model.ChannelAccount;
import io.swagger.client.model.ConversationAccount;
import irene.bot.messaging.model.AuthenticationResponse;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class MessageProcessorComponent {

    private static final String AUTHORIZATION = "Authorization";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MessageProcessorComponent.class);


    private AuthenticationService authenticationService = new AuthenticationService();

    private Queue<Activity> messages = new ArrayDeque<>();

    public void enqueueMessage(Activity message) {
        this.messages.add(message);
    }

    public void processMessage() throws ApiException, NoSuchFieldException, IllegalAccessException, IOException {
        if (!this.messages.isEmpty()) {
            Activity message = this.messages.poll();
            log.info("Processing message: "+message.getText());
            this.sendMessageToConversation(message.getChannelId(), message.getRecipient(), message.getFrom(), message.getServiceUrl(), message.getText(), message.getConversation().getId());
        }
    }

    private void sendMessageToConversation(String channelId, ChannelAccount fromAccount, ChannelAccount toAccount, String serviceUrl, String text, String conversationId) throws ApiException, NoSuchFieldException, IllegalAccessException, IOException {
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
        conversationsApi.conversationsSendToConversation(echo, conversationId);
    }

    private ApiClient instantiateApiClient(String urlBasePath) throws NoSuchFieldException, IllegalAccessException, IOException {
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
