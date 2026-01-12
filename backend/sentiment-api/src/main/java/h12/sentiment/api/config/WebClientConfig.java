package h12.sentiment.api.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  @Value("${microservice.url}")
  private String microserviceUrl;

  @Bean
  public WebClient sentimentWebClient() {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(30))
            .addHandlerLast(new WriteTimeoutHandler(30)))
        .responseTimeout(Duration.ofSeconds(30));

    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    return WebClient.builder()
        .baseUrl(microserviceUrl)
        .clientConnector(connector)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .filter((request, next) -> next.exchange(request)
            .flatMap(response -> {
              if (response.statusCode().isError()) {
                return response.createException().flatMap(Mono::error);
              }
              return Mono.just(response);
            }))
        .build();
  }
}
