package h12.sentiment.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sentimentWebClient(){

        return WebClient.builder().
                baseUrl("http://microservice:8000/predict")
                .build();
    }
}
