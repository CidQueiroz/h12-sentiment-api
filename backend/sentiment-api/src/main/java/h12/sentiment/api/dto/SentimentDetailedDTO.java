package h12.sentiment.api.dto;

import h12.sentiment.api.model.SentimentAnalysis;

public record SentimentDetailedDTO(Long id, String text, String previsao, Double probabilidade, String idioma) {

  public SentimentDetailedDTO(SentimentAnalysis sentimentAnalysis) {
    this(
        sentimentAnalysis.getId(),
        sentimentAnalysis.getText(),
        sentimentAnalysis.getPrevisao(),
        sentimentAnalysis.getProbabilidade(),
        sentimentAnalysis.getIdioma());
  }
}
