package irene.bot.weather;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.lex.model.FullfillmentState;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.util.ApplicationPropertiesUtil;
import irene.bot.util.MessageUtil;
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

import static irene.bot.util.MessageUtil.getErrorEmoji;

public class WeatherLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherLambda.class);
    private static final String POSITION_PATH = "position";
    private static final String OPENWEATHER_API_KEY = "openweather.api.key";
    private static final int TIMEOUT = 10000;
    private static final String OPENWEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String METEOGURU_URL = "http://www.meteoguru.com/en/pro/forecast/";

    private final String openWeatherApiKey = ApplicationPropertiesUtil.getProperty(OPENWEATHER_API_KEY, this.getClass());

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final String JSONresponse = embeddedEndpointGET(POSITION_PATH);
            final Position position = parseResponse(JSONresponse, Position.class);
            final String JSONForecasts = this.getWeatherForecasts(position);
            final Forecasts forecasts = parseResponse(JSONForecasts, Forecasts.class);

            log.info("Retrieved position: " + position);
            log.info("Retrieved forecasts: " + forecasts);

            msg = buildForecastMessage(forecasts);
            msg = msg+"You can check the weather forecasts here:\n" + this.getWeatherUrl(position)+" "+MessageUtil.getRandomEmoji();
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to locate my position right now thus I am unable to get accurate weather forecasts " + getErrorEmoji();
        }
        return lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
    }


    private String buildForecastMessage(final Forecasts forecasts){
        final StringBuilder stringBuilder = new StringBuilder();

        //General Description
        stringBuilder.append("Current weather for my position is: \"");
        stringBuilder.append(forecasts.getWeather().get(0).getDescription()+"\".\n");

        //Clouds %
        stringBuilder.append(String.format("There are %d %% of clouds in the sky and ",forecasts.getClouds().getAll()));

        //wind
        stringBuilder.append(String.format("wind speed is %.2f meter/sec with direction of %d meteorological degrees.\n", forecasts.getWind().getSpeed(), forecasts.getWind().getDeg()));

        //temp
        stringBuilder.append(String.format("Temperature is %.2f Celsius degrees.\n",forecasts.getMain().getTemp()));

        log.info("Built forecast message: "+stringBuilder.toString());
        return stringBuilder.toString();
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

            final HttpGet httpGet = new HttpGet(buildUrl(position.getLatitude(), position.getLongitude(), openWeatherApiKey));

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

    private String buildUrl(final double latitude, final double longitude, final String apiKey){
        final String url =  OPENWEATHER_URL + "?units=metric&lat=" +latitude+"&lon="+longitude+"&appid="+apiKey;
        log.info("Requesting weather forecasts: "+url);
        return url;
    }

    private String getWeatherUrl(final Position position) {
        return String.format(METEOGURU_URL + "?latlon=%s,%s", position.getLatitude(), position.getLongitude());
    }
}
