package h12.sentiment.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  // Lê a URL do microserviço da variável de ambiente ou application.properties
  @Value("${microservice.url:http://microservice:8000}")
  private String microserviceUrl;

  @Bean
  public WebClient sentimentWebClient() {

    return WebClient.builder().baseUrl(microserviceUrl)
        .build();
  }
}
