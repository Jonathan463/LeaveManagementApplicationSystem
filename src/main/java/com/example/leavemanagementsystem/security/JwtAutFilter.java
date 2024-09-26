package com.example.leavemanagementsystem.security;

import com.example.leavemanagementsystem.model.Staff;
import com.example.leavemanagementsystem.repository.StaffRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;



import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAutFilter extends OncePerRequestFilter {


        private final  JwtAuthenticationHelper jwtHelper;

        private final StaffRepository staffRepository;


        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {


            String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            String username;
            String token;
            if(requestHeader!=null && requestHeader.startsWith("Bearer"))
            {
                token = requestHeader.substring(7);
                username= jwtHelper.getUsernameFromToken(token);
                if(username!= null && SecurityContextHolder.getContext().getAuthentication() == null)
                {

                    Staff userDetails = staffRepository.findByEmail(username).orElseThrow( () -> new UsernameNotFoundException("User not found"));
                    log.info("");
                    if(Boolean.FALSE.equals(jwtHelper.isTokenExpired(token)))
                    {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        log.info("UserPasswordToken {} ",usernamePasswordAuthenticationToken);
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }

                }
            }
            filterChain.doFilter(request, response);
        }

    }

