package h12.sentiment.api.dto;

public interface SentimentByModelProjection {
    String getModelType();
    String getPrediction();
    Long getCount();
}
