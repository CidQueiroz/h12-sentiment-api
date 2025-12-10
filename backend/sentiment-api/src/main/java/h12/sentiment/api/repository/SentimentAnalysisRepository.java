package h12.sentiment.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import h12.sentiment.api.model.SentimentAnalysis;

public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {
}
