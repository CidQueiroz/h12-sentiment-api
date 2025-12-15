package h12.sentiment.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.service.SentimentAnalysisService;

@RestController
@RequestMapping("/sentiment")
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem
public class SentimentAnalysisController {

  private SentimentAnalysisService service;

  public SentimentAnalysisController(SentimentAnalysisService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<OutputSentimentDTO> createAnalysis(@Valid @RequestBody InputSentimentDTO inputSentimentDTO) {
    try {
      var sentiment = service.createAnalysis(inputSentimentDTO);
      return ResponseEntity.status(HttpStatus.OK).body(sentiment);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
  }

}
