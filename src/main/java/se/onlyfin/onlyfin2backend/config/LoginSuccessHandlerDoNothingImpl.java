package se.onlyfin.onlyfin2backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * This class is used to override the default behavior of Spring Security's login success handler.
 * This is to disable the redirect to the default login success page.
 */
public class LoginSuccessHandlerDoNothingImpl implements AuthenticationSuccessHandler {
    /**
     * Executed when authentication is successful
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the Authentication object which was created during the authentication process.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //this implementation only returns HTTP 200 on successful logins instead of issuing a redirect
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
