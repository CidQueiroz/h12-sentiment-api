package h12.sentiment.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InputSentimentDTO {

  @NotBlank(message = "O campo 'text' não pode estar vazio.")
  private String text;

  @NotBlank(message = "O campo 'model_type' não pode estar vazio.")
  private String modelType;

  public InputSentimentDTO() {
  }

  public InputSentimentDTO(String text, String modelType) {
    this.text = text;
    this.modelType = modelType;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public SentimentAnalysisEntity toEntity() {
    return SentimentAnalysisEntity.builder()
        .originalText(this.text)
        .modelType(this.modelType)
        .build();
  }
}
