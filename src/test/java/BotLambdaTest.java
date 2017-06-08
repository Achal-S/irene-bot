import com.google.gson.Gson;
import irene.bot.lambda.BotLambda;
import irene.bot.model.LexEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;


public class BotLambdaTest {

    private BotLambda botLambda = new BotLambda();
    private String input = "{\n" +
            "  \"currentIntent\": {\n" +
            "    \"name\": \"intent-name\",\n" +
            "    \"slots\": {\n" +
            "      \"slot-name\": \"value\",\n" +
            "      \"slot-name\": \"value\",\n" +
            "      \"slot-name\": \"value\"\n" +
            "    },\n" +
            "    \"confirmationStatus\": \"None, Confirmed, or Denied (intent confirmation, if configured)\",\n" +
            "  },\n" +
            "  \"bot\": {\n" +
            "    \"name\": \"bot-name\",\n" +
            "    \"alias\": \"bot-alias\",\n" +
            "    \"version\": \"bot-version\"\n" +
            "  },\n" +
            "  \"userId\": \"User ID specified in the POST request to Amazon Lex.\",\n" +
            "  \"inputTranscript\": \"Text used to process the request\",\n" +
            "  \"invocationSource\": \"FulfillmentCodeHook or DialogCodeHook\",\n" +
            "  \"outputDialogMode\": \"Text or Voice, based on ContentType request header in runtime API request\",\n" +
            "  \"messageVersion\": \"1.0\",\n" +
            "  \"sessionAttributes\": { \n" +
            "     \"key1\": \"value1\",\n" +
            "     \"key2\": \"value2\"\n" +
            "  }\n" +
            "}";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore
    public void testLambda(){
        Gson gson = new Gson();
        LexEvent lexEvent = gson.fromJson(input, LexEvent.class);

        this.botLambda.handleRequest(lexEvent, null);
    }

}