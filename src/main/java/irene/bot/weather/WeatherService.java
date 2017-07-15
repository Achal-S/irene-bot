package irene.bot.weather;

import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.util.ApplicationPropertiesUtil;
import irene.bot.weather.model.Forecasts;
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

public class WeatherService extends AbstractEmbeddedClient {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherLambda.class);
    private static final String OPENWEATHER_API_KEY = "openweather.api.key";
    private static final int TIMEOUT = 10000;
    private static final String OPENWEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String METEOGURU_URL = "http://www.meteoguru.com/en/pro/forecast/";

    private final String openWeatherApiKey = ApplicationPropertiesUtil.getProperty(OPENWEATHER_API_KEY, this.getClass());


    public Forecasts getForecasts(Position position) throws IOException {
        final String JSONForecasts = this.getWeatherForecasts(position);
        final Forecasts forecasts = parseResponse(JSONForecasts, Forecasts.class);
        log.info("Retrieved forecasts: " + forecasts);
        return forecasts;
    }

    private String getWeatherForecasts(final Position position) throws IOException {
        log.info("Requesting embedded endpoint with position " + position);
        CloseableHttpResponse httpResponse = null;
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient =
                     HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {

            final HttpGet httpGet = new HttpGet(buildUrl(position, openWeatherApiKey));

            httpResponse = httpclient.execute(httpGet);
            final HttpEntity entity = httpResponse.getEntity();
            final String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("Weather forecast GET endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + httpResponse.getStatusLine().getStatusCode());
            }

            log.info("Weather forecast GET endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(entity);
            return stringResponse;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    private String buildUrl(Position position, final String apiKey) {
        final String url = OPENWEATHER_URL + "?units=metric&lat=" + position.getLatitude() + "&lon=" + position.getLongitude() + "&appid=" + apiKey;
        log.info("Requesting weather forecasts: " + url);
        return url;
    }

    public String getWeatherUrl(final Position position) {
        return String.format(METEOGURU_URL + "?latlon=%s,%s", position.getLatitude(), position.getLongitude());
    }
}