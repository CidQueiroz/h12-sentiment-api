package h12.sentiment.api.model;

import h12.sentiment.api.dto.InputSentimentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysis {

  private Long id;
  private String text;
  private String previsao;
  private double probabilidade;
  private String idioma;

  public SentimentAnalysis(InputSentimentDTO inputSentimentDTO) {
    this.text = inputSentimentDTO.getText();
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setPrevisao(String previsao) {
    this.previsao = previsao;
  }

  public void setProbabilidade(double probabilidade) {
    this.probabilidade = probabilidade;
  }

  public String getIdioma() {
    return idioma;
  }

  public void setIdioma(String idioma) {
    this.idioma = idioma;
  }
}
