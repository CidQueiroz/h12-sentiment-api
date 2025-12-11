package h12.sentiment.api.service;

import org.springframework.stereotype.Service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;

@Service
public class SentimentAnalysisService {

  public OutputSentimentDTO createAnalysis(InputSentimentDTO inputSentimentDTO) {
    // var sentimentAnalysis = new SentimentAnalysis(inputSentimentDTO);
    return new OutputSentimentDTO("Positive", 0.95);
  }

  public OutputSentimentDTO getOneAnalysis() {
    return new OutputSentimentDTO("Positive", 0.95);
  }

}
