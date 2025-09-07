package com.employeeservice.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class OpaClient {

    private static final Logger logger = LoggerFactory.getLogger(OpaClient.class);
    private final WebClient webClient;

    @Value("${opa.service.url}")
    private String opaUrl;

    public OpaClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Boolean> isAllowed(Map<String, Object> input) {
        logger.debug("Calling OPA at URL: {}", opaUrl);
        logger.debug("OPA request payload: {}", Map.of("input", input));

        return webClient.post()
                .uri(opaUrl)
                .bodyValue(Map.of("input", input))
                .retrieve()
                .bodyToMono(OpaResponse.class)
                .doOnNext(response -> logger.debug("OPA response: {}", response))
                .map(OpaResponse::isResult)
                .doOnError(error -> logger.error("Error calling OPA service at {}: {}", opaUrl, error.getMessage()))
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

        @Override
        public String toString() {
            return "OpaResponse{result=" + result + "}";
        }
    }
}