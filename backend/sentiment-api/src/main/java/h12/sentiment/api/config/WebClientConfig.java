package h12.sentiment.api.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  @Value("${microservice.url:https://g064f0b27cc2c88_sentimenth12_high.adb.sa-saopaulo-1.oraclecloud.com}")
  private String microserviceUrl;

  @Bean
  public WebClient sentimentWebClient() {

    HttpClient httpClient = HttpClient.create()
        // Timeout máximo de resposta do microserviço
        .responseTimeout(Duration.ofSeconds(30));

    return WebClient.builder()
        .baseUrl(microserviceUrl)
        .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "application/json")
        .filter((request, next) -> next.exchange(request)
            .flatMap(response -> {
              if (response.statusCode().isError()) {
                return response.bodyToMono(String.class)
                    .defaultIfEmpty("Erro desconhecido no microserviço")
                    .flatMap(body -> Mono.error(
                        new WebClientResponseException(
                            response.statusCode().value(),
                            "Erro ao chamar microserviço",
                            response.headers().asHttpHeaders(),
                            body.getBytes(),
                            null)));
              }
              return Mono.just(response);
            }))
        .build();
  }
}
