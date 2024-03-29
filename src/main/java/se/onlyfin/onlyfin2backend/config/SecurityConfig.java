package se.onlyfin.onlyfin2backend.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is used to configure the security settings in Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * This method is used to configure which endpoints are protected by roles and which are not.
     * It is here that you can see which endpoints need authentication and which do not.
     * This method is also used to set up the login form.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                //allow OPTION requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //allow specified endpoints for unauthenticated users
                .requestMatchers(
                        "/",
                        "/login",
                        "/plz",

                        "/dash/fetch-user-stocks",
                        "/dash/fetch-categories-and-modules-under-user-stock",

                        "/stocks/all",
                        "/stocks/search",

                        "/subscriptions/count",

                        "/reviews/get",

                        "/search/covers-stock",

                        "/users/whoami",
                        "/users/register",
                        "/users/search/newest",
                        "/users/search/username",
                        "/users/username",
                        "/users/about-me",
                        "/users/profile-picture"
                )
                .permitAll()

                //restrict specified endpoints to only authenticated users
                .requestMatchers(
                        "/dash/add-stock",
                        "/dash/delete-stock",
                        "/dash/add-category",
                        "/dash/fetch-module",
                        "/dash/update-category",
                        "/dash/delete-category",
                        "/dash/add-module",
                        "/dash/update-module",
                        "/dash/update-module-layout",
                        "/dash/update-module-layout-batch",
                        "/dash/delete-module",
                        "/dash/metadata",

                        "/stocks/add-custom-stock",
                        "/stocks/delete-custom-stock",

                        "/subscriptions/add",
                        "/subscriptions/remove",
                        "/subscriptions/check",
                        "/subscriptions/list",

                        "/reviews/push",
                        "/reviews/delete",
                        "/reviews/my-review",

                        "/users/update-about-me",
                        "/users/password-change",
                        "/users/update-profile-picture"
                )
                .hasRole("USER")
        );

        http.formLogin(loginForm -> loginForm
                .loginProcessingUrl("/plz")
                .successHandler(new LoginSuccessHandlerDoNothingImpl())
                .failureHandler(new LoginFailureHandlerDoNothingImpl()))
                .logout(logoutConfigurer ->
                        logoutConfigurer.deleteCookies("JSESSIONID"));

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    /**
     * This method is used to configure which password encoder to use.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method is used to configure CORS globally for the application.
     *
     * @return a WebMvcConfigurer that allows CORS from localhost:3000
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000").allowCredentials(true);
            }
        };
    }

}
