package h12.sentiment.api.dto;

import jakarta.validation.constraints.NotBlank;

public record InputSentimentDTO(@NotBlank String text) {
}
