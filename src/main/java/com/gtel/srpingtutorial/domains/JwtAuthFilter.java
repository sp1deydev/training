package com.gtel.srpingtutorial.domains;

import com.gtel.srpingtutorial.domains.impl.JwtDomain;
import com.gtel.srpingtutorial.model.securities.CustomUserDetail;
import com.gtel.srpingtutorial.model.securities.UserInfoDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final DomainFactory domainFactory;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;


        if (StringUtils.isEmpty(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        TokenDomain tokenDomain = domainFactory.tokenFactory();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            String username = tokenDomain.validateToken(token);

            CustomUserDetail userDetails = new CustomUserDetail(username);
            SecurityContextHolder.getContext().setAuthentication(userDetails);
        }

        filterChain.doFilter(request, response);
    }
}
