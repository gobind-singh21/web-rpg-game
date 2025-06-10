package com.rpg_game.game.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.Customizer;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder; 
import org.springframework.security.oauth2.jwt.JwtEncoder; 
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder; 
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder; 
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter; 

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JWTSecurityConfiguration {

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // This is a password encoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authentificationConfiguration) throws Exception {
        return authentificationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true)
                        .maxAge(3600); // How long pre-flight requests can be cached
            }
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        System.out.println("DEBUG: jwtSecret loaded: " + jwtSecret);
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        System.out.println("DEBUG: jwtSecret bytes length: " + secretBytes.length);
        
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSha256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSha256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                               .macAlgorithm(MacAlgorithm.HS256) 
                               .build();
    }

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(""); 
        converter.setAuthoritiesClaimName("roles"); 
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) 
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/api/auth/signup", 
                    "/api/auth/login", 
                    "/api/test/public", 
                    "api/auth/forgot-password/request",
                    "api/auth/reset-password-with-code",
                    "api/characters/all",
                    "/api/test/public/action/basic", 
                    "/api/test/public/action/skill")
                .permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                )
            );
        return http.build();
    }
}
