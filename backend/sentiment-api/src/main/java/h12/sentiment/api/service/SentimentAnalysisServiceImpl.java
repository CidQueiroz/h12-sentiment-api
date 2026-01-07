package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import h12.sentiment.api.repository.SentimentAnalysisRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Profile("real")
public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {

    private final WebClient sentimentWebClient;
    private final SentimentAnalysisRepository repository;

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
                .bodyToMono(OutputSentimentDTO.class)
                .flatMap(output -> saveAnalysis(input, output).thenReturn(output));
    }

    private Mono<SentimentAnalysisEntity> saveAnalysis(InputSentimentDTO input, OutputSentimentDTO output) {
        return Mono.fromCallable(() -> {
            SentimentAnalysisEntity entity = SentimentAnalysisEntity.builder()
                    .originalText(input.getText())
                    .modelType(input.getModel_type())
                    .prediction(output.previsao())
                    .probability(output.probabilidade())
                    .build();
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<OutputSentimentDTO> getOneAnalysis() {
        return Mono.just(new OutputSentimentDTO("Positive", 0.99));
    }
}