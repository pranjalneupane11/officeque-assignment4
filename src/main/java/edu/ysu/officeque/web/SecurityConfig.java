package edu.ysu.officeque.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // The scoped demo uses stateless HTTP Basic authentication, not browser sessions.
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/queue").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("STUDENT")
                        .anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService demoUsers() {
        return new InMemoryUserDetailsManager(
                User.withUsername("student1").password("{noop}demo-only").roles("STUDENT").build(),
                User.withUsername("student2").password("{noop}demo-only").roles("STUDENT").build(),
                User.withUsername("faculty1").password("{noop}demo-only").roles("FACULTY").build());
    }
}
