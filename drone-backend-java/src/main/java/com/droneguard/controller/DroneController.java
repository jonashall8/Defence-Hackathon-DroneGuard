package com.droneguard.controller;

import com.droneguard.model.DetectionResponse;
import com.droneguard.service.PythonBridgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/drone")
// @CrossOrigin allows your future Frontend (React/HTML) to talk to this backend
// without getting blocked by browser security policies.
@CrossOrigin(origins = "*")
public class DroneController {

    private final PythonBridgeService bridgeService;

    // Constructor Injection: Spring automatically finds and injects the Service
    public DroneController(PythonBridgeService bridgeService) {
        this.bridgeService = bridgeService;
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanAudio(@RequestParam("audio") MultipartFile file) {

        // 1. Basic Validation
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: No audio file provided.");
        }

        System.out.println("mic input received: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");

        try {
            // 2. Call the Service (which talks to Python)
            DetectionResponse result = bridgeService.analyzeAudio(file);

            // 3. Return the result
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing audio: " + e.getMessage());
        }
    }
}