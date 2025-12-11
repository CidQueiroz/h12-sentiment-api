package h12.sentiment.api.service;

import org.springframework.stereotype.Service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;

@Service
public interface SentimentAnalysisService {
    OutputSentimentDTO createAnalysis(InputSentimentDTO input);
    OutputSentimentDTO getOneAnalysis();
}