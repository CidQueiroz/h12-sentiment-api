package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.MicroserviceResponseDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import h12.sentiment.api.repository.SentimentAnalysisRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

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
                    // 1. Cria o objeto de resposta para o usuário imediatamente
                    OutputSentimentDTO output = new OutputSentimentDTO(response.previsao(), response.probabilidade());
                    
                    // 2. Salva no banco de forma assíncrona (sem travar a resposta)
                    return saveAnalysis(input, response).thenReturn(output);
                });
    }

    // AQUI O SEGREDO DO MERGE:
    private Mono<SentimentAnalysisEntity> saveAnalysis(InputSentimentDTO input, MicroserviceResponseDTO response) {
        return Mono.fromCallable(() -> {
            // Usamos o método toEntity() que consertamos no passo anterior (Vem da Main)
            SentimentAnalysisEntity entity = input.toEntity();
            
            // Preenchemos o resto com a resposta do Python
            entity.setPrediction(response.previsao());
            entity.setProbability(response.probabilidade());
            entity.setLanguage(response.idioma());

            // Salvamos no banco
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic()); // Mantemos isso da FIX para não travar o servidor
    }

    @Override
    public Mono<OutputSentimentDTO> getOneAnalysis() {
        return Mono.just(new OutputSentimentDTO("Positive", 0.99));
    }

    @Override
    public Mono<List<SentimentAnalysisEntity>> getAllAnalyses() {
        return Mono.fromCallable(repository::findAll)
                .flatMapMany(reactor.core.publisher.Flux::fromIterable)
                .collectList()
                .subscribeOn(Schedulers.boundedElastic());
    }
}