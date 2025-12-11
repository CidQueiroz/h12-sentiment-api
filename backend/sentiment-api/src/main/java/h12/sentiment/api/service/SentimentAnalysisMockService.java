package h12.sentiment.api.service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class SentimentAnalysisMockService implements SentimentAnalysisService{

    @Override
    public OutputSentimentDTO createAnalysis(InputSentimentDTO input) {
        return new OutputSentimentDTO("Mocked", 1.0);
    }

    @Override
    public OutputSentimentDTO getOneAnalysis() {
        return new OutputSentimentDTO("Positive", 0.99);
    }
}
