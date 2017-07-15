package irene.bot.weather;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.lex.model.FullfillmentState;
import irene.bot.lex.model.LexEvent;
import irene.bot.lex.model.LexResponse;
import irene.bot.util.MessageUtil;
import irene.bot.weather.model.Forecasts;

import static irene.bot.util.MessageUtil.getErrorEmoji;

public class WeatherLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherLambda.class);
    private static final String POSITION_PATH = "position";
    private static final String OPENWEATHER_API_KEY = "openweather.api.key";
    private static final String SNOW = "snow";
    private static final String MIST = "mist";
    private static final String PLURAL_SUFFIX = "s";
    private static final String RAIN = "rain";

    private final WeatherService weatherService = new WeatherService();

    @Override
    public LexResponse handleRequest(final LexEvent lexEvent, final Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        try {
            final String JSONresponse = embeddedEndpointGET(POSITION_PATH);
            final Position position = parseResponse(JSONresponse, Position.class);
            Forecasts forecasts = weatherService.getForecasts(position);
            log.info("Retrieved position: " + position);
            log.info("Retrieved forecasts: " + forecasts);

            msg = buildForecastMessage(forecasts);
            msg = msg + "You can check the weather forecasts here:\n" + weatherService.getWeatherUrl(position) + " " + MessageUtil.getRandomEmoji();
        } catch (Exception e) {
            log.error(e);
            msg = "Sorry, I am unable to locate my position right now thus I am unable to get accurate weather forecasts " + getErrorEmoji();
        }
        return lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FULFILLED);
    }

    private String getIncipit(Forecasts forecasts) {
        //General Description
        if (forecasts.getWeather().get(0).getDescription().endsWith(PLURAL_SUFFIX)) {
            return "Where I am there are ";
        } else {
            if (forecasts.getWeather().get(0).getDescription().equals(SNOW) || forecasts.getWeather().get(0).getDescription().equals(MIST) || forecasts.getWeather().get(0).getDescription().equals(RAIN)) {
                return "Where I am there is ";
            } else {
                return "Where I am there is a ";
            }
        }
    }

    private String buildForecastMessage(final Forecasts forecasts) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getIncipit(forecasts));
        //General
        stringBuilder.append(forecasts.getWeather().get(0).getDescription());
        //Clouds %
        stringBuilder.append(String.format(" and, more precisely, there are %d %% of clouds in the sky.\n", forecasts.getClouds().getAll()));
        //wind
        stringBuilder.append(String.format("Wind speed is %.2f m/s, blowing direction is %d degrees.\n", forecasts.getWind().getSpeed(), forecasts.getWind().getDeg()));
        //temp
        stringBuilder.append(String.format("Temperature is %.2f Celsius degrees.\n", forecasts.getMain().getTemp()));
        log.info("Built forecast message: " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
