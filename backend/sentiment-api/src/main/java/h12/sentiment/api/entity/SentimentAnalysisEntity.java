package h12.sentiment.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * Entidade mapeada para Spring Data R2DBC.
 * As colunas usam snake_case para casar com o migration do Flyway.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sentiment_analysis")
public class SentimentAnalysisEntity {

  @Id
  private Long id;

  @Column("original_text")
  private String originalText;

  @Column("model_type")
  private String modelType;

  @Column("prediction")
  private String prediction;

  @Column("probability")
  private Double probability;

  @Column("lang")
  private String language;

  /**
   * Criado no DB via DEFAULT CURRENT_TIMESTAMP (migration).
   */
  @Column("created_at")
  private LocalDateTime createdAt;
}
