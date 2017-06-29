package irene.bot.map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import irene.bot.model.*;

import java.io.IOException;

public class GeocodingService extends AbstractGoogleMapService {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GeocodingService.class);
    public static final String GOOGLE_MAP_BASE_URL = "https://maps.google.com?q=";

    private double[] geocodeApi(final String address) throws InterruptedException, ApiException, IOException {
        final GeoApiContext context = new GeoApiContext().setApiKey(getApiKey());
        final GeocodingResult[] results = GeocodingApi.geocode(context,
                address).await();
        return new double[]{results[0].geometry.location.lat, results[0].geometry.location.lng};
    }

    private String reverseGeocodeApi(final double latitude, final double longitude) throws InterruptedException, ApiException, IOException {
        final GeoApiContext context = new GeoApiContext().setApiKey(getApiKey());
        final LatLng latLng = new LatLng(latitude, longitude);
        final GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).await();
        return results[0].formattedAddress;
    }

    public GeocodingResponse geoCode(final String address) {
        GeocodingResponse geocodingResponse;
        try {
            log.info("Requested geocoding for address: " + address);
            double[] coordinates = this.geocodeApi(address);
            geocodingResponse = new GeocodingResponse(coordinates[0], coordinates[1], true, null);
            log.info("Obtained geocoding: " + geocodingResponse.getLatitude() + "," + geocodingResponse.getLongitude());
        } catch (final Exception e) {
            log.error(e.getMessage());
            geocodingResponse = new GeocodingResponse(0, 0, false, e.getMessage());
        }
        return geocodingResponse;
    }

    public String getMapURL(Position position){
        StringBuilder stringBuilder = new StringBuilder(GOOGLE_MAP_BASE_URL);
        stringBuilder.append(Double.valueOf(position.getLatitude()));
        stringBuilder.append(",");
        stringBuilder.append(Double.valueOf(position.getLongitude()));
        return stringBuilder.toString();
    }



    public ReverseGeocodingResponse reverseGeocode(final Position position) {
        ReverseGeocodingResponse reverseGeocodingResponse;
        try {
            log.info("Requested reverse geocoding for: " + position.getLatitude() + "," + position.getLongitude());
            final String result = this.reverseGeocodeApi(position.getLatitude(), position.getLongitude());
            reverseGeocodingResponse = new ReverseGeocodingResponse(result, true, null);
            log.info("Obtained reverse geocoding: " + reverseGeocodingResponse.getAddress());
        } catch (final Exception e) {
            log.error(e.getMessage());
            reverseGeocodingResponse = new ReverseGeocodingResponse(null, false, e.getMessage());
        }
        return reverseGeocodingResponse;
    }
}
