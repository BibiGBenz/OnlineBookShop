package com.fs.onlinebookshop.Security;

import com.fs.onlinebookshop.Entity.Token;
import com.fs.onlinebookshop.Filter.JwtAuthFilter;
import com.fs.onlinebookshop.Repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Autowired
    private TokenRepository tokenRepository;

    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter(exceptionResolver);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer-> customizer
                        .requestMatchers("/user/register","/user/login").permitAll()
                        .requestMatchers("/user/admin/**","/category/admin/**","/book/admin/**","/booking/admin/**","/payment/admin/**")
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/user/delete","/my-info","/user/update","/user/changePassword","/category/all","/category/find/**",
                                "/book/all","/book/find/**","/booking/order","/booking/myBooking","/booking/cancel/**","/payment/make/**","/payment/check",
                                "/invoice/download/**","/booking/newBookings","/invoice/download","/user/logout")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                        .anyRequest().hasAnyAuthority("ROLE_USER","ROLE_ADMIN")
                )
                .sessionManagement(customizer->
                        customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/user/logout").addLogoutHandler(customLogoutHandler())
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return security.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public LogoutHandler customLogoutHandler(){
        return (request, response, authentication) -> {
            String authHeader=request.getHeader("Authorization");
            String token;
            if(authHeader==null && !authHeader.startsWith("Bearer")){
                return;
            }
            token=authHeader.substring(7);
            Token storedToken=tokenRepository.findByToken(token).orElse(null);

            if (storedToken!=null){
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
            }
            try {
                response.getWriter().write("Logged Out Successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        };
    }


}
