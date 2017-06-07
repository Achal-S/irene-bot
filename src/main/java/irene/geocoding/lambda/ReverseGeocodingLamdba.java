package irene.geocoding.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import irene.geocoding.model.GeocodingRequest;
import irene.geocoding.model.ReverseGeocodingRequest;
import irene.geocoding.model.ReverseGeocodingResponse;

import java.io.IOException;

public class ReverseGeocodingLamdba extends AbstractLambdaRequest implements RequestHandler<ReverseGeocodingRequest, ReverseGeocodingResponse> {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GeocodingRequest.class);


    private String reverseGeoCode(final double latitude, final double longitude) throws InterruptedException, ApiException, IOException {
        final GeoApiContext context = new GeoApiContext().setApiKey(getApiKey());
        final LatLng latLng = new LatLng(latitude, longitude);
        final GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).await();
        return results[0].formattedAddress;
    }

    @Override
    public ReverseGeocodingResponse handleRequest(final ReverseGeocodingRequest reverseGeocodingRequest, final Context context) {
        ReverseGeocodingResponse reverseGeocodingResponse;
        try {
            log.info("Requested reverse geocoding for: " + reverseGeocodingRequest.getLatitude() + "," + reverseGeocodingRequest.getLongitude());
            final String result = this.reverseGeoCode(reverseGeocodingRequest.getLatitude(), reverseGeocodingRequest.getLongitude());
            reverseGeocodingResponse = new ReverseGeocodingResponse(result, true, null);
            log.info("Obtained reverse geocoding: " + reverseGeocodingResponse.getAddress());
        } catch (final Exception e) {
            log.error(e.getMessage());
            reverseGeocodingResponse = new ReverseGeocodingResponse(null, false, e.getMessage());
        }
        return reverseGeocodingResponse;
    }
}
