package com.novoda;

public class Response {

    String message;

    public Response() {
        // aws use
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
