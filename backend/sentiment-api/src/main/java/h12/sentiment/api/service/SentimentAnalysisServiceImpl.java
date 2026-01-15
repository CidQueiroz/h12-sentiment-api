package h12.sentiment.api.service;

import h12.sentiment.api.dto.ChartDataDTO;
import h12.sentiment.api.dto.ConfidenceProjection;
import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.KpiDTO;
import h12.sentiment.api.dto.MicroserviceResponseDTO;
import h12.sentiment.api.dto.ModelUsageDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentByModelProjection;
import h12.sentiment.api.dto.SentimentDistributionDTO;
import h12.sentiment.api.dto.StackedChartDataDTO;
import h12.sentiment.api.dto.TimelineProjection;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import h12.sentiment.api.repository.SentimentAnalysisRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;


@Service
@Primary
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

    private final WebClient sentimentWebClient;
    private final SentimentAnalysisRepository repository;
    private final List<String> ALL_MODELS = Arrays.asList("svm", "nb", "lr");
    private final List<String> ALL_SENTIMENTS = Arrays.asList("Positivo", "Negativo", "Neutro");


    public SentimentAnalysisServiceImpl(WebClient sentimentWebClient, SentimentAnalysisRepository repository) {
        this.sentimentWebClient = sentimentWebClient;
        this.repository = repository;
    }

    @Override
    public Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO input) {
        return sentimentWebClient.post()
                .uri("/predict")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .retrieve()
                .bodyToMono(MicroserviceResponseDTO.class)
                .flatMap(response -> {
                    OutputSentimentDTO output = new OutputSentimentDTO(response.previsao(), response.probabilidade());
                    return saveAnalysis(input, response).thenReturn(output);
                });
    }

    private Mono<SentimentAnalysisEntity> saveAnalysis(InputSentimentDTO input, MicroserviceResponseDTO response) {
        return Mono.fromCallable(() -> {
            SentimentAnalysisEntity entity = SentimentAnalysisEntity.builder()
                    .originalText(input.getText())
                    .modelType(input.getAlgorithm())
                    .prediction(response.previsao())
                    .probability(response.probabilidade())
                    .language(response.idioma())
                    .build();
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<OutputSentimentDTO> getOneAnalysis() {
        return Mono.just(new OutputSentimentDTO("Positive", 0.99));
    }

    @Override
    public Mono<Page<SentimentAnalysisEntity>> getAllAnalyses(Pageable pageable) {
        return Mono.fromCallable(() -> repository.findAll(pageable))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<SentimentDistributionDTO> getSentimentDistribution() {
        return Mono.fromCallable(() -> {
            Map<String, Long> distribution = new HashMap<>();
            distribution.put("Positivo", repository.countByPrediction("Positivo"));
            distribution.put("Negativo", repository.countByPrediction("Negativo"));
            distribution.put("Neutro", repository.countByPrediction("Neutro"));
            return new SentimentDistributionDTO(distribution);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ModelUsageDTO> getModelUsage() {
        return Mono.fromCallable(() -> {
            Map<String, Long> usage = new HashMap<>();
            for (String model : ALL_MODELS) {
                usage.put(model, repository.countByModelType(model));
            }
            return new ModelUsageDTO(usage);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<KpiDTO> getKpis() {
        return Mono.fromCallable(() -> {
            long total = repository.countTotalAnalyses();
            long positive = repository.countByPrediction("Positivo");
            double positivityPercentage = (total > 0) ? ((double) positive / total) * 100 : 0.0;
            return new KpiDTO(total, positivityPercentage);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getLanguageDistribution() {
        return Mono.fromCallable(() -> {
            List<h12.sentiment.api.dto.LanguageProjection> projections = repository.findLanguageDistribution();
            List<String> labels = projections.stream().map(h12.sentiment.api.dto.LanguageProjection::getLanguage).collect(Collectors.toList());
            List<Long> values = projections.stream().map(h12.sentiment.api.dto.LanguageProjection::getCount).collect(Collectors.toList());
            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getHourlyDistribution() {
        return Mono.fromCallable(() -> {
            List<h12.sentiment.api.dto.HourlyProjection> projections = repository.findHourlyDistribution();
            Map<Integer, Long> hourlyCounts = projections.stream()
                    .collect(Collectors.toMap(
                            h12.sentiment.api.dto.HourlyProjection::getHour,
                            h12.sentiment.api.dto.HourlyProjection::getCount
                    ));

            List<String> labels = java.util.stream.IntStream.range(0, 24)
                    .mapToObj(i -> String.format("%02dh", i))
                    .collect(Collectors.toList());
            List<Long> values = java.util.stream.IntStream.range(0, 24)
                    .mapToObj(i -> hourlyCounts.getOrDefault(i, 0L))
                    .collect(Collectors.toList());

            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<StackedChartDataDTO> getSentimentByModel() {
        return Mono.fromCallable(() -> {
            List<SentimentByModelProjection> projections = repository.findSentimentByModel();

            // Group by modelType
            Map<String, Map<String, Long>> groupedData = projections.stream()
                .collect(Collectors.groupingBy(
                    SentimentByModelProjection::getModelType,
                    Collectors.toMap(SentimentByModelProjection::getPrediction, SentimentByModelProjection::getCount)
                ));

            // Prepare datasets for Chart.js
            List<Map<String, Long>> datasets = ALL_MODELS.stream().map(model -> {
                Map<String, Long> modelSentimentCounts = new LinkedHashMap<>();
                modelSentimentCounts.put("model", (long) model.hashCode()); // Dummy ID or other identifier if needed
                for (String sentiment : ALL_SENTIMENTS) {
                    modelSentimentCounts.put(sentiment, groupedData.getOrDefault(model, Collections.emptyMap()).getOrDefault(sentiment, 0L));
                }
                return modelSentimentCounts;
            }).collect(Collectors.toList());
            
            // Ensure labels order
            List<String> labels = ALL_MODELS;

            return new StackedChartDataDTO(labels, datasets);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<ChartDataDTO> getConfidenceLevels() {
        return Mono.fromCallable(() -> {
            long total = repository.countTotalAnalyses();
            long highConf = repository.findAll().stream().filter(e -> e.getProbability() >= 0.9).count();
            long lowConf = total - highConf;
            return new ChartDataDTO(Arrays.asList("Alta (>90%)", "Média/Baixa"), Arrays.asList(highConf, lowConf));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getFeedbackLength() {
        return Mono.fromCallable(() -> {
            long shortFeedback = repository.findAll().stream().filter(e -> e.getOriginalText().length() < 20).count();
            long mediumFeedback = repository.findAll().stream().filter(e -> e.getOriginalText().length() >= 20 && e.getOriginalText().length() <= 100).count();
            long longFeedback = repository.findAll().stream().filter(e -> e.getOriginalText().length() > 100).count();
            return new ChartDataDTO(Arrays.asList("Curtos (<20)", "Médios (20-100)", "Longos (>100)"), Arrays.asList(shortFeedback, mediumFeedback, longFeedback));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getTimeline() {
        return Mono.fromCallable(() -> {
            List<TimelineProjection> projections = repository.findTimeline();
            List<String> labels = projections.stream()
                    .map(TimelineProjection::getDate)
                    .collect(Collectors.toList());
            List<Long> values = projections.stream()
                    .map(TimelineProjection::getCount)
                    .collect(Collectors.toList());
            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getAverageConfidenceBySentiment() {
        return Mono.fromCallable(() -> {
            List<ConfidenceProjection> projections = repository.findAverageConfidenceBySentiment();
            List<String> labels = projections.stream()
                    .sorted(Comparator.comparing(ConfidenceProjection::getSentiment)) // Ensure consistent order
                    .map(ConfidenceProjection::getSentiment)
                    .collect(Collectors.toList());
            List<Long> values = projections.stream()
                    .sorted(Comparator.comparing(ConfidenceProjection::getSentiment)) // Ensure consistent order
                    .map(p -> p.getAverageConfidence().longValue()) // Convert Double to Long for ChartDataDTO
                    .collect(Collectors.toList());
            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}