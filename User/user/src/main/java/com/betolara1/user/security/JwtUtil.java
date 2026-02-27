package com.betolara1.user.security;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // CHAVE SECRETA PARA ASSINAR O TOKEN
    @Value("${secret.key}")
    private String key;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    // ESSE METODO VAI GERAR O TOKEN PRONTO 
    public String generateToken(String username){
        return Jwts.builder()
                    .setSubject(username)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
    }


    // METODO PARA EXTRAIR O USUARIO
    public String extractUsername(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token).getBody().getSubject();
    }


    // METODO PARA VALIDAR O TOKEN
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        }
        catch(JwtException e){
            return false;
        }
    }
}