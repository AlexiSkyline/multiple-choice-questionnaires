package org.skyline.mcq.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.utils.CustomUserDetails;
import org.skyline.mcq.infrastructure.inputport.AccountDetailsInputPort;
import org.skyline.mcq.infrastructure.utils.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountDetailsInputPort accountDetailsInputPort;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = this.parserJwt( request );

        if(token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = this.jwtTokenProvider.extractUsername(token);
            CustomUserDetails userDetails = (CustomUserDetails) accountDetailsInputPort.loadUserByUsername(username);

            if(Boolean.TRUE.equals(jwtTokenProvider.validateToken(token, userDetails.getEmail()))){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parserJwt( HttpServletRequest request ) {

        String headerAuth = request.getHeader( "Authorization" );
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith( "Bearer ") ) {
            return headerAuth.substring( 7 );
        }

        return null;
    }
}

