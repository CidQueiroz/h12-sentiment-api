package h12.sentiment.api.dto;

import jakarta.validation.constraints.NotBlank;

public class InputSentimentDTO {

    @NotBlank(message = "O campo 'text' não pode estar vazio.")
    private String text;

    @NotBlank(message = "O campo 'model_type' não pode estar vazio.")
    private String model_type;

    // Getters e Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModel_type() {
        return model_type;
    }

    public void setModel_type(String model_type) {
        this.model_type = model_type;
    }
}
