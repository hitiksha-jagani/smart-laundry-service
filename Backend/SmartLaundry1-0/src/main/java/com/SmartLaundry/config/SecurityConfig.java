package com.SmartLaundry.config;

import com.SmartLaundry.filter.JwtFilter;
import com.SmartLaundry.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter filter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/register", "/login", "/otp/**").permitAll()  // public endpoints
//                        .anyRequest().authenticated()  // all other requests need auth
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // stateless
//                .authenticationProvider(authenticationProvider())  // set custom provider
//                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);  // add JWT filter
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since you're using JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Enable CORS with default settings, configure separately if needed
                .cors(Customizer.withDefaults())
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/otp/**", "/publish/**").permitAll() // public endpoints
                        .anyRequest().authenticated() // all others require authentication
                )
                // Use stateless session management for JWT-based auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Register custom authentication provider
                .authenticationProvider(authenticationProvider())
                // Add your JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public UserDetailsService UserDetailsService(){
        UserDetails userDetails = User.builder()
                .username("person")
                .password(passwordEncoder().encode("secret123"))
                .roles("DELIVERY_AGENT")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }


}
