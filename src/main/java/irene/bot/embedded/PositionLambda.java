package irene.bot.embedded;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.model.LexEvent;
import irene.bot.embedded.model.LexResponse;
import irene.bot.embedded.model.Position;
import irene.bot.map.GeocodingService;

public class PositionLambda extends AbstractEmbeddedLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PositionLambda.class);
    private static final String POSITION_PATH = "position";

    private GeocodingService geocodingService = new GeocodingService();


    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final String JSONresponse = retrieveSensorReading(POSITION_PATH);
            final Position position = parseResponse(JSONresponse, Position.class);
            final String address = reverseGeoCode(position);
            final String mapUrl = getMapUrl(position);
            log.info("Retrieved position: " + position);
            msg = String.format("Hey darling, my position is %s.\nCheck it out on a map: %s", address, mapUrl);
        } catch (Exception e) {
            msg = "Sorry, I am unable to locate my position right now: satellites are unpredictable.";
        }
        return sendReplyToLex(msg, FULFILLED, CLOSE, PLAIN_TEXT);
    }


    private String reverseGeoCode(final Position position) {
        return this.geocodingService.reverseGeocode(position.getLatitude(), position.getLongitude()).getAddress();
    }

    private String getMapUrl(final Position position) {
        return this.geocodingService.getMapURL(position.getLatitude(), position.getLongitude());
    }
}
