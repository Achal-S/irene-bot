package irene.bot.embedded.safety;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.model.Status;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.map.GeocodingService;
import irene.bot.text.TextingService;


public class SafetyNotificationLambda extends AbstractEmbeddedClient implements RequestHandler<Status, String> {

    private static final String POSITION_PATH = "position";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SafetyNotificationLambda.class);

    private TextingService textingService = new TextingService();
    private GeocodingService geocodingService = new GeocodingService();


    @Override
    public String handleRequest(final Status status, final Context context) {
        try {
            log.info("Received status for alarm notification:" + status);
            final String JSONresponse = embeddedEndpointGET(POSITION_PATH);
            final Position position = parseResponse(JSONresponse, Position.class);

            if (position.isSuccess()) {
                final String mapUrl = getMapUrl(position);
                String msg = String.format("Hi, I am Irene the motorbike bot. The bike seems to be on the ground at these coordinates: [%f, %f]. Check them out on a map: %s", position.getLatitude(), position.getLongitude(), mapUrl);
                return textingService.sendTextNotification(msg, status.getTextNotification());
            } else {
                return "Position tracking failed: " + position.getMessage();
            }
        } catch (Exception e) {
            log.error(e);
            return "Error during text notification: " + e.getMessage();
        }
    }

    private String reverseGeoCode(final Position position) {
        return this.geocodingService.reverseGeocode(position.getLatitude(), position.getLongitude()).getAddress();
    }

    private String getMapUrl(final Position position) {
        return this.geocodingService.getMapURL(position.getLatitude(), position.getLongitude());
    }
}
