package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	//constante uma sha 256 que sera nossa chave de codificação do token
	public static final String SECRET = "8b17a0a002a8b00e9943297489f9b3d547d51e373ea7a98310a48d7c8c7e728c";

	//chave secret foi,codificada base64
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	//recupera as informações do token
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}

	//extrair os claims -> informações token
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	//extrai o user name do token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	//extrai data que o token deve expirar
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	
	//valida que o token não expirou ainda
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());//validade token 11/0316:35
	}

	//verifica validade do usuario e validade do token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	//forma o token com seus claims (informações) e assinatura
	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	/*
	 * data atual + 1000 *60 *60 => 1h
	 */
	
	//gera o toke chamando o metodo anterior
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

}
