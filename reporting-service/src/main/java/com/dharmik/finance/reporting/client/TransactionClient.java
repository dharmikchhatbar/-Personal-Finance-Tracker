package com.dharmik.finance.reporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "transaction-service", url = "http://localhost:8080")
public interface TransactionClient {

    @GetMapping("/api/transactions")
    List<Map<String, Object>> getTransactions(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestHeader("Authorization") String token
    );
}