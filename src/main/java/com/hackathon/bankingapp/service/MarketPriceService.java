package com.hackathon.bankingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Service
public class MarketPriceService {

    private static final String URL = "https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MarketPriceService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, BigDecimal> getMarketPrices() throws JsonProcessingException {
            String response = restTemplate.getForObject(URL, String.class);
            return  objectMapper.readValue(response, new TypeReference<>() {});
    }

    public BigDecimal getAssetPrice(String assetSymbol) throws JsonProcessingException {
        return getMarketPrices().get(assetSymbol);
    }
}