package com.healthcare.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/",
                    "/login",
                    "/register",
                    "/api/**"
                ).permitAll()
                .requestMatchers("/patient/**").hasRole("PATIENT")
                .requestMatchers("/receptionist/**").hasRole("RECEPTIONIST")
                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                .requestMatchers("/pharmacy/**").hasAnyRole("PHARMACIST", "ADMIN")
                .requestMatchers("/laboratory/**").hasAnyRole("LAB_TECHNICIAN", "ADMIN")
                .requestMatchers("/billing/**").hasAnyRole("BILLING", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/ot/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "STAFF", "BILLING")
                .requestMatchers("/reports/**").hasAnyRole("ADMIN", "BILLING", "LAB_TECHNICIAN", "PHARMACIST")
                .requestMatchers("/staff/**").hasAnyRole("STAFF", "NURSE", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
