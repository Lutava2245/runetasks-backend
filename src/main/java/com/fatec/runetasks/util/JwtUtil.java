package com.fatec.runetasks.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Utilitário para operações relacionadas a JWT (JSON Web Tokens).
 * <p>
 * Ele é responsável por gerar tokens JWT, extrair informações dos tokens e
 * validar os tokens.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Component
public class JwtUtil {

    /**
     * A chave secreta usada para assinar os tokens JWT.
     * <p>
     * Ela é lida do arquivo de configuração da aplicação (application.properties) e
     * deve ser uma string codificada em Base64.
     * <p>
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * O tempo de expiração dos tokens JWT, em milissegundos.
     * <p>
     * Ele é lido do arquivo de configuração da aplicação (application.properties) e
     * define por quanto tempo um token JWT é válido antes de expirar.
     * <p>
     */
    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    /**
     * Obtém a chave secreta usada para assinar os tokens JWT.
     * 
     * @return a chave secreta como um objeto {@link SecretKey}, decodificada a
     *         partir da string Base64 configurada
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera um token JWT para um usuário autenticado.
     * <p>
     * Ele cria um token JWT contendo o nome de usuário do usuário autenticado, a
     * data de emissão e a data de expiração, e o assina usando a chave secreta
     * configurada.
     * <p>
     * 
     * @param userDetails os detalhes do usuário autenticado para o qual o token
     *                    será gerado
     * @return um {@code String} representando o token JWT gerado, que pode ser
     *         usado para autenticação em requisições subsequentes
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrai o nome de usuário de um token JWT.
     * <p>
     * Ele decodifica o token JWT usando a chave secreta configurada e extrai o nome
     * de usuário do campo "sub" (subject) do token.
     * <p>
     * 
     * @param token o token JWT do qual o nome de usuário deve ser extraído
     * @return o nome de usuário extraído do token JWT, ou {@code null} se o token
     *         for inválido ou não contiver um nome de usuário
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Valida um token JWT em relação a um usuário específico.
     * <p>
     * Ele verifica se o nome de usuário extraído do token JWT corresponde ao nome
     * de usuário do {@code UserDetails} fornecido e se o token não está expirado.
     * <p>
     * 
     * @param token       o token JWT a ser validado
     * @param userDetails os detalhes do usuário contra os quais o token deve ser
     *                    validado
     * @return {@code true} se o token for válido para o usuário fornecido, ou
     *         {@code false} caso contrário (por exemplo, se o nome de usuário
     *         não corresponder ou se o token estiver expirado)
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Verifica se um token JWT está expirado.
     * <p>
     * Ele decodifica o token JWT usando a chave secreta configurada e verifica se
     * a data de expiração do token é anterior à data atual.
     * <p>
     * 
     * @param token o token JWT a ser verificado
     * @return {@code true} se o token estiver expirado, {@code false} caso
     *         contrário
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

}
