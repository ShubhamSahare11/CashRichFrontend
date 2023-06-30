package com.cashrich.coinrich.vo;

import android.app.Application;

public class SessionVo extends Application {
    private String sessionId;
    private Long validTill;
    private String fullName;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getValidTill() {
        return validTill;
    }

    public void setValidTill(Long validTill) {
        this.validTill = validTill;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void destroySession() {
        this.sessionId = null;
        this.validTill = null;
        this.fullName = null;
    }
}
