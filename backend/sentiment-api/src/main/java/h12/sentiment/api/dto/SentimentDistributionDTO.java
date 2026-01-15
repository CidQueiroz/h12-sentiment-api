package h12.sentiment.api.dto;

import java.util.Map;

public record SentimentDistributionDTO(Map<String, Long> distribution) {}
