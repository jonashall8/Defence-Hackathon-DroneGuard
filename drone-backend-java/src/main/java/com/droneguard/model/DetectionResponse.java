package com.droneguard.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DetectionResponse {

    // These names MUST match the JSON keys from Python exactly
    @JsonProperty("class_name")
    private String className;

    @JsonProperty("confidence")
    private double confidence;

    @JsonProperty("is_drone")
    private boolean isDrone;

    // Getters and Setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public boolean isDrone() { return isDrone; }
    public void setDrone(boolean drone) { isDrone = drone; }

    @Override
    public String toString() {
        return "Detection: " + className + " (Threat: " + isDrone + ")";
    }
}