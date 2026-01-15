package h12.sentiment.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.service.SentimentAnalysisService;

import h12.sentiment.api.entity.SentimentAnalysisEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/sentiment")
@CrossOrigin(origins = "*")
public class SentimentAnalysisController {

  private SentimentAnalysisService service;

  public SentimentAnalysisController(SentimentAnalysisService service) {
    this.service = service;
  }

  @PostMapping
  public Mono<ResponseEntity<OutputSentimentDTO>> createAnalysis(@Valid @RequestBody InputSentimentDTO inputSentimentDTO) {
      return service.createAnalysis(inputSentimentDTO)
              .map(sentiment -> ResponseEntity.status(HttpStatus.OK).body(sentiment))
              .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()));
  }

  @GetMapping("/history")
  public Mono<List<SentimentAnalysisEntity>> getAllAnalyses() {
      return service.getAllAnalyses();
  }

}
