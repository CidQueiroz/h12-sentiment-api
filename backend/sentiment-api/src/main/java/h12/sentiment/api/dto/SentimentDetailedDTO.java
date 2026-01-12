package h12.sentiment.api.dto;

import h12.sentiment.api.entity.SentimentAnalysisEntity;
import java.time.LocalDateTime;

public record SentimentDetailedDTO(
    Long id,
    String text,
    String previsao,
    Double probabilidade,
    String idioma,
    LocalDateTime createdAt) {
  public SentimentDetailedDTO(SentimentAnalysisEntity e) {
    this(
        e.getId(),
        e.getOriginalText(),
        e.getPrediction(),
        e.getProbability(),
        e.getLanguage(),
        e.getCreatedAt());
  }
}
