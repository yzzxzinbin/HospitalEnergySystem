package com.hospital.energymgmt.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher; // 新增导入
import org.springframework.web.filter.OncePerRequestFilter;
import com.hospital.energymgmt.service.UserService;
import com.hospital.energymgmt.util.JwtTokenUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
// import java.util.Arrays; // 如果使用 Arrays.asList 定义 patterns
// import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    // 定义一组路径模式，这些路径的请求应该跳过JWT检查
    // 这些路径应与 SecurityConfig.java 中 permitAll() 和 web.ignoring() 配置的路径保持一致
    private static final String[] PATHS_TO_IGNORE_JWT_CHECK = new String[]{
            // Spring Security permitAll() 路径
            "/",
            "/index.html",
            "/login",         // 前端路由，实际由 index.html 提供服务
            "/register",      // 前端路由
            "/api/auth/login",
            "/api/auth/register",
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
            "/swagger-ui/**",
            // 静态资源路径 (虽然 web.ignoring() 应该处理这些，但作为补充检查)
            "/css/**",
            "/js/**",
            "/img/**",
            "/images/**",
            "/assets/**",
            "/fonts/**",
            "/favicon.ico"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 检查当前请求的URI是否匹配任何应忽略JWT检查的模式
        for (String pattern : PATHS_TO_IGNORE_JWT_CHECK) {
            if (pathMatcher.match(pattern, requestURI)) {
                chain.doFilter(request, response); // 如果匹配，则直接放行，不进行JWT检查
                return;
            }
        }

        // --- 原有的JWT处理逻辑开始 ---
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            // 只有在请求不是公共路径且没有有效的Bearer Token时，此警告才有意义
            // 对于需要认证的路径，如果Token格式不正确，此警告是正常的
            logger.warn("JWT Token does not begin with Bearer String for request: " + requestURI);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // --- 原有的JWT处理逻辑结束 ---
        chain.doFilter(request, response);
    }
}
