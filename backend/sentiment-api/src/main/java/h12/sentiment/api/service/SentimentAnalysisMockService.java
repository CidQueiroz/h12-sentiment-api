package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
    public Mono<java.util.List<h12.sentiment.api.entity.SentimentAnalysisEntity>> getAllAnalyses() {
        return Mono.just(java.util.Collections.emptyList());
    }
}
