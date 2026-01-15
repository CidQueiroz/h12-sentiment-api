package h12.sentiment.api.dto;

import java.util.List;
import java.util.Map;

// Usado para retornar dados para gráficos de barras empilhadas
public record StackedChartDataDTO(List<String> labels, List<Map<String, Long>> data) {}
