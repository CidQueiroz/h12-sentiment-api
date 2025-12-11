package h12.sentiment.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.service.SentimentAnalysisService;

@RestController
@RequestMapping("/sentiment")
public class SentimentAnalysisController {

  private SentimentAnalysisService service;

  public SentimentAnalysisController(SentimentAnalysisService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<OutputSentimentDTO> createAnalysis(@RequestBody InputSentimentDTO inputSentimentDTO) {
    var sentiment = service.createAnalysis(inputSentimentDTO);
    return ResponseEntity.status(HttpStatus.OK).body(sentiment);
  }

  @GetMapping
  public ResponseEntity<OutputSentimentDTO> getAnalyzed() {
    var sentiment = service.getOneAnalysis();
    return ResponseEntity.status(HttpStatus.OK).body(sentiment);
  }
}
