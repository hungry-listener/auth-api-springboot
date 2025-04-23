package com.ekagra.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ekagra.auth.exceptions.HandleJwtFilterExceptions;
import com.ekagra.auth.exceptions.KeyLoadingException;
import com.ekagra.auth.exceptions.TokenExpiredException;
import com.ekagra.auth.utils.JwtUtils;
import com.ekagra.auth.utils.KeyUtils;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final KeyUtils keyUtils;
    private final HandleJwtFilterExceptions exceptionHandler = new HandleJwtFilterExceptions();

    public JwtAuthenticationFilter(JwtUtils jwtUtils, KeyUtils keyUtils) {
        this.jwtUtils = jwtUtils;
        this.keyUtils = keyUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try{
        
            PublicKey publicKey = keyUtils.getPublicKey();
            var claims = jwtUtils.parseToken(token, publicKey);

            String email = claims.getSubject();
            String role = (String) claims.get("role");

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
            var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }catch (TokenExpiredException ex) {
            exceptionHandler.handleTokenExpired(response, ex);
        } catch (KeyLoadingException ex) {
            exceptionHandler.handleKeyLoadingException(response, ex);
        } catch (RuntimeException ex) {
            exceptionHandler.handleInvalidToken(response, ex);
        } catch (Exception ex){
            exceptionHandler.handleAllOtherExceptions(response, ex);
        }

    }
}