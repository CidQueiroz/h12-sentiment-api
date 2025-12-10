package h12.sentiment.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.service.SentimentAnalysisService;

@RestController
@RequestMapping("sentiment")
public class SentimentAnalysisController {

  @Autowired
  private SentimentAnalysisService service;

  @GetMapping
  public ResponseEntity<OutputSentimentDTO> getAnalyzed() {
    var sentiment = service.getOneAnalysis();
    return ResponseEntity.status(HttpStatus.OK).body(sentiment);
  }
}
