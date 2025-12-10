package h12.sentiment.api.dto;

import h12.sentiment.api.model.SentimentAnalysis;

public record OutputSentimentDTO(String previsao, double probabilidade) {

  public OutputSentimentDTO(SentimentAnalysis sentimentAnalysis) {
    this(sentimentAnalysis.getPrevisao(), sentimentAnalysis.getProbabilidade());
  }
}
