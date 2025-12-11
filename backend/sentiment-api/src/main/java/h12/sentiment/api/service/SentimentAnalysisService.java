package h12.sentiment.api.service;

import org.springframework.stereotype.Service;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;

@Service
public class SentimentAnalysisService {

  public OutputSentimentDTO createAnalysis(InputSentimentDTO inputSentimentDTO) {

    if (inputSentimentDTO.text() == null || inputSentimentDTO.text().isEmpty()) {
            return new OutputSentimentDTO("El texto está vacío.", 0.0);
        }

        String text = inputSentimentDTO.text().toLowerCase();
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

        return new OutputSentimentDTO(sentiment, score);


  }

  public OutputSentimentDTO getOneAnalysis() {
    return new OutputSentimentDTO("Positive", 0.95);
  }

}
