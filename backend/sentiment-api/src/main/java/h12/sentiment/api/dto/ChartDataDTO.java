package h12.sentiment.api.dto;

import java.util.List;

// Usado para retornar dados para gráficos de barra ou de linha simples
public record ChartDataDTO(List<String> labels, List<Long> values) {}
