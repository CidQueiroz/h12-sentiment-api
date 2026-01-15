package h12.sentiment.api.repository;

import h12.sentiment.api.dto.ConfidenceProjection;
import h12.sentiment.api.dto.HourlyProjection;
import h12.sentiment.api.dto.LanguageProjection;
import h12.sentiment.api.dto.SentimentByModelProjection;
import h12.sentiment.api.dto.TimelineProjection;
import h12.sentiment.api.entity.SentimentAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysisEntity, Long> {

    long countByModelType(String modelType);
    long countByPrediction(String prediction);

    @Query("SELECT COUNT(s) FROM SentimentAnalysisEntity s")
    long countTotalAnalyses();

    @Query("SELECT s.language as language, COUNT(s.language) as count FROM SentimentAnalysisEntity s WHERE s.language IS NOT NULL GROUP BY s.language")
    List<LanguageProjection> findLanguageDistribution();

    @Query("SELECT FUNCTION('TO_CHAR', s.createdAt, 'YYYY-MM-DD') as date, COUNT(s) as count FROM SentimentAnalysisEntity s GROUP BY FUNCTION('TO_CHAR', s.createdAt, 'YYYY-MM-DD') ORDER BY date")
    List<TimelineProjection> findTimeline();

    @Query("SELECT FUNCTION('TO_CHAR', s.createdAt, 'HH24') as hour, COUNT(s) as count FROM SentimentAnalysisEntity s GROUP BY FUNCTION('TO_CHAR', s.createdAt, 'HH24') ORDER BY hour")
    List<HourlyProjection> findHourlyDistribution();

    @Query("SELECT s.modelType as modelType, s.prediction as prediction, COUNT(s) as count FROM SentimentAnalysisEntity s WHERE s.modelType IS NOT NULL AND s.prediction IS NOT NULL GROUP BY s.modelType, s.prediction")
    List<SentimentByModelProjection> findSentimentByModel();

    @Query("SELECT s.prediction as sentiment, AVG(s.probability) as averageConfidence FROM SentimentAnalysisEntity s WHERE s.prediction IN ('Positivo', 'Negativo', 'Neutro') GROUP BY s.prediction")
    List<ConfidenceProjection> findAverageConfidenceBySentiment();
}
