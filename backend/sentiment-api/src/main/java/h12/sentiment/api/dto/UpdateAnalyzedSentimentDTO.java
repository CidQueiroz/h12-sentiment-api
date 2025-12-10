package h12.sentiment.api.dto;

import h12.sentiment.api.model.SentimentAnalysis;

public record UpdateAnalyzedSentimentDTO(Long id, String text, String previsao, Double probabilidade) {

  public UpdateAnalyzedSentimentDTO(SentimentAnalysis sentimentAnalysis) {
    this(
        sentimentAnalysis.getId(),
        sentimentAnalysis.getText(),
        sentimentAnalysis.getPrevisao(),
        sentimentAnalysis.getProbabilidade());
  }
}
