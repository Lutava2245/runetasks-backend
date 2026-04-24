package com.fatec.runetasks.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fatec.runetasks.util.JwtAuthFilter;

import lombok.AllArgsConstructor;

/**
 * Configuração de segurança para a aplicação.
 * <p>
 * Esta classe é responsável por configurar a segurança da aplicação, definindo
 * as regras de autenticação e autorização, os filtros de segurança e os
 * provedores de autenticação. Ela utiliza JWT para autenticação e autorização,
 * garantindo que as requisições sejam protegidas e que apenas usuários
 * autenticados possam acessar os recursos protegidos da API.
 * <p>
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Configura o PasswordEncoder para a aplicação, utilizando BCrypt para hashing
     * de senhas.
     * 
     * @return um objeto {@link PasswordEncoder} configurado para a aplicação
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o DaoAuthenticationProvider para a aplicação, definindo o
     * UserDetailsService e o PasswordEncoder.
     * 
     * @return um objeto {@link DaoAuthenticationProvider} configurado para a
     *         aplicação
     */
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura o AuthenticationManager para a aplicação, utilizando a configuração
     * de autenticação definida.
     * 
     * @param authConfig a configuração de autenticação da aplicação
     * @return um objeto {@link AuthenticationManager} configurado para a aplicação
     * @throws Exception se ocorrer um erro ao configurar o AuthenticationManager
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura a cadeia de filtros de segurança para a aplicação.
     * <p>
     * Ele define as regras de autorização para as rotas, desabilita CSRF e
     * configura a
     * política de criação de sessão para stateless, além de adicionar o filtro de
     * autenticação JWT antes do filtro de autenticação padrão do Spring Security.
     * <p>
     * 
     * @param httpSecurity o objeto HttpSecurity para configurar as regras de
     *                     segurança
     * @return um objeto {@link SecurityFilterChain} configurado para a aplicação
     * @throws Exception se ocorrer um erro ao configurar a cadeia de filtros de
     *                   segurança
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/users/register", "/api/auth/**", "/error").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
