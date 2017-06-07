package irene.geocoding.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import irene.geocoding.model.GeocodingRequest;
import irene.geocoding.model.GeocodingResponse;

import java.io.IOException;

public class GeocodingLamdba extends AbstractLambdaRequest implements RequestHandler<GeocodingRequest, GeocodingResponse> {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GeocodingRequest.class);

    private double[] geoCode(final String address) throws InterruptedException, ApiException, IOException {
        final GeoApiContext context = new GeoApiContext().setApiKey(getApiKey());
        final GeocodingResult[] results = GeocodingApi.geocode(context,
                address).await();
        return new double[]{results[0].geometry.location.lat, results[0].geometry.location.lng};
    }

    @Override
    public GeocodingResponse handleRequest(final GeocodingRequest geocodingRequest, final Context context) {
        GeocodingResponse geocodingResponse;
        try {
            log.info("Requested geocoding for: " + geocodingRequest.getAddress());
            double[] coordinates = this.geoCode(geocodingRequest.getAddress());
            geocodingResponse = new GeocodingResponse(coordinates[0], coordinates[1], true, null);
            log.info("Obtained geocoding: " + geocodingResponse.getLatitude()+","+geocodingResponse.getLongitude());
        } catch (final Exception e) {
            log.error(e.getMessage());
            geocodingResponse = new GeocodingResponse(0, 0, false, e.getMessage());
        }
        return geocodingResponse;
    }
}
