package com.exe.whateat.infrastructure.security;

import com.exe.whateat.entity.account.AccountRole;
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
    private final WhatEatAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public WhatEatSecurityConfiguration(WhatEatJwtAuthenticationFilter jwtAuthenticationFilter,
                                        Environment environment, WhatEatAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.environment = environment;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> c.accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/docs/**")).permitAll())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/auth/**")).permitAll())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, "/error").permitAll());
        handleAccountApi(http);
        handleRestaurantApi(http);
        handleFoodApi(http);
        handleTagApi(http);
        handleRandomApi(http);
        handleFoodTagApi(http);
        handlePersonalProfileApi(http);
        handleReviewApi(http);
        handlePostCommentApi(http);
        handlePostVotingApi(http);
        handlePostApi(http);
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            http.authorizeHttpRequests(c -> c.requestMatchers(resolvePath("/test/**")).permitAll());
        }
        http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
        return http.build();
    }

    private void handleAccountApi(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/users"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/users"))
                        .permitAll())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, resolvePath("/users/*/verify"))
                        .permitAll())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath("/users/**"))
                        .hasAuthority(AccountRole.ADMIN.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, resolvePath("/users/**"))
                        .permitAll());
    }

    @SuppressWarnings("java:S1075")
    private void handleRestaurantApi(HttpSecurity http) throws Exception {
        final String restaurantPath = "/restaurants/**";
        final String restaurantIdPath = "/restaurants/{id}";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, resolvePath(restaurantPath))
                        .hasAnyAuthority(AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/restaurants/current"))
                        .hasAnyAuthority(AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(restaurantPath))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(restaurantIdPath + "/activate"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(restaurantIdPath + "/deactivate"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/restaurants"))
                        .permitAll());
    }

    private void handleFoodApi(HttpSecurity http) throws Exception {
        final String foodPath = resolvePath("/foods/**");
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/foods"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, foodPath)
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, foodPath)
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/foods/random"))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, foodPath)
                        .authenticated());
    }

    private void handleTagApi(HttpSecurity http) throws Exception {
        final String tagPath = resolvePath("/tags/**");
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/tags"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, tagPath)
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, tagPath)
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, tagPath)
                        .authenticated());
    }

    @SuppressWarnings("java:S1075")
    private void handleFoodTagApi(HttpSecurity http) throws Exception {
        final String foodTagPath = "/foodtags/**";
        final String foodTagIdPath = "/foodtags/{id}";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/foodtags"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, resolvePath(foodTagPath))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(foodTagPath))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(foodTagIdPath + "/activate"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(foodTagIdPath + "/deactivate"))
                        .hasAnyAuthority(AccountRole.ADMIN.name(), AccountRole.MANAGER.name()));
    }

    @SuppressWarnings("java:S1075")
    private void handlePostApi(HttpSecurity http) throws Exception {
        final String postPath = "/posts/**";
        final String postIdPath = "/posts/{id}";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/posts"))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, resolvePath(postIdPath))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath(postIdPath))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.MANAGER.name(), AccountRole.RESTAURANT.name(), AccountRole.ADMIN.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(postIdPath))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.MANAGER.name(), AccountRole.RESTAURANT.name(), AccountRole.ADMIN.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(postPath))
                        .permitAll());
    }

    @SuppressWarnings("java:S1075")
    private void handlePostVotingApi(HttpSecurity http) throws Exception {
        final String postVotingPath = "/posts/post-votings/**";
        final String postVotingIdPath = "/posts/post-votings/{id}";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(postVotingPath))
                        .hasAnyAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PUT, resolvePath(postVotingIdPath))
                        .hasAnyAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath(postVotingIdPath))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.MANAGER.name(), AccountRole.ADMIN.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/posts/{id}/post-votings"))
                        .hasAnyAuthority(AccountRole.USER.name()));
    }

    @SuppressWarnings("java:S1075")
    private void handlePostCommentApi(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath("/posts/{id}/comments"))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PUT, resolvePath("/posts/comments/{commentId}"))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath("/posts/comments/{commentId}"))
                        .hasAnyAuthority(AccountRole.USER.name(), AccountRole.MANAGER.name(), AccountRole.ADMIN.name(), AccountRole.RESTAURANT.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath("/posts/{id}/comments"))
                        .permitAll());
    }

    @SuppressWarnings("java:S1075")
    private void handleRandomApi(HttpSecurity http) throws Exception {
        final String path = "/foods/random";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(path + "/history"))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(path + "/status"))
                        .hasAuthority(AccountRole.USER.name()));
    }

    @SuppressWarnings("java:S1075")
    private void handlePersonalProfileApi(HttpSecurity http) throws Exception {
        final String path = "/personal-profiles";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(path))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(path))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath(path))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath(path + "/all"))
                        .hasAuthority(AccountRole.USER.name()));
    }

    private void handleReviewApi(HttpSecurity http) throws Exception {
        final String dishReviewPath = "/dishes/{id}/reviews";
        final String reviewPath = "/reviews/**";
        http.authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(dishReviewPath))
                        .authenticated())
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.POST, resolvePath(dishReviewPath))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.DELETE, resolvePath(reviewPath))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.PATCH, resolvePath(reviewPath))
                        .hasAuthority(AccountRole.USER.name()))
                .authorizeHttpRequests(c -> c.requestMatchers(HttpMethod.GET, resolvePath(reviewPath))
                        .authenticated());
    }

    private String resolvePath(String path) {
        return (apiPath + path);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
