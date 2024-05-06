package com.exe.whateat.infrastructure.security;

import com.exe.whateat.infrastructure.security.jwt.WhatEatJwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WhatEatSecurityConfiguration {

    @Value("${whateat.api.path}")
    private String apiPath;

    private final WhatEatJwtAuthenticationFilter jwtAuthenticationFilter;
    private final Environment environment;

    @Autowired
    public WhatEatSecurityConfiguration(WhatEatJwtAuthenticationFilter jwtAuthenticationFilter,
                                        Environment environment) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET).permitAll())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/docs/**")).permitAll())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/auth/**")).permitAll());
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            httpSecurity.authorizeHttpRequests(c -> c.requestMatchers(resolvePath("/test/**")).permitAll());
        }
        httpSecurity.authorizeHttpRequests(c -> c.anyRequest().authenticated());
        return httpSecurity.build();
    }

    private String resolvePath(String path) {
        return (apiPath + path);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
