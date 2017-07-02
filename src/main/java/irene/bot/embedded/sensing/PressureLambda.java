package irene.bot.embedded.sensing;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedLambda;
import irene.bot.embedded.model.LexEvent;
import irene.bot.embedded.model.LexResponse;
import irene.bot.embedded.sensing.model.Pressure;

public class PressureLambda extends AbstractEmbeddedLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PressureLambda.class);
    private static final String PRESSURE_PATH = "pressure";

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final String JSONresponse = embeddedEndpointGET(PRESSURE_PATH);
            final Pressure pressure = parseResponse(JSONresponse, Pressure.class);
            log.info("Retrieved pressure: " + pressure);
            msg = String.format("Hey darling, pressure is %f millibars.", pressure.getPressure());
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to sense the pressure right now.";
        }
        return sendReplyToLex(msg, FULFILLED, CLOSE, PLAIN_TEXT);
    }
}
