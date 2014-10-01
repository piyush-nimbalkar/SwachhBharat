package com.sbm;

public class ServerResponse {
    private int statusCode;
    private String message;

    public ServerResponse(int statusCode_, String message_) {
        statusCode = statusCode_;
        message = message_;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
