package se.onlyfin.onlyfin2backend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * This class is used to override the default behavior of Spring Security's login failure handler.
 * This is to disable the redirect to the default error page.
 */
public class LoginFailureHandlerDoNothingImpl implements AuthenticationFailureHandler {
    /**
     * Executed when authentication fails
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication request.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //this implementation only returns HTTP 401 on unsuccessful logins instead of issuing a redirect
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
