package com.aegis.presentation;
import com.aegis.domain.CurrencyPair;
import com.aegis.domain.Trade;
import com.aegis.presentation.dto.TradeRequestDto;
import com.aegis.presentation.dto.TradeResponseDto;
import com.aegis.service.ReconciliationEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trades")
@Tag(name="Trade Clearing API", description="Endpoints for submitting and processing financial trades")
public class TradeController {
    private static final Logger log = LoggerFactory.getLogger(TradeController.class);
    private final ReconciliationEngine engine;

    public TradeController(ReconciliationEngine engine) {
        this.engine = engine;
    }

    @Operation(summary = "Submit a new trade", description = "Validates and submit a new trade into the clearing engine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trade successfuly created and queued for processing.",
            content = @Content(schema = @Schema(implementation =  TradeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload (RFC 7807 Problem Detail)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @PostMapping
    public ResponseEntity<TradeResponseDto> submitTrade(@Valid @RequestBody TradeRequestDto request) {
        log.info("Received Trade Request [{}] on thread: {}", request.externalReference(), Thread.currentThread());
        UUID tradeId = UUID.randomUUID();
        CurrencyPair pair = new CurrencyPair(request.baseCurrency(), request.quoteCurrency());
        Trade trade = new Trade(tradeId, request.externalReference(), pair, request.amount());
        
        engine.submitTrade(trade);
        engine.processTrade(trade);
        
        TradeResponseDto response = new TradeResponseDto(tradeId, trade.getStatus().name(), "Trade processed successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
