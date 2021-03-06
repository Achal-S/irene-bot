package irene.bot.messaging;


import com.google.gson.Gson;
import irene.bot.messaging.model.AuthenticationResponse;
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

public class AuthenticationService {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AuthenticationService.class);

    private static final String MS_AUTH_URL = "https://login.microsoftonline.com/botframework.com/oauth2/v2.0/token";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String SCOPE = "scope";
    private static final String SCOPE_URL = "https://api.botframework.com/.default";
    private static final String APPLICATION_ID = "application.id";
    private static final String SECRET = "secret";
    private static final int TIMEOUT = 10000;

    private final String applicationId = ApplicationPropertiesUtil.getProperty(APPLICATION_ID, this.getClass());
    private final String secret = ApplicationPropertiesUtil.getProperty(SECRET, this.getClass());

    public AuthenticationResponse authenticate() throws IOException {
        CloseableHttpResponse httpResponse = null;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();

        try (CloseableHttpClient httpclient =
                     HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(GRANT_TYPE, CLIENT_CREDENTIALS));
            params.add(new BasicNameValuePair(CLIENT_ID, applicationId));
            params.add(new BasicNameValuePair(CLIENT_SECRET, secret));
            params.add(new BasicNameValuePair(SCOPE, SCOPE_URL));
            HttpPost httpPost = new HttpPost(MS_AUTH_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            log.info(String.format("Submitting authentication with: %s %s", applicationId, secret));
            httpResponse = httpclient.execute(httpPost);

            log.info("Response from MS bot framework with status code: " + httpResponse.getStatusLine().getStatusCode());
            HttpEntity entity = httpResponse.getEntity();

            String stringResponse = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());

            Gson gson = new Gson();
            AuthenticationResponse authenticationResponse = gson.fromJson(stringResponse, AuthenticationResponse.class);

            EntityUtils.consume(entity);
            return authenticationResponse;

        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }
}
