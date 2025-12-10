package h12.sentiment.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDetailedDTO;
import h12.sentiment.api.dto.UpdateAnalyzedSentimentDTO;
import h12.sentiment.api.service.SentimentAnalysisService;

@RestController
@RequestMapping("sentiment")
public class SentimentAnalysisController {

  @Autowired
  SentimentAnalysisService service;

  @PostMapping
  public ResponseEntity<SentimentDetailedDTO> createAnalysis(@RequestBody InputSentimentDTO inputSentimentDTO) {
    var sentimentAnalyzed = service.createAnalysis(inputSentimentDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(sentimentAnalyzed);
  }

  @GetMapping("/analysis")
  public ResponseEntity<Page<OutputSentimentDTO>> getAllAnalysis(
      @PageableDefault(size = 10, sort = "probabilidade") Pageable pageable) {
    var page = service.getAllAnalysis(pageable);
    return ResponseEntity.status(HttpStatus.OK).body(page);
  }

  @GetMapping
  public ResponseEntity<Page<SentimentDetailedDTO>> getAllAnalyzed(
      @PageableDefault(size = 10, sort = "probabilidade") Pageable pageable) {
    var page = service.getAllAnalyzedSentiment(pageable);
    return ResponseEntity.status(HttpStatus.OK).body(page);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SentimentDetailedDTO> getAnalyzed(@PathVariable(value = "id") Long id) {
    var sentiment = service.getOneAnalyzedSentiment(id);
    return ResponseEntity.status(HttpStatus.OK).body(sentiment);
  }

  @PutMapping
  public ResponseEntity<SentimentDetailedDTO> updateAnalysis(@RequestBody UpdateAnalyzedSentimentDTO updateDTO) {
    var sentimentUpdated = service.updateAnalyzis(updateDTO);
    return ResponseEntity.status(HttpStatus.OK).body(sentimentUpdated);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAnalysis(@PathVariable(value = "id") Long id) {
    service.deleteAnalyzedSentiment(id);
  }

}
