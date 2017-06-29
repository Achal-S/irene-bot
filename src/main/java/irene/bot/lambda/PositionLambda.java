package irene.bot.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import irene.bot.map.GeocodingService;
import irene.bot.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PositionLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PositionLambda.class);
    private static final String IRENE_EMBEDDED_BASE_URL = "http://34.203.111.203:4990/";
    private static final String POSITION_PATH = "position";
    private static final int TIMEOUT = 5000;


    private GeocodingService geocodingService = new GeocodingService();


    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            Position position = this.retrieveVehiclePosition();
            String address = this.reverseGeoCode(position);
            String mapUrl = this.getMapUrl(position);
            msg = String.format("Hey darling, my position is %s.\nCheck it out on a map: %s", address, mapUrl);
        } catch (Exception e) {
            msg = "Sorry, I am unable to locate my position right now: satellites are unpredictable.";
        }

        LexResponse lexResponse = new LexResponse();
        DialogAction dialogAction = new DialogAction();
        dialogAction.setFulfillmentState("Fulfilled");
        dialogAction.setType("Close");
        lexResponse.setDialogAction(dialogAction);
        Message message = new Message();
        message.setContentType("PlainText");
        message.setContent(msg);
        dialogAction.setMessage(message);
        return lexResponse;
    }

    protected Position retrieveVehiclePosition() throws IOException {
        log.info("Requesting vehicle position");
        CloseableHttpResponse httpResponse = null;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient =
                     HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {


            HttpGet httpGet = new HttpGet(IRENE_EMBEDDED_BASE_URL + POSITION_PATH);
            httpResponse = httpclient.execute(httpGet);

            log.info("Position response from vehicle with status code: " + httpResponse.getStatusLine().getStatusCode());
            HttpEntity entity = httpResponse.getEntity();

            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());
            Position position = parsePositionResponse(stringResponse);

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK || !position.isSuccess()) {
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + position.getMessage());
            }

            EntityUtils.consume(entity);
            return position;

        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    private Position parsePositionResponse(String positionJSON) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Position position = objectMapper.readValue(positionJSON, Position.class);
        log.info("Position is: " + position);
        return position;
    }


    private String reverseGeoCode(Position position) {
        return this.geocodingService.reverseGeocode(position).getAddress();
    }

    private String getMapUrl(Position position) {
        return this.geocodingService.getMapURL(position);
    }
}
