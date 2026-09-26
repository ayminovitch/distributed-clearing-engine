package com.aegis;

import com.aegis.presentation.dto.TradeRequestDto;
import com.aegis.presentation.dto.TradeResponseDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AegisIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2");

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @LocalServerPort
    private int port;

    @Test
    void shouldSuccessfullySubmitAndProcessTrade() {
        TradeRequestDto request = new TradeRequestDto(
                "TEST-INTEGRATION-001",
                "USD",
                "JPY",
                new BigDecimal("50000.00")
        );

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        ResponseEntity<TradeResponseDto> response = restClient.post()
                .uri("/api/v1/trades")
                .body(request)
                .retrieve()
                .toEntity(TradeResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().tradeId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo("VALIDATED");
        assertThat(response.getBody().message()).isEqualTo("Trade processed successfully");
    }
}