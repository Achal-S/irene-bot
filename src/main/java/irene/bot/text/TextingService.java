package irene.bot.text;

import irene.bot.util.ApplicationPropertiesUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextingService {

    private static final int TIMEOUT = 10000;
    private static final String endpoint = "http://gateway.skebby.it/api/send/smseasy/advanced/http.php";
    private static final String METHOD = "method";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SENDER_STRING = "sender_string";
    private static final String TEXT = "text";
    private static final String CHARSET = "charset";
    private static final String SENDER_NAME = "Irene";
    //    private static final String SEND_SMS_CLASSIC = "send_sms_classic";
    private static final String SEND_SMS_CLASSIC = "test_send_sms_classic";
    private static final String RECIPIENTS = "recipients[]";
    private static final String SKEBBY_USERNAME_PROPERTY = "skebby.username";
    private static final String SKEBBY_PASSWORD_PROPERTY = "skebby.password";
    private static final String STATUS_SUCCESS = "status=success";

    private final String SKEBBY_USERNAME = ApplicationPropertiesUtil.getProperty(SKEBBY_USERNAME_PROPERTY, this.getClass());
    private final String SKEBBY_PWD = ApplicationPropertiesUtil.getProperty(SKEBBY_PASSWORD_PROPERTY, this.getClass());

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TextingService.class);


    public String sendTextNotification(final String message, final String... recipients) throws IOException {
        CloseableHttpResponse httpResponse = null;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            List<NameValuePair> formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair(METHOD, SEND_SMS_CLASSIC));
            formParams.add(new BasicNameValuePair(USERNAME, SKEBBY_USERNAME));
            formParams.add(new BasicNameValuePair(PASSWORD, SKEBBY_PWD));
            formParams.add(new BasicNameValuePair(SENDER_STRING, SENDER_NAME));
            formParams.add(new BasicNameValuePair(TEXT, message));
            formParams.add(new BasicNameValuePair(CHARSET, StandardCharsets.UTF_8.name()));

            for (String recipient : recipients) {
                formParams.add(new BasicNameValuePair(RECIPIENTS, recipient));
                log.info("Sending text notification to number: " + recipient);
            }

            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8.name()));
            httpResponse = httpclient.execute(httpPost);

            log.info("Response from SMS provider with status code: " + httpResponse.getStatusLine().getStatusCode());
            HttpEntity entity = httpResponse.getEntity();
            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            EntityUtils.consume(entity);

            if (stringResponse.contains(STATUS_SUCCESS)) {
                log.info("Successful response from SMS provider: " + stringResponse);
            } else {
                log.error("Error from SMS provider: " + stringResponse);
            }
            return stringResponse;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TextingService textingService = new TextingService();
        textingService.sendTextNotification("this is a test", "393454132959");
    }
}
