package com.kefu.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.kefu.filter.AdminRoleFilter;
import com.kefu.filter.TokenAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(new TokenAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new AdminRoleFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED))
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(publicMatchers()).permitAll()
                .requestMatchers("/api/admin/users/my-permissions").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/config/**").hasRole("ADMIN")
                .requestMatchers("/api/license/**").hasRole("ADMIN")
                .requestMatchers("/api/payment/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private RequestMatcher[] publicMatchers() {
        return Arrays.stream(new String[] {
            "/",
            "/index.html",
            "/favicon.ico",
            "/manifest.webmanifest",
            "/sw.js",
            "/css/**",
            "/js/**",
            "/img/**",
            "/fonts/**",
            "/assets/**",
            "/landing/**",
            "/kjs-assets/**",
            "/share-assets/**",
            "/fanghong/**",
            "/404.html",
            "/welcome.html",
            "/maintenance.html",
            "/blocked.html",
            "/notfound.html",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/**",
            "/login",
            "/logout",
            "/api/auth/login",
            "/api/auth/me",
            "/api/auth/logout",
            "/api/auth/guest-permissions",
            "/api/auth/change-password",
            "/api/auth/set-guest-password",
            "/api/login/**",
            "/api/announcement/**",
            "/api/red-envelope/**",
            "/api/payment/**",
            "/api/recharge/**",
            "/api/settings/**",
            "/api/user/**",
            "/api/order/**",
            "/api/chat/**",
            "/api/push/**",
            "/error"
        }).map(AntPathRequestMatcher::new).toArray(RequestMatcher[]::new);
    }
}
