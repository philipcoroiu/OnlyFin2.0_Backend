package se.onlyfin.onlyfin2backend.model;

import lombok.Getter;

/**
 * This class is used to contain a turnstile site-verify request.
 */
@Getter
public class CloudflareTurnstileRequest {
    private String secret;
    private String response;
    private String remoteIp;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
