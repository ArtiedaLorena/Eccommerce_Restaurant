package com.lorena.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration  // Indica que esta clase contiene configuraciones de beans que se registrarán en el contexto de Spring.
@EnableWebSecurity  // Habilita la seguridad web de Spring y permite la personalización de la configuración de seguridad.
public class AppConfig {

    // Define un bean que configura la cadena de filtros de seguridad para la aplicación.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configura la política de gestión de sesiones como STATELESS (sin estado), lo que significa que no se usará sesión HTTP.
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(Authorize -> Authorize
                        // Especifica que las solicitudes que coinciden con el patrón "/api/admin/**" requieren que el usuario tenga los roles "RESTAURANT_OWNER" o "ADMIN".
                        .requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN")
                        // Cualquier solicitud que coincida con el patrón "/api/**" requiere que el usuario esté autenticado.
                        .requestMatchers("/api/**").authenticated()
                        // Permite todas las demás solicitudes sin autenticación.
                        .anyRequest().permitAll()
                )
                // Añade un filtro personalizado (JwtTokenValidator) antes de que se ejecute el filtro de autenticación básica (BasicAuthenticationFilter).
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                // Desactiva la protección CSRF, ya que no es necesaria para APIs RESTful sin estado.
                .csrf(csrf -> csrf.disable())
                // Habilita la configuración CORS (Cross-Origin Resource Sharing) utilizando un método personalizado.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));


        return http.build();
    }

    // Método privado que proporciona una fuente de configuración CORS para permitir solicitudes desde dominios específicos.
    private CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();
                // Especifica los orígenes permitidos (dominios desde los cuales se pueden hacer solicitudes).
                cfg.setAllowedOrigins(Arrays.asList(
                        "http://artiedalorena-food.varcel.app",
                        "http://localhost:3000"
                ));
                // Permite todos los métodos HTTP (GET, POST, etc.).
                cfg.setAllowedMethods(Collections.singletonList("*"));
                // Permite el uso de credenciales (cookies, encabezados de autorización).
                cfg.setAllowCredentials(true);
                // Permite todos los encabezados en las solicitudes.
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                // Especifica los encabezados que se expondrán al cliente.
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                // Configura el tiempo máximo en segundos que los resultados de la solicitud CORS pueden ser almacenados en caché.
                cfg.setMaxAge(3600L);

                return cfg;
            }
        };
    }

    // Define un bean que proporciona un encriptador de contraseñas usando el algoritmo BCrypt.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
