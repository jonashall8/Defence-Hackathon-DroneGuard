package com.droneguard.service;

import com.droneguard.model.DetectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PythonBridgeService {

    private final WebClient webClient;

    public PythonBridgeService(WebClient.Builder builder) {
        // Point this to your running Python server
        this.webClient = builder.baseUrl("http://127.0.0.1:8000").build();
    }

    public DetectionResponse analyzeAudio(MultipartFile file) {
        try {
            // 1. Prepare the file to be sent over HTTP
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", file.getResource());

            // 2. Send POST request to Python
            System.out.println("📡 Sending audio to Python Brain...");

            return webClient.post()
                    .uri("/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(DetectionResponse.class)
                    .block(); // .block() waits for the answer (Simple for now)

        } catch (Exception e) {
            System.err.println("❌ Error talking to Python: " + e.getMessage());
            // Return a safe fallback or rethrow
            throw new RuntimeException("AI Service Unavailable", e);
        }
    }
}