package com.example.quizservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

// Feign client to call Report-service for saving quiz results
@FeignClient(name = "REPORT-SERVICE", url = "http://localhost:8083/report")
public interface ReportInterface {

    @PostMapping("/save")
    ResponseEntity<String> saveReport(@RequestBody Map<String, Object> reportData);
}
