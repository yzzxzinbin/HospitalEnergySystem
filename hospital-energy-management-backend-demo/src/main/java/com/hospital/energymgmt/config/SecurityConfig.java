package com.hospital.energymgmt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder; // 确保导入

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 从 SecurityConfig 中移除 PasswordEncoder Bean 的定义
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception { // 修改这里
        auth
            .inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder.encode("admin_password")) // 使用注入的 passwordEncoder
                .roles("ADMIN", "USER"); // 你可以根据需要分配角色
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(
                    "/api/auth/**",
                    // Swagger UI v2
                    "/v2/api-docs",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui.html",
                    "/webjars/**",
                    // Swagger UI v3 (OpenAPI)
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                ).permitAll() // 允许访问认证API和Swagger UI
                .anyRequest().authenticated() // 其他所有请求都需要认证
            .and()
            .formLogin() // 启用表单登录
                // .loginPage("/login") // 如果你有自定义登录页面，可以在这里指定
                .permitAll() // 允许所有人访问登录页面
            .and()
            .logout()
                .permitAll() // 允许所有人访问登出
            .and()
            .httpBasic(); // 启用HTTP Basic认证
    }
}