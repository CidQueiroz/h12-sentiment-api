package h12.sentiment.api.dto;

// Usado para queries de projeção customizadas do JPA
public interface LanguageProjection {
    String getLanguage();
    Long getCount();
}
