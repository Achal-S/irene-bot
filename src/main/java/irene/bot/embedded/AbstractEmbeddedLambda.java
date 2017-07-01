package irene.bot.embedded;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import irene.bot.embedded.model.DialogAction;
import irene.bot.embedded.model.LexEvent;
import irene.bot.embedded.model.LexResponse;
import irene.bot.embedded.model.Message;
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

public abstract class AbstractEmbeddedLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractEmbeddedLambda.class);
    private static final String IRENE_EMBEDDED_BASE_URL = "http://34.203.111.203:4990/";
    private static final int TIMEOUT = 10000;

    protected static final String FULFILLED = "Fulfilled";
    protected static final String CLOSE = "Close";
    protected static final String PLAIN_TEXT = "PlainText";

    public abstract LexResponse handleRequest(final LexEvent lexEvent, final Context context);

    protected LexResponse sendReplyToLex(final String msg, final String fullfillmentState, final String type, final String contentType) {
        final LexResponse lexResponse = new LexResponse();
        final DialogAction dialogAction = new DialogAction();
        final Message message = new Message();

        message.setContentType(contentType);
        message.setContent(msg);

        dialogAction.setFulfillmentState(fullfillmentState);
        dialogAction.setType(type);
        dialogAction.setMessage(message);

        lexResponse.setDialogAction(dialogAction);
        return lexResponse;
    }

    protected <T> T parseResponse(final String positionJSON, final Class<T> clazz) throws IOException {
        final Gson gson = new Gson();
        final T result = gson.fromJson(positionJSON, clazz);
        log.debug("Parsed JSON response is: " + result);
        return result;
    }

    protected String retrieveSensorReading(final String path) throws IOException {
        log.info("Requesting sensor reading from path " + path);
        CloseableHttpResponse httpResponse = null;
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient =
                     HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {

            HttpGet httpGet = new HttpGet(IRENE_EMBEDDED_BASE_URL + path);
            httpResponse = httpclient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("Received sensor reading with status code: " + httpResponse.getStatusLine().getStatusCode());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + httpResponse.getStatusLine().getStatusCode());
            }

            log.info("Received sensor reading with status code: " + httpResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(entity);
            return stringResponse;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }
}
