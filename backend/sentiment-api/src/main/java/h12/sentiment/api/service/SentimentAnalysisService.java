package h12.sentiment.api.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;

@Service
public interface SentimentAnalysisService {
    Mono<OutputSentimentDTO> createAnalysis(InputSentimentDTO input);
    Mono<OutputSentimentDTO> getOneAnalysis();
}
