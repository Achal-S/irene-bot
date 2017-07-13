package irene.bot.embedded.sensing;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.lex.model.FullfillmentState;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.embedded.sensing.model.Pressure;
import irene.bot.util.MessageUtil;

import static irene.bot.util.MessageUtil.getErrorEmoji;

public class PressureLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

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
            msg = String.format("Hey "+ MessageUtil.getRandomGreeting()+", pressure is %.2f millibars.", pressure.getPressure());
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to sense the pressure right now "+getErrorEmoji();
        }
        return lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
    }
}
