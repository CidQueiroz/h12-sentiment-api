package h12.sentiment.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.dto.SentimentDetailedDTO;
import h12.sentiment.api.dto.UpdateAnalyzedSentimentDTO;
import h12.sentiment.api.model.SentimentAnalysis;
import h12.sentiment.api.repository.SentimentAnalysisRepository;

@Service
public class SentimentAnalysisService {

  @Autowired
  private SentimentAnalysisRepository repository;

  // Create a new sentiment analysis
  public SentimentDetailedDTO createAnalysis(InputSentimentDTO inputSentimentDTO) {
    var sentimentAnalysis = new SentimentAnalysis(inputSentimentDTO);
    // repository.save(sentimentAnalysis);
    analyzeSentiment(sentimentAnalysis);
    return new SentimentDetailedDTO(sentimentAnalysis);
  }

  // Perform a mock of sentiment analysis
  public void analyzeSentiment(SentimentAnalysis sentimentAnalysis) {

    String predictedSentiment = "Positive";
    double probability = 0.95;

    sentimentAnalysis.setPrevisao(predictedSentiment);
    sentimentAnalysis.setProbabilidade(probability);
    repository.save(sentimentAnalysis);
  }

  // --- Retrieve only analysis results ---

  public Page<OutputSentimentDTO> getAllAnalysis(Pageable pageable) {
    return repository.findAll(pageable).map(OutputSentimentDTO::new);
  }

  public OutputSentimentDTO getOneAnalysis(Long id) {
    var sentiment = repository.getReferenceById(id);
    return new OutputSentimentDTO(sentiment);
  }

  // --- Retrieve detailed analysis (Text + Result) ---

  public Page<SentimentDetailedDTO> getAllAnalyzedSentiment(Pageable pageable) {
    return repository.findAll(pageable).map(SentimentDetailedDTO::new);
  }

  public SentimentDetailedDTO getOneAnalyzedSentiment(Long id) {
    var sentiment = repository.getReferenceById(id);
    return new SentimentDetailedDTO(sentiment);
  }

  public void deleteAnalyzedSentiment(Long id) {
    if (!repository.existsById(id)) {
      throw new IllegalArgumentException("Topic with ID " + id + " does not exist.");
    }
    repository.deleteById(id);
  }

  @Transactional
  public SentimentDetailedDTO updateAnalyzis(UpdateAnalyzedSentimentDTO updateDTO) {
    SentimentAnalysis sentiment = repository.findById(updateDTO.id())
        .orElseThrow(() -> new IllegalArgumentException("Topic with ID " + updateDTO.id() + " does not exist."));

    sentiment.setText(updateDTO.text());
    sentiment.setPrevisao(updateDTO.previsao());
    sentiment.setProbabilidade(updateDTO.probabilidade());

    return new SentimentDetailedDTO(sentiment);
  }
}
