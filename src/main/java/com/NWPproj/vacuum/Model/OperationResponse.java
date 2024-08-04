package com.NWPproj.vacuum.Model;

public class OperationResponse {
    private String message;
    private long remainingTimeSeconds;

    public OperationResponse(String message, long remainingTimeSeconds) {
        this.message = message;
        this.remainingTimeSeconds = remainingTimeSeconds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getRemainingTimeSeconds() {
        return remainingTimeSeconds;
    }

    public void setRemainingTimeSeconds(long remainingTimeSeconds) {
        this.remainingTimeSeconds = remainingTimeSeconds;
    }
}