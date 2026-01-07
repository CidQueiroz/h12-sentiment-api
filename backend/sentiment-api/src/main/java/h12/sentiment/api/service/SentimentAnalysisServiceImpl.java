package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.MicroserviceResponseDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import h12.sentiment.api.repository.SentimentAnalysisRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Primary
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
                    .modelType(input.getModel_type())
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
    public Mono<java.util.List<SentimentAnalysisEntity>> getAllAnalyses() {
        return Mono.fromCallable(repository::findAll)
                .flatMapMany(reactor.core.publisher.Flux::fromIterable)
                .collectList()
                .subscribeOn(Schedulers.boundedElastic());
    }
}