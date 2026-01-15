package h12.sentiment.api.service;

import h12.sentiment.api.dto.*;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("mock")
public class SentimentAnalysisMockService implements SentimentAnalysisService {

    @Override
    public Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO inputSentimentDTO) {
        String text = inputSentimentDTO.getText().toLowerCase();
        String sentiment;
        double score;

        if (text.contains("feliz")) {
            sentiment = "Positivo";
            score = 0.9;
        } else if (text.contains("triste")) {
            sentiment = "Negativo";
            score = 0.2;
        } else {
            sentiment = "Neutro";
            score = 0.5;
        }
        return Mono.just(new OutputSentimentDTO(sentiment, score));
    }

    @Override
    public Mono<OutputSentimentDTO> getOneAnalysis() {
        return Mono.just(new OutputSentimentDTO("Positive", 0.95));
    }

    @Override
    public Mono<Page<SentimentAnalysisEntity>> getAllAnalyses(Pageable pageable) {
        return Mono.just(new PageImpl<>(Collections.emptyList(), pageable, 0));
    }

    @Override
    public Mono<SentimentDistributionDTO> getSentimentDistribution() {
        return Mono.just(new SentimentDistributionDTO(Map.of("Positivo", 50L, "Negativo", 30L, "Neutro", 20L)));
    }

    @Override
    public Mono<ModelUsageDTO> getModelUsage() {
        return Mono.just(new ModelUsageDTO(Map.of("svm", 40L, "nb", 35L, "lr", 25L)));
    }

    @Override
    public Mono<KpiDTO> getKpis() {
        return Mono.just(new KpiDTO(100L, 50.0));
    }

    @Override
    public Mono<ChartDataDTO> getLanguageDistribution() {
        return Mono.just(new ChartDataDTO(Arrays.asList("pt", "en", "es"), Arrays.asList(60L, 30L, 10L)));
    }

    @Override
    public Mono<ChartDataDTO> getHourlyDistribution() {
        return Mono.just(new ChartDataDTO(Arrays.asList("09", "10", "11", "14", "15"), Arrays.asList(5L, 10L, 15L, 12L, 8L)));
    }

    @Override
    public Mono<StackedChartDataDTO> getSentimentByModel() {
        List<String> labels = Arrays.asList("svm", "nb", "lr");
        List<StackedChartDataDTO.Dataset> datasets = Arrays.asList(
                new StackedChartDataDTO.Dataset("Positivo", Arrays.asList(15L, 10L, 8L)),
                new StackedChartDataDTO.Dataset("Negativo", Arrays.asList(5L, 4L, 2L)),
                new StackedChartDataDTO.Dataset("Neutro", Arrays.asList(0L, 1L, 0L))
        );
        return Mono.just(new StackedChartDataDTO(labels, datasets));
    }

    @Override
    public Mono<ChartDataDTO> getConfidenceLevels() {
        return Mono.just(new ChartDataDTO(Arrays.asList("Alta (>90%)", "Média/Baixa"), Arrays.asList(70L, 30L)));
    }

    @Override
    public Mono<ChartDataDTO> getFeedbackLength() {
        return Mono.just(new ChartDataDTO(Arrays.asList("Curtos (<50)", "Médios (50-140)", "Longos (>140)"), Arrays.asList(40L, 45L, 15L)));
    }

    @Override
    public Mono<ChartDataDTO> getTimeline() {
        List<String> labels = Arrays.asList(
                LocalDate.now().minusDays(2).toString(),
                LocalDate.now().minusDays(1).toString(),
                LocalDate.now().toString()
        );
        List<Long> values = Arrays.asList(5L, 12L, 8L);
        return Mono.just(new ChartDataDTO(labels, values));
    }

    @Override
    public Mono<ChartDataDTO> getAverageConfidenceBySentiment() {
        return Mono.just(new ChartDataDTO(Arrays.asList("Positivo", "Negativo", "Neutro"), Arrays.asList(90L, 80L, 75L)));
    }
}