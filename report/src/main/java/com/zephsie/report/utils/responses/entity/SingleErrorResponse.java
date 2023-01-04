package com.zephsie.report.utils.responses.entity;

import com.zephsie.report.utils.responses.api.ErrorResponse;

public class SingleErrorResponse extends ErrorResponse {
    private String message;

    public SingleErrorResponse(String logref, String message) {
        super(logref);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}