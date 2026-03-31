package com.security.demo.config;

import com.security.demo.security.oidc.AppOidcUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    private final AppOidcUserService appOidcUserService;

    @Value("${app.security.oauth2.login-uri:/login}")
    private String loginUri;

    public SecurityConfig(AppOidcUserService appOidcUserService) {
        this.appOidcUserService = appOidcUserService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider
    ) throws Exception {
        boolean oidcEnabled = clientRegistrationRepositoryProvider.getIfAvailable() != null;

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
        );

        if (oidcEnabled) {
            http.oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(appOidcUserService))
                    .defaultSuccessUrl("/menu", true)
            );
            http.exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint(authenticationEntryPoint())
            );
        } else {
            http.formLogin(formLogin -> formLogin.defaultSuccessUrl("/menu", true));
        }

        http.logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint(loginUri);
    }
}
