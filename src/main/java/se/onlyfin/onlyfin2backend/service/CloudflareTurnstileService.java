package se.onlyfin.onlyfin2backend.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.onlyfin.onlyfin2backend.model.CloudflareTurnstileRequest;
import se.onlyfin.onlyfin2backend.model.CloudflareTurnstileResponse;

/**
 * This class is responsible for handling Cloudflare Turnstile verification requests
 */
@Service
public class CloudflareTurnstileService {
    @Value("${TURNSTILE_SECRET}")
    private String turnstileSecret;
    private final RestTemplate restTemplate;

    public CloudflareTurnstileService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Verifies a Turnstile token against Cloudflare's servers. Returns whether the token is valid or not
     *
     * @param turnstileRequest the Turnstile request. See: {@link CloudflareTurnstileRequest}
     * @return true if valid else false
     */
    public boolean verifyToken(@NonNull CloudflareTurnstileRequest turnstileRequest) {
        turnstileRequest.setSecret(turnstileSecret);

        ResponseEntity<CloudflareTurnstileResponse> responseEntity = restTemplate.postForEntity(
                "https://challenges.cloudflare.com/turnstile/v0/siteverify",
                turnstileRequest,
                CloudflareTurnstileResponse.class);

        CloudflareTurnstileResponse response = responseEntity.getBody();

        if (response == null) {
            return false;
        }
        if (response.isSuccess() == null) {
            return false;
        }

        return response.isSuccess();
    }

}
