package h12.sentiment.api.service;

import h12.sentiment.api.dto.ChartDataDTO;
import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.KpiDTO;
import h12.sentiment.api.dto.ModelUsageDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDistributionDTO;
import h12.sentiment.api.dto.StackedChartDataDTO;
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
public class SentimentAnalysisMockService implements SentimentAnalysisService{

    @Override
    public Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO inputSentimentDTO) {

        if (inputSentimentDTO.getText() == null || inputSentimentDTO.getText().isEmpty()) {
            return Mono.just(new OutputSentimentDTO("El texto está vacío.", 0.0));
        }

        String text = inputSentimentDTO.getText().toLowerCase();
        String sentiment;
        double score;

        if (text.contains("feliz") || text.contains("contento") || text.contains("alegre")) {
            sentiment = "Sentimiento positivo 😊";
            score = 0.9;

        } else if (text.contains("triste") || text.contains("mal") || text.contains("enojado")) {
            sentiment = "Sentimiento negativo 😢";
            score = 0.2;

        } else {
            sentiment = "Sentimiento neutral 😐";
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
        // Retorna uma página vazia para o mock
        return Mono.just(new PageImpl<>(Collections.emptyList(), pageable, 0));
    }

    @Override
    public Mono<SentimentDistributionDTO> getSentimentDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("Positivo", 50L);
        distribution.put("Negativo", 30L);
        distribution.put("Neutro", 20L);
        return Mono.just(new SentimentDistributionDTO(distribution));
    }

    @Override
    public Mono<ModelUsageDTO> getModelUsage() {
        Map<String, Long> usage = new HashMap<>();
        usage.put("svm", 40L);
        usage.put("nb", 35L);
        usage.put("lr", 25L);
        return Mono.just(new ModelUsageDTO(usage));
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
        return Mono.just(new ChartDataDTO(Arrays.asList("09h", "10h", "11h", "14h", "15h"), Arrays.asList(5L, 10L, 15L, 12L, 8L)));
    }

    @Override
    public Mono<StackedChartDataDTO> getSentimentByModel() {
        // Corrigido para retornar a estrutura que o frontend espera
        List<String> labels = Arrays.asList("svm", "nb", "lr");
        List<Map<String, Long>> data = Arrays.asList(
                Map.of("model", 1L, "Positivo", 15L, "Negativo", 5L, "Neutro", 0L), // Exemplo para SVM
                Map.of("model", 2L, "Positivo", 10L, "Negativo", 4L, "Neutro", 1L), // Exemplo para NB
                Map.of("model", 3L, "Positivo", 8L, "Negativo", 2L, "Neutro", 0L)   // Exemplo para LR
        );
        return Mono.just(new StackedChartDataDTO(labels, data));
    }

    @Override
    public Mono<ChartDataDTO> getConfidenceLevels() {
        return Mono.just(new ChartDataDTO(Arrays.asList("Alta (>90%)", "Média/Baixa"), Arrays.asList(70L, 30L)));
    }

    @Override
    public Mono<ChartDataDTO> getFeedbackLength() {
        return Mono.just(new ChartDataDTO(Arrays.asList("Curtos (<20)", "Médios (20-100)", "Longos (>100)"), Arrays.asList(40L, 45L, 15L)));
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
