package com.employeeservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Map;

@Component
public class OpaClient {

    private final WebClient webClient;

    @Value("${opa.service.url}")
    private String opaUrl;

    public OpaClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Boolean> isAllowed(Map<String, Object> input) {
        // The input is wrapped in a JSON object with a single key "input"
        return webClient.post()
                .uri(opaUrl)
                .bodyValue(Map.of("input", input))
                .retrieve()
                .bodyToMono(OpaResponse.class)
                .map(OpaResponse::isResult)
                .onErrorReturn(false); // Fail-closed: if OPA is down or errors, deny access.
    }

    // Helper class to deserialize OPA's response JSON: {"result": true}
    private static class OpaResponse {
        @JsonProperty("result")
        private boolean result;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }
}