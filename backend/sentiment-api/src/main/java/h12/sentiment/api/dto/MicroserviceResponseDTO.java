package h12.sentiment.api.dto;

public record MicroserviceResponseDTO(
    String previsao,
    double probabilidade,
    String idioma,
    String algoritmo
) {}
