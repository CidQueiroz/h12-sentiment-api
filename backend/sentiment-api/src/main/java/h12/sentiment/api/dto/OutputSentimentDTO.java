package h12.sentiment.api.dto;

import h12.sentiment.api.entity.SentimentAnalysisEntity;

public record OutputSentimentDTO(String previsao, double probabilidade) {

  public OutputSentimentDTO(SentimentAnalysisEntity e) {
    this(
        e.getPrediction(),
        e.getProbability() != null ? e.getProbability() : 0.0);
  }
}
