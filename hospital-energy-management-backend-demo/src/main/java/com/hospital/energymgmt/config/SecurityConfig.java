package com.hospital.energymgmt.config;

import com.hospital.energymgmt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable method-level security
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/", // SPA index.html - entry point
                "/index.html", // SPA index.html
                "/favicon.ico",
                "/css/**",      // All CSS files
                "/js/**",       // All JS files
                "/img/**",      // All image files (e.g., in public/img or static/img)
                "/images/**",   // Another common images directory
                "/assets/**",   // Assets directory
                "/fonts/**",    // All font files
                // Swagger UI paths
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                // Authentication API paths
                "/api/auth/login",
                "/api/auth/register"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                // Permit all requests to frontend routes, which will be handled by SpaController
                // and serve index.html. The actual resources (js, css) are ignored by webSecurityCustomizer.
                .antMatchers("/login", "/register", "/dashboard", "/room-management", "/device-template-management", "/device-management", "/energy-data-management").permitAll()
                
                // User management API security
                .antMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN") // For fetching all users
                .antMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/users/{id}").authenticated() // Authenticated users can attempt, @PreAuthorize will refine

                .anyRequest().authenticated()
            .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}