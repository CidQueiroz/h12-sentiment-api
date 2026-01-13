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

import java.util.List;
import java.util.concurrent.Semaphore;

@Service
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

  private final WebClient sentimentWebClient;
  private final SentimentAnalysisRepository repository;

  @Value("${semaphore.size:18}")
  private int semaphoreSize;

  private final Semaphore dbSemaphore;

  public SentimentAnalysisServiceImpl(WebClient sentimentWebClient, SentimentAnalysisRepository repository) {
    this.sentimentWebClient = sentimentWebClient;
    this.repository = repository;
    if (this.semaphoreSize <= 0) {
      this.semaphoreSize = 1;
    }
    this.dbSemaphore = new Semaphore(this.semaphoreSize);
  }

  @Override
  public OutputSentimentDTO createAnalysis(InputSentimentDTO input) {
    // chama o microservice e bloqueia para esperar a resposta
    MicroserviceResponseDTO response;
    try {
      response = sentimentWebClient.post()
              .uri("/predict")
              .accept(MediaType.APPLICATION_JSON)
              .bodyValue(input)
              .retrieve()
              .bodyToMono(MicroserviceResponseDTO.class)
              .block(); // bloqueante
    } catch (Exception e) {
      throw new ResponseStatusException(
              org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
              "Microservice unavailable", e
      );
    }

    // monta/atualiza a entity a partir do DTO e da resposta do microservice
    SentimentAnalysisEntity entity = input.toEntity();
    entity.setPrediction(response.previsao());
    entity.setProbability(response.probabilidade());
    entity.setLanguage(response.idioma());

    // proteção com semaphore
    boolean acquired = false;
    try {
      acquired = dbSemaphore.tryAcquire();
      if (!acquired) {
        throw new ResponseStatusException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                "DB busy, try later"
        );
      }

      // salva usando JPA repository (bloqueante)
      SentimentAnalysisEntity saved = repository.save(entity);

      // retorna DTO baseado na entity salva
      return new OutputSentimentDTO(saved);

    } finally {
      if (acquired) {
        dbSemaphore.release();
      }
    }
  }

  @Override
  public OutputSentimentDTO getOneAnalysis() {
    return new OutputSentimentDTO("Positive", 0.99);
  }

  @Override
  public List<SentimentAnalysisEntity> getAllAnalyses() {
    boolean acquired = dbSemaphore.tryAcquire();
    if (!acquired) {
      throw new ResponseStatusException(
              org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
              "DB busy, try later"
      );
    }

    try {
      return repository.findAll(); // retorna diretamente a lista
    } finally {
      dbSemaphore.release();
    }
  }
}
