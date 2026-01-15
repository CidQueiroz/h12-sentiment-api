package h12.sentiment.api.service;

import h12.sentiment.api.dto.ChartDataDTO;
import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.KpiDTO;
import h12.sentiment.api.dto.ModelUsageDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDistributionDTO;
import h12.sentiment.api.dto.StackedChartDataDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface SentimentAnalysisService {
    Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO input);
    Mono<OutputSentimentDTO> getOneAnalysis();
    Mono<Page<SentimentAnalysisEntity>> getAllAnalyses(Pageable pageable);

    // Métodos para Analytics
    Mono<SentimentDistributionDTO> getSentimentDistribution();
    Mono<ModelUsageDTO> getModelUsage();
    Mono<KpiDTO> getKpis();
    Mono<ChartDataDTO> getLanguageDistribution();
    Mono<ChartDataDTO> getHourlyDistribution();
    Mono<StackedChartDataDTO> getSentimentByModel();
    Mono<ChartDataDTO> getConfidenceLevels();
    Mono<ChartDataDTO> getFeedbackLength();
    Mono<ChartDataDTO> getTimeline();
    Mono<ChartDataDTO> getAverageConfidenceBySentiment();
}