package se.onlyfin.onlyfin2backend.model;

/**
 * This class is used to contain the response from a turnstile site-verify request.
 */
public class CloudflareTurnstileResponse {
    private Boolean success;

    public Boolean isSuccess() {
        if (success == null) {
            return false;
        }

        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
