package se.onlyfin.onlyfin2backend.model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import se.onlyfin.onlyfin2backend.service.CloudflareTurnstileService;

import java.util.Objects;

/**
 * This filter intercepts login attempts to check if the provided Turnstile token is valid.
 */
public class TurnstileLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final CloudflareTurnstileService turnstileService;

    public TurnstileLoginFilter() {
        this.turnstileService = new CloudflareTurnstileService();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String turnstileToken = request.getParameter("turnstileToken");

        CloudflareTurnstileRequest turnstileRequest = new CloudflareTurnstileRequest();
        turnstileRequest.setResponse(turnstileToken);
        turnstileRequest.setRemoteIp(Objects.requireNonNullElse(request.getHeader("CF-Connecting-IP"), "localhost")); //TODO: remove localhost fallback in prod!
        if (!turnstileService.verifyToken(turnstileRequest)) {
            throw new InsufficientAuthenticationException("Token error");
        }

        return super.attemptAuthentication(request, response);
    }
}
