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
import irene.bot.LexRuntimeService;
import irene.bot.messaging.model.AuthenticationResponse;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageProcessorService {

    public static final String CHANNEL = "channel";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SERVICE_URL = "serviceUrl";

    private static final String AUTHORIZATION = "Authorization";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MessageProcessorService.class);

    private static final String MESSAGE = "message";

    private AuthenticationService authenticationService = new AuthenticationService();
    private LexRuntimeService lexRuntimeService = new LexRuntimeService();

    public String processMessage(final Activity activity) throws ApiException, NoSuchFieldException, IllegalAccessException, IOException {
        log.info("Processing message: " + activity.getText());
        Map<String, String> sessionAttributes = this.buildSessionAttributesMap(activity.getChannelId(), activity.getConversation().getId(), activity.getFrom().getId(), activity.getFrom().getName(), activity.getServiceUrl());
        final String reply = lexRuntimeService.sendToBot(activity.getText(), sessionAttributes);
        return this.sendMessageToConversation(activity.getChannelId(), activity.getRecipient(), activity.getFrom(), activity.getServiceUrl(), reply, activity.getConversation().getId()).getId();
    }

    private Map<String, String> buildSessionAttributesMap(String channel, String conversationId, String id, String name, String serviceUrl){
        Map<String, String> sessionAttributesMap = new HashMap<>();
        sessionAttributesMap.put(CHANNEL, channel);
        sessionAttributesMap.put(CONVERSATION_ID, conversationId);
        sessionAttributesMap.put(ID, id);
        sessionAttributesMap.put(NAME, name);
        sessionAttributesMap.put(SERVICE_URL, serviceUrl);
        return sessionAttributesMap;
    }

    public ResourceResponse sendMessageToConversation(final String channelId, final ChannelAccount fromAccount, final ChannelAccount toAccount, final String serviceUrl, final String text, final String conversationId) throws ApiException, NoSuchFieldException, IllegalAccessException, IOException {
        final Activity echo = new Activity();
        echo.setFrom(fromAccount);
        echo.setType(MESSAGE);
        echo.setText(text);
        echo.setRecipient(toAccount);
        echo.setChannelId(channelId);

        final ConversationAccount conversationAccount = new ConversationAccount();
        conversationAccount.setId(conversationId);
        echo.setConversation(conversationAccount);

        final ConversationsApi conversationsApi = new ConversationsApi(instantiateApiClient(serviceUrl));

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
        return conversationsApi.conversationsSendToConversation(echo, conversationId);
    }

    private ApiClient instantiateApiClient(final String urlBasePath) throws NoSuchFieldException, IllegalAccessException, IOException {
        log.info("Starting authentication process");
        final ApiClient apiClient = new ApiClient();
        final AuthenticationResponse authenticationResponse = authenticationService.authenticate();
        apiClient.getHttpClient().networkInterceptors().add(chain -> {
            Request request = chain.request();
            request = request.newBuilder().addHeader(AUTHORIZATION, authenticationResponse.getTokenType() + " " + authenticationResponse.getAccessToken()).build();
            return chain.proceed(request);
        });
        apiClient.setBasePath(urlBasePath);
        return apiClient;
    }
}
