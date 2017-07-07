package irene.bot.embedded;

import com.google.gson.Gson;
import irene.bot.lex.LexFullfillmentService;
import irene.bot.util.ApplicationPropertiesUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractEmbeddedClient {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractEmbeddedClient.class);
    private static final String IRENE_EMBEDDED_URL_PROPERTY = "irene.embedded.url";
    private static final int TIMEOUT = 10000;
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-type";

    private final String IRENE_EMBEDDED_BASE_URL = ApplicationPropertiesUtil.getProperty(IRENE_EMBEDDED_URL_PROPERTY, this.getClass());
    protected final LexFullfillmentService lexFullfillmentService = new LexFullfillmentService();


    protected <T> T parseResponse(final String positionJSON, final Class<T> clazz) throws IOException {
        final Gson gson = new Gson();
        final T result = gson.fromJson(positionJSON, clazz);
        log.debug("Parsed JSON response is: " + result);
        return result;
    }

    protected String embeddedEndpointGET(final String path) throws IOException {
        log.info("Requesting embedded endpoint with path " + path);
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
                log.error("Embedded GET endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + httpResponse.getStatusLine().getStatusCode());
            }

            log.info("Embedded GET endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(entity);
            return stringResponse;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    private <T> String instantiateBodyString(T body) {
        Gson gson = new Gson();
        return gson.toJson(body);
    }

    protected <T> String embeddedEndpointPOST(final String path, T body) throws IOException {
        log.info("Requesting embedded endpoint with path " + path);
        CloseableHttpResponse httpResponse = null;
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient =
                     HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {

            HttpPost httpPost = new HttpPost(IRENE_EMBEDDED_BASE_URL + path);
            String bodyString = instantiateBodyString(body);
            httpPost.setEntity(new StringEntity(bodyString, APPLICATION_JSON));

            httpResponse = httpclient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("Embedded POST endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + httpResponse.getStatusLine().getStatusCode());
            }

            log.info("Embedded POST endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(entity);
            return stringResponse;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }


    protected <T> String embeddedEndpointPUT(final String path, T body) throws IOException {
        log.info("Requesting embedded endpoint with path " + path);
        CloseableHttpResponse httpResponse = null;
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient =
                     HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {

            HttpPut httpPut = new HttpPut(IRENE_EMBEDDED_BASE_URL + path);
            String bodyString = instantiateBodyString(body);
            log.info("PUTing string: " + bodyString);
            httpPut.setEntity(new StringEntity(bodyString, StandardCharsets.UTF_8.name()));
            httpPut.setHeader(CONTENT_TYPE, APPLICATION_JSON);

            httpResponse = httpclient.execute(httpPut);
            HttpEntity entity = httpResponse.getEntity();
            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("Embedded PUT endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Response is not successful: " + httpResponse.getStatusLine().getStatusCode());
            }

            log.info("Embedded PUT endpoint invoked with status code: " + httpResponse.getStatusLine().getStatusCode());
            EntityUtils.consume(entity);
            return stringResponse;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }
}
