package gov.nysenate.ess.core.config;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiClientTokenProvider;
import gov.nysenate.ess.core.util.OutputUtils;
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
    public CloseableHttpClient everfiHttpClient() {
        return HttpClients.custom()
                .disableCookieManagement()
                .build();
    }

    @Bean
    public EverfiClientTokenProvider everfiClientTokenProvider(
            @Value("${pec.everfi.client.id:}") String clientId,
            @Value("${pec.everfi.client.secret:}") String clientSecret,
            CloseableHttpClient everfiHttpClient) {
        return new EverfiClientTokenProvider(
                everfiHttpClient,
                OutputUtils.jsonMapper,
                clientId,
                clientSecret,
                Clock.systemUTC(),
                Duration.ofMinutes(1)
        );
    }
}
