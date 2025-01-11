package com.dharmik.finance.reporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Map;

@FeignClient(name = "mlPredictionClient", url = "http://localhost:5000")
public interface MLPredictionClient {
    @PostMapping(value = "/predict", consumes = "application/json")
    Map<String, Double> getPrediction(@RequestBody Map<String, Object> request, @RequestHeader("Content-Type") String contentType);
}