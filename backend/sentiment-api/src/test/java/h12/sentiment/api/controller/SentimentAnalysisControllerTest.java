package h12.sentiment.api.controller;

import h12.sentiment.api.dto.InputSentimentDTO;
import h12.sentiment.api.dto.OutputSentimentDTO;
import h12.sentiment.api.service.SentimentAnalysisService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(SentimentAnalysisController.class)
class SentimentAnalysisControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Configura 30 segundos de timeout para evitar o erro de 5000ms
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @MockBean
    private SentimentAnalysisService service;

    @Test
    void shouldReturnSentimentAnalysis() {
        InputSentimentDTO input = new InputSentimentDTO();
        input.setText("Estou muito feliz!");
        input.setAlgorithm("svm");

        OutputSentimentDTO output = new OutputSentimentDTO("Positivo", 0.95);

        Mockito.when(service.createAnalysis(Mockito.any(InputSentimentDTO.class))).thenReturn(Mono.just(output));

        webTestClient.post()
                .uri("/sentiment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.previsao").isEqualTo("Positivo")
                .jsonPath("$.probabilidade").isEqualTo(0.95);
    }
}
