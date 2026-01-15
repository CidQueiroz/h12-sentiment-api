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

import h12.sentiment.api.dto.ChartDataDTO;
import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.KpiDTO;
import h12.sentiment.api.dto.ModelUsageDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDistributionDTO;
import h12.sentiment.api.dto.StackedChartDataDTO;
import h12.sentiment.api.service.SentimentAnalysisService;

import h12.sentiment.api.entity.SentimentAnalysisEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/sentiment")
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
  public Mono<Page<SentimentAnalysisEntity>> getAllAnalyses(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "createdAt") String sortBy,
          @RequestParam(defaultValue = "desc") String sortDirection) {
      Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
      Pageable pageable = PageRequest.of(page, size, sort);
      return service.getAllAnalyses(pageable);
  }

  @GetMapping("/analytics/distribution")
  public Mono<SentimentDistributionDTO> getSentimentDistribution() {
      return service.getSentimentDistribution();
  }

  @GetMapping("/analytics/models")
  public Mono<ModelUsageDTO> getModelUsage() {
      return service.getModelUsage();
  }

  @GetMapping("/analytics/kpis")
  public Mono<KpiDTO> getKpis() {
      return service.getKpis();
  }

  @GetMapping("/analytics/languages")
  public Mono<ChartDataDTO> getLanguageDistribution() {
      return service.getLanguageDistribution();
  }

  @GetMapping("/analytics/hourly-peak")
  public Mono<ChartDataDTO> getHourlyDistribution() {
      return service.getHourlyDistribution();
  }

  @GetMapping("/analytics/sentiment-by-model")
  public Mono<StackedChartDataDTO> getSentimentByModel() {
      return service.getSentimentByModel();
  }

  @GetMapping("/analytics/confidence-levels")
  public Mono<ChartDataDTO> getConfidenceLevels() {
      return service.getConfidenceLevels();
  }

  @GetMapping("/analytics/feedback-length")
  public Mono<ChartDataDTO> getFeedbackLength() {
      return service.getFeedbackLength();
  }
}
