package gov.nysenate.ess.core.config;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiClientTokenProvider;
import gov.nysenate.ess.core.util.OutputUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class EverfiClientConfig {

    @Bean
    public CloseableHttpClient everfiHttpClient(
            @Value("${everfi.http.connect-timeout-ms:10000}") int connectTimeoutMs,
            @Value("${everfi.http.connection-request-timeout-ms:10000}") int connectionRequestTimeoutMs,
            @Value("${everfi.http.socket-timeout-ms:60000}") int socketTimeoutMs) {
        requirePositiveTimeout("everfi.http.connect-timeout-ms", connectTimeoutMs);
        requirePositiveTimeout("everfi.http.connection-request-timeout-ms", connectionRequestTimeoutMs);
        requirePositiveTimeout("everfi.http.socket-timeout-ms", socketTimeoutMs);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeoutMs)
                .setConnectionRequestTimeout(connectionRequestTimeoutMs)
                .setSocketTimeout(socketTimeoutMs)
                .build();
        return HttpClients.custom()
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private static void requirePositiveTimeout(String propertyName, int timeoutMs) {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException(propertyName + " must be positive.");
        }
    }

    @Bean
    public EverfiClientTokenProvider everfiClientTokenProvider(
            @Value("${pec.everfi.client.id:}") String clientId,
            @Value("${pec.everfi.client.secret:}") String clientSecret,
            @Value("${pec.everfi.oauth.token-url:https://api.fifoundry.net/oauth/token}") String tokenUrl,
            CloseableHttpClient everfiHttpClient) {
        return new EverfiClientTokenProvider(
                everfiHttpClient,
                OutputUtils.jsonMapper,
                clientId,
                clientSecret,
                tokenUrl,
                Clock.systemUTC(),
                Duration.ofMinutes(1)
        );
    }
}
