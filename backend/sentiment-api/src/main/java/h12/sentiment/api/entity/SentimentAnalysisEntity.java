package h12.sentiment.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;


import java.time.LocalDateTime;

/**
 * Entidade mapeada para Spring Data JPA.
 * As colunas usam snake_case para casar com o migration do Flyway.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sentiment_analysis")
public class SentimentAnalysisEntity {

  @Id
  @SequenceGenerator(name = "sentiment_seq", sequenceName = "SENTIMENT_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sentiment_seq")
  private Long id;

  @Column(name = "original_text")
  private String originalText;

  @Column(name = "model_type")
  private String modelType;

  @Column(name = "prediction")
  private String prediction;

  @Column(name = "probability")
  private Double probability;

  @Column(name = "lang")
  private String language;

  @Column(name = "created_at", updatable = false)
  @org.hibernate.annotations.CreationTimestamp
  private LocalDateTime createdAt;
}
