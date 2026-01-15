package h12.sentiment.api.dto;

import java.util.List;

public record StackedChartDataDTO(
    List<String> labels, // e.g., ["svm", "nb", "lr"]
    List<Dataset> datasets
) {
    public record Dataset(
        String label,    // e.g., "Positivo"
        List<Long> data  // e.g., [15, 10, 8]
    ) {}
}