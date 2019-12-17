package com.rar.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GenerateJWT {

    private static final long JWT_TOKEN_VALIDITY = (long )7* 24 * 60 * 60;
    @Value("${jwt.secret}")
    private String secret;


    public String generateToken(String email){
        String generatedToken = Jwts.builder()
                .setSubject(String.valueOf(email))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return generatedToken;
    }
}
