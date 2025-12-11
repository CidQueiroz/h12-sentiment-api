package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Profile("real")
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

  private final WebClient sentimentWebClient;

  public SentimentAnalysisServiceImpl(WebClient sentimentWebClient) {
    this.sentimentWebClient = sentimentWebClient;
  }

  @Override
  public OutputSentimentDTO createAnalysis(InputSentimentDTO input) {
    return sentimentWebClient.post()
        .uri("/predict")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(input)
        .retrieve()
        .bodyToMono(OutputSentimentDTO.class)
        .block();
  }

  @Override
  public OutputSentimentDTO getOneAnalysis() {
    return new OutputSentimentDTO("Positive", 0.99);
  }
}
