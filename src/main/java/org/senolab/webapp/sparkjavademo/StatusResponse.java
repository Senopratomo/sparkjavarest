package org.senolab.webapp.sparkjavademo;

public enum StatusResponse {

    SUCCESS("Success"), ERROR("Error");

    final private String status;

    StatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
