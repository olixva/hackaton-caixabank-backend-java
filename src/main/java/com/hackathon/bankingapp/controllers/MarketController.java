package com.hackathon.bankingapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.hackathon.bankingapp.service.MarketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
@Tag(name = "Market", description = "Prices of assets in the market")
public class MarketController {

    private final MarketPriceService marketPriceService;

    @Operation(
            summary = "List all assets prices",
            description = "Returns the prices of all assets in the market"
    )
    @GetMapping(value = "/prices")
    public ResponseEntity<Map<String, BigDecimal>> getMarketsPrices() throws JsonProcessingException {
        return ResponseEntity.ok(marketPriceService.getMarketPrices());
    }

    @Operation(
            summary = "Get asset price",
            description = "Returns the price of a specific asset"
    )
    @GetMapping(value = "/prices/{symbol}")
    public ResponseEntity<BigDecimal> getAssetPrice(@PathVariable String symbol) throws JsonProcessingException {
        return ResponseEntity.ok(marketPriceService.getAssetPrice(symbol));
    }
}