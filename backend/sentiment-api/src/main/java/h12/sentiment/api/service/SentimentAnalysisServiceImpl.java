package h12.sentiment.api.service;

import h12.sentiment.api.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            List<LanguageProjection> projections = repository.findLanguageDistribution();
            List<String> labels = projections.stream().map(LanguageProjection::getLanguage).collect(Collectors.toList());
            List<Long> values = projections.stream().map(LanguageProjection::getCount).collect(Collectors.toList());
            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getHourlyDistribution() {
        return Mono.fromCallable(() -> {
            List<HourlyProjection> projections = repository.findHourlyDistribution();
            Map<String, Long> hourlyCounts = projections.stream()
                    .collect(Collectors.toMap(
                            HourlyProjection::getHour,
                            HourlyProjection::getCount
                    ));

            List<String> labels = IntStream.range(0, 24)
                    .mapToObj(i -> String.format("%02d", i))
                    .collect(Collectors.toList());
            List<Long> values = labels.stream()
                    .map(label -> hourlyCounts.getOrDefault(label, 0L))
                    .collect(Collectors.toList());

            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<StackedChartDataDTO> getSentimentByModel() {
        return Mono.fromCallable(() -> {
            List<SentimentByModelProjection> projections = repository.findSentimentByModel();
            Map<String, Map<String, Long>> groupedData = projections.stream()
                .collect(Collectors.groupingBy(
                    SentimentByModelProjection::getModelType,
                    Collectors.toMap(SentimentByModelProjection::getPrediction, SentimentByModelProjection::getCount)
                ));

            List<StackedChartDataDTO.Dataset> datasets = ALL_SENTIMENTS.stream().map(sentiment -> {
                List<Long> counts = ALL_MODELS.stream()
                    .map(model -> groupedData.getOrDefault(model, Collections.emptyMap()).getOrDefault(sentiment, 0L))
                    .collect(Collectors.toList());
                return new StackedChartDataDTO.Dataset(sentiment, counts);
            }).collect(Collectors.toList());
            
            return new StackedChartDataDTO(ALL_MODELS, datasets);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getConfidenceLevels() {
        return Mono.fromCallable(() -> {
            long total = repository.count();
            long highConf = repository.findAll().stream().filter(e -> e.getProbability() != null && e.getProbability() >= 0.9).count();
            long lowConf = total - highConf;
            return new ChartDataDTO(Arrays.asList("Alta (>90%)", "Média/Baixa"), Arrays.asList(highConf, lowConf));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getFeedbackLength() {
        return Mono.fromCallable(() -> {
            List<SentimentAnalysisEntity> all = repository.findAll();
            long shortFeedback = all.stream().filter(e -> e.getOriginalText().length() < 50).count();
            long mediumFeedback = all.stream().filter(e -> e.getOriginalText().length() >= 50 && e.getOriginalText().length() <= 140).count();
            long longFeedback = all.stream().filter(e -> e.getOriginalText().length() > 140).count();
            return new ChartDataDTO(Arrays.asList("Curtos (<50)", "Médios (50-140)", "Longos (>140)"), Arrays.asList(shortFeedback, mediumFeedback, longFeedback));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getTimeline() {
        return Mono.fromCallable(() -> {
            List<TimelineProjection> projections = repository.findTimeline();
            Map<LocalDate, Long> countsByDate = projections.stream()
                .collect(Collectors.toMap(
                    p -> LocalDate.parse(p.getDate()),
                    TimelineProjection::getCount
                ));

            List<String> labels = new ArrayList<>();
            List<Long> values = new ArrayList<>();
            if (!countsByDate.isEmpty()) {
                LocalDate minDate = Collections.min(countsByDate.keySet());
                LocalDate maxDate = Collections.max(countsByDate.keySet());

                for (LocalDate date = minDate; !date.isAfter(maxDate); date = date.plusDays(1)) {
                    labels.add(date.toString());
                    values.add(countsByDate.getOrDefault(date, 0L));
                }
            }
            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ChartDataDTO> getAverageConfidenceBySentiment() {
        return Mono.fromCallable(() -> {
            List<ConfidenceProjection> projections = repository.findAverageConfidenceBySentiment();
            List<String> labels = projections.stream()
                    .map(ConfidenceProjection::getSentiment)
                    .collect(Collectors.toList());
            List<Long> values = projections.stream()
                    .map(p -> (long) (p.getAverageConfidence() * 100))
                    .collect(Collectors.toList());
            return new ChartDataDTO(labels, values);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
