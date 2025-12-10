package h12.sentiment.api.service;

import org.springframework.stereotype.Service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.model.SentimentAnalysis;

@Service
public class SentimentAnalysisService {

  public SentimentAnalysis createAnalysis(InputSentimentDTO inputSentimentDTO) {
    var sentimentAnalysis = new SentimentAnalysis(inputSentimentDTO);
    return sentimentAnalysis;
  }

  public OutputSentimentDTO getOneAnalysis() {
    return new OutputSentimentDTO("Positive", 0.95);
  }

}
