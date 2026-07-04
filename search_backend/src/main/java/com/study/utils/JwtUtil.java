package com.study.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expire}")
	private long expire;

	private Key getKey(){
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String createToken(Integer userId){
		return Jwts.builder()
				.setSubject(String.valueOf(userId))
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+expire))
				.signWith(getKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims getClaims(String token){
		return Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public Integer getUserId(String token){
		return Integer.valueOf(getClaims(token).getSubject());
	}

	public boolean validate(String token){
		try{
			getClaims(token);
			return true;
		}catch (Exception e){
			return false;
		}
	}
}