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
   * Retorna o histórico de análises como Mono<List<SentimentDetailedDTO>>
   */
  @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<List<SentimentDetailedDTO>> getAllAnalyses() {
    return service.getAllAnalyses()
        .map(list -> list.stream()
            .map(SentimentDetailedDTO::new) // adapta se o construtor aceitar entity
            .collect(Collectors.toList()))
        .onErrorResume(ex -> {
          // Se for ResponseStatusException (503), deixe-o propagar para o cliente com
          // status 503.
          // Caso queira que o endpoint retorne lista vazia em vez de erro, descomente a
          // linha abaixo:
          // return Mono.just(Collections.emptyList());
          return Mono.error(ex);
        });
  }
}
