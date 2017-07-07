package irene.bot.embedded.sensing;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.lex.model.FullfillmentState;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.embedded.sensing.model.Humidity;

public class HumidityLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HumidityLambda.class);
    private static final String HUMIDITY_PATH = "humidity";

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final String JSONresponse = embeddedEndpointGET(HUMIDITY_PATH);
            final Humidity humidity = parseResponse(JSONresponse, Humidity.class);
            log.info("Retrieved humidity: " + humidity);
            msg = String.format("Hey darling, humidity is %f.", humidity.getHumidity());
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to sense the humidity right now.";
        }
        return lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
    }
}
