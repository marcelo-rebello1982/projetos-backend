package br.com.cadastroit.services.config;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JWTTokenUtil implements Serializable {

	private static final long serialVersionUID = -6586911795436612150L;

	// EXPIRATION_TIME = 1 hour
	// csf-secret-acess-key-get-values-views 
	// Crypt 1 layer = Y3NmLXNlY3JldC1hY2Vzcy1rZXktZ2V0LXZhbHVlcy12aWV3cw==,
	// Crypt 2 layer = WTNObUxYTmxZM0psZEMxaFkyVnpjeTFyWlhrdFoyVjBMWFpoYkhWbGN5MTJhV1YzY3c9PQ==
	static final long EXPIRATION_TIME = (1000*60*60*24);
	static final String SECRET = System.getenv("SECRET") != null ? System.getenv("SECRET") : "WTNObUxYTmxZM0psZEMxaFkyVnpjeTFyWlhrdFoyVjBMWFpoYkhWbGN5MTJhV1YzY3c9PQ==";
	static final String TOKEN_PREFIX 	= "Bearer";
	static final String HEADER_STRING 	= "Authorization";
	
	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}


	// validate token
	public Boolean validateToken(String token, String userNoSQL) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userNoSQL) && !isTokenExpired(token));
	}
}
