package h12.sentiment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a resposta do microservice de IA.
 */
public record MicroserviceResponseDTO(
    @JsonProperty("previsao") String previsao,
    @JsonProperty("probabilidade") double probabilidade,
    @JsonProperty("idioma") String idioma,
    @JsonProperty("algoritmo") String algoritmo) {
}
