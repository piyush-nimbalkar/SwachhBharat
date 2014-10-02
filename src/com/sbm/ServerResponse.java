package com.sbm;

public class ServerResponse {

    private int statusCode;
    private String message;

    public ServerResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

}
