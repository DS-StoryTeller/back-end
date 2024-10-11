package com.cojac.storyteller.common.deploy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.serverAddress}")
    private String serverAddress;

    @GetMapping("/healthCheck")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> responseData = new TreeMap<>();

        responseData.put("env", env);
        responseData.put("serverPort", serverPort);
        responseData.put("serverAddress", serverAddress);

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }
}
