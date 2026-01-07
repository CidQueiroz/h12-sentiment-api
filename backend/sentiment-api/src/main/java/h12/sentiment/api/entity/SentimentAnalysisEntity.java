package h12.sentiment.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sentiment_analysis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalText;

    @Column(nullable = false)
    private String modelType;

    @Column(nullable = false)
    private String prediction;

    @Column(nullable = false)
    private Double probability;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
