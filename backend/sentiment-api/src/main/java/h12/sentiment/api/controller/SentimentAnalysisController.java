package h12.sentiment.api.controller;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDetailedDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import h12.sentiment.api.service.SentimentAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
  public ResponseEntity<OutputSentimentDTO> createAnalysis(
          @Valid @RequestBody InputSentimentDTO inputSentimentDTO) {

    try {
      OutputSentimentDTO output = service.createAnalysis(inputSentimentDTO);
      return ResponseEntity.ok(output);
    } catch (Exception e) {
      // tratamento simples de erro
      OutputSentimentDTO errorOutput = new OutputSentimentDTO("serviço_indisponivel", 0.0);
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorOutput);
    }
  }

  /**
   * GET /sentiment/history
   * Retorna o histórico de análises.
   */
  @GetMapping("/history")
  public ResponseEntity<List<SentimentDetailedDTO>> getAllAnalyses() {
    try {
      List<SentimentAnalysisEntity> entities = service.getAllAnalyses();
      List<SentimentDetailedDTO> result = entities.stream()
              .map(SentimentDetailedDTO::new) // adapta se o construtor aceitar entity
              .collect(Collectors.toList());
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
  }
}
