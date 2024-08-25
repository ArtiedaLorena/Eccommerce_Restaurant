package com.lorena.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

    // Este método se ejecuta en cada solicitud HTTP y valida el JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Se obtiene el JWT del encabezado de la solicitud
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        // Verifica si el JWT está presente
        if (jwt != null) {
            // Elimina el prefijo "Bearer " del token
            jwt = jwt.substring(7);

            try {
                // Crea una clave secreta a partir de la constante SECRET_KEy
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEy.getBytes());

                // Valida y parsea el JWT para obtener las reclamaciones (claims)
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                // Extrae el correo electrónico del JWT
                String email = String.valueOf(claims.get("email"));

                // Extrae las autoridades (roles) del JWT
                String authorities = String.valueOf(claims.get("authorities"));

                // Convierte las autoridades en una lista de objetos GrantedAuthority
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                //ROLE_CUSTOMER, ROLE_ADMIN

                // Crea un objeto de autenticación con el email y las autoridades
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);

                // Establece la autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Si ocurre algún error, lanza una excepción indicando que el token es inválido
                throw new BadCredentialsException("Invalid token...");
            }
        }

        // Continúa con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}

