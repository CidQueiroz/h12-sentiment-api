package h12.sentiment.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InputSentimentDTO {

    @NotBlank(message = "O campo 'text' não pode estar vazio.")
    private String text;

    @NotBlank(message = "O campo 'algorithm' não pode estar vazio.")
    private String algorithm;

    public InputSentimentDTO() {
    }

    public InputSentimentDTO(String text, String algorithm) {
        this.text = text;
        this.algorithm = algorithm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public SentimentAnalysisEntity toEntity() {
        return SentimentAnalysisEntity.builder()
                .originalText(this.text)
                .modelType(this.algorithm) // Aqui fazemos a ponte!
                .build();
    }
}