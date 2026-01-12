package h12.sentiment.api.controller;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDetailedDTO;
import h12.sentiment.api.service.SentimentAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller reativo para análise de sentimento.
 */
@RestController
@RequestMapping("/sentiment")
@CrossOrigin(origins = "*")
public class SentimentAnalysisController {

  private final SentimentAnalysisService service;

  public SentimentAnalysisController(SentimentAnalysisService service) {
    this.service = service;
  }

  /**
   * POST /sentiment
   * Cria uma nova análise de sentimento.
   */
  @PostMapping
  public Mono<ResponseEntity<OutputSentimentDTO>> createAnalysis(
      @Valid @RequestBody InputSentimentDTO inputSentimentDTO) {

    return service.createAnalysis(inputSentimentDTO)
        .map(ResponseEntity::ok)
        .onErrorResume(e -> Mono.just(
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new OutputSentimentDTO("serviço_indisponivel", 0.0))));
  }

  /**
   * GET /sentiment/history
   * Retorna o histórico de análises em streaming (Flux).
   */
  @GetMapping("/history")
  public Flux<SentimentDetailedDTO> getAllAnalyses() {

    return service.getAllAnalyses()
        .map(SentimentDetailedDTO::new)
        .onErrorResume(e -> {
          // log opcional
          return Flux.empty();
        });
  }
}
