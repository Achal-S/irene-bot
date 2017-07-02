package irene.bot.embedded.sensing;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedLambda;
import irene.bot.embedded.model.LexEvent;
import irene.bot.embedded.model.LexResponse;
import irene.bot.embedded.sensing.model.Temperature;

public class TemperatureLambda extends AbstractEmbeddedLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TemperatureLambda.class);
    private static final String TEMPERATURE_PATH = "temperature";

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final String JSONresponse = embeddedEndpointGET(TEMPERATURE_PATH);
            final Temperature temperature = parseResponse(JSONresponse, Temperature.class);
            log.info("Retrieved temperature: " + temperature);
            msg = String.format("Hey darling, temperature is %f Celsius degrees.", temperature.getTemperature());
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to sense the temperature right now.";
        }
        return sendReplyToLex(msg, FULFILLED, CLOSE, PLAIN_TEXT);
    }
}
