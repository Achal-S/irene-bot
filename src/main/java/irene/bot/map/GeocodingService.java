package irene.bot.map;

import com.google.gson.Gson;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import irene.bot.map.model.GeocodingResponse;
import irene.bot.map.model.ReverseGeocodingResponse;
import irene.bot.util.ApplicationPropertiesUtil;
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

public class GeocodingService {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GeocodingService.class);
    private static final String GOOGLE_MAP_BASE_URL = "https://maps.google.com?q=";
    private static final String GOOGLE_MAP_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    private static final String API_KEY = "api.key";
    private static final int TIMEOUT = 13000;
    private static final String PLACE_QUERY = "&rankBy=distance&query=";//&location=";
    private static final String LOCATION = "&location=";
    private final String apiKey = ApplicationPropertiesUtil.getProperty(API_KEY, this.getClass());

    private double[] geocodeApi(final String address) throws InterruptedException, ApiException, IOException {
        final GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
        final GeocodingResult[] results = GeocodingApi.geocode(context,
                address).await();
        return new double[]{results[0].geometry.location.lat, results[0].geometry.location.lng};
    }

    private String reverseGeocodeApi(final double latitude, final double longitude) throws InterruptedException, ApiException, IOException {
        final GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
        final LatLng latLng = new LatLng(latitude, longitude);
        final GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).await();
        return results[0].formattedAddress;
    }

    public PlacesSearchResponse getPlace(final String place, final double latitude, final double longitude) throws IOException, InterruptedException, ApiException {
        CloseableHttpResponse httpResponse = null;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = GOOGLE_MAP_API_BASE_URL + "key=" + apiKey + PLACE_QUERY +place+LOCATION+ latitude + "," + longitude + "&radius=2000";
            log.info("Getting places with url " + url);
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpclient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("Embedded GET endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + httpResponse.getStatusLine().getStatusCode());
            }

            log.info("Embedded GET endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(entity);

            return parseResponse(stringResponse, PlacesSearchResponse.class);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    private <T> T parseResponse(final String positionJSON, final Class<T> clazz) throws IOException {
        final Gson gson = new Gson();
        final T result = gson.fromJson(positionJSON, clazz);
        log.debug("Parsed JSON response is: " + result);
        return result;
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

    public String getMapURL(final double latitude, final double longitude) {
        final StringBuilder stringBuilder = new StringBuilder(GOOGLE_MAP_BASE_URL);
        stringBuilder.append(latitude);
        stringBuilder.append(",");
        stringBuilder.append(longitude);
        return stringBuilder.toString();
    }


    public ReverseGeocodingResponse reverseGeocode(final double latitude, final double longitude) {
        ReverseGeocodingResponse reverseGeocodingResponse;
        try {
            log.info("Requested reverse geocoding for: " + latitude + "," + longitude);
            final String result = this.reverseGeocodeApi(latitude, longitude);
            reverseGeocodingResponse = new ReverseGeocodingResponse(result, true, null);
            log.info("Obtained reverse geocoding: " + reverseGeocodingResponse.getAddress());
        } catch (final Exception e) {
            log.error(e.getMessage());
            reverseGeocodingResponse = new ReverseGeocodingResponse(null, false, e.getMessage());
        }
        return reverseGeocodingResponse;
    }
}
