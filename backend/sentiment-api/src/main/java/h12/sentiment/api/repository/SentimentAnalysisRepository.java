package h12.sentiment.api.repository;

import h12.sentiment.api.entity.SentimentAnalysisEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentimentAnalysisRepository extends ReactiveCrudRepository<SentimentAnalysisEntity, Long> {
}
