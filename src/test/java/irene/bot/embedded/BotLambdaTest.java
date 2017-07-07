package irene.bot.embedded;

import com.google.gson.Gson;
import irene.bot.embedded.sensing.PositionLambda;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.lex.model.LexResponse;
import irene.bot.lex.model.Slots;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BotLambdaTest {

    private PositionLambda botLambda = new PositionLambda();
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
    public void testLambda() {
        Gson gson = new Gson();

        String slots = gson.toJson(new Slots());
//
        System.out.println(slots);
    }


    @Test
    public void deserializeTemperature() throws Exception {
        String input = "{\"latitude\":45.812127836,\"longitude\":9.088960812,\"speed\":0.0,\"speedError\":68.57,\"success\":true}";
        Gson gson = new Gson();
        Position position = gson.fromJson(input, Position.class);
    }
}