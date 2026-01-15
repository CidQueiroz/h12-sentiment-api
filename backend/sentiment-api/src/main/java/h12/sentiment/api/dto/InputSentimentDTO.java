package h12.sentiment.api.dto;

import jakarta.validation.constraints.NotBlank;

public class InputSentimentDTO {

    @NotBlank(message = "O campo 'text' não pode estar vazio.")
    private String text;

    @NotBlank(message = "O campo 'algorithm' não pode estar vazio.")
    private String algorithm;

    // Getters e Setters
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
}
