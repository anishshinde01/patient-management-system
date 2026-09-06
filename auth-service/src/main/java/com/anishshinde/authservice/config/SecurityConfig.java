package com.anishshinde.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Allow all requests here as service is internal
        // and external traffic is controlled through API Gateway.
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                // Disable CSRF as this is stateless REST API, not browser session-based app.
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * In order to not store users' raw passwords
     * Hashes are one-way: they are verified with matches(), not decoded.
     *
     * @return BCrypt encoder for securely hashing and verifying passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
