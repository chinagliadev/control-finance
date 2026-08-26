package dev.chinaglia.control_finance.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    private final TokenConfig tokenConfig;
    private final UserDetailsService userDetailsService;
 
    public JwtAuthFilter(TokenConfig tokenConfig, UserDetailsService userDetailsService) {
        this.tokenConfig = tokenConfig;
        this.userDetailsService = userDetailsService;
    }
 
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
 
        String token = extrairTokenDoCookie(request);
 
        if (token != null) {
            String email = tokenConfig.validateToken(token);
            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
 
        filterChain.doFilter(request, response);
    }
 
    private String extrairTokenDoCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
 
        return Arrays.stream(request.getCookies())
                .filter(c -> "token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}