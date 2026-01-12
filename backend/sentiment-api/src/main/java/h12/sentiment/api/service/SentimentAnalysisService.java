package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import reactor.core.publisher.Mono;
import java.util.List;

public interface SentimentAnalysisService {
    Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO input);
    Mono<OutputSentimentDTO> getOneAnalysis();
    Mono<List<SentimentAnalysisEntity>> getAllAnalyses();
}