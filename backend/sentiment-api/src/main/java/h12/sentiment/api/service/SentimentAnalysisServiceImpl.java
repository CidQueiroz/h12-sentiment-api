package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.MicroserviceResponseDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import h12.sentiment.api.repository.SentimentAnalysisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Semaphore;

@Service
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

  private final WebClient sentimentWebClient;
  private final SentimentAnalysisRepository repository;

  @Value("${semaphore.size:18}")
  private int semaphoreSize;

  private Semaphore dbSemaphore;

  public SentimentAnalysisServiceImpl(WebClient sentimentWebClient, SentimentAnalysisRepository repository) {
    this.sentimentWebClient = sentimentWebClient;
    this.repository = repository;
    if (this.semaphoreSize <= 0) {
      this.semaphoreSize = 1;
    }
    this.dbSemaphore = new Semaphore(this.semaphoreSize);
  }

  @Override
  public Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO input) {
    return sentimentWebClient.post()
        .uri("/predict")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(input)
        .retrieve()
        .bodyToMono(MicroserviceResponseDTO.class)
        .flatMap(response -> {
          // monta/atualiza a entity a partir do DTO e da resposta do microservice
          SentimentAnalysisEntity entity = input.toEntity();
          entity.setPrediction(response.previsao());
          entity.setProbability(response.probabilidade());
          entity.setLanguage(response.idioma());

          // salva usando repository reativo (não bloquear)
          return repository.save(entity)
              .map(saved -> new OutputSentimentDTO(saved)); // usa construtor que aceita a entity
        });
  }

  @Override
  public Mono<OutputSentimentDTO> getOneAnalysis() {
    return Mono.just(new OutputSentimentDTO("Positive", 0.99));
  }

  @Override
  public Mono<List<SentimentAnalysisEntity>> getAllAnalyses() {
    // proteção simples com Semaphore: tenta adquirir, senão retorna 503 rapidamente
    return Mono.defer(() -> {
      boolean acquired = dbSemaphore.tryAcquire();
      if (!acquired) {
        return Mono.error(new ResponseStatusException(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE, "DB busy, try later"));
      }

      return repository.findAll()
          .collectList()
          .doFinally(signal -> dbSemaphore.release());
    });
  }
}
