package br.com.cadastroit.services.config.security;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import br.com.cadastroit.services.config.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenCriteria implements Serializable {

	private static final long serialVersionUID = -6586911795436612150L;
	
	//Expire = 1 hour
	//Key  	 = cst-token-secret-20XX
	//Hash 	 = $2y$12$Gy83Sjhg2be8dTU2anBWmu89W5UE0tmV0F6K365wK/ZHfu.W7Jdey
	static final long EXPIRATION_TIME = System.getenv("expire") != null ? (Long.parseLong(System.getenv("expire"))*(1000*60*60*24)) : (1000*60*60*24);
	static final String SECRET = System.getenv("SECRET") != null ? System.getenv("SECRET") : "JDJ5JDEyJEd5ODNTamhnMmJlOGRUVTJhbkJXbXU4OVc1VUUwdG1WMEY2SzM2NXdLL1pIZnUuVzdKZGV5";
	static final String TOKEN_PREFIX 	= "Bearer";
	static final String HEADER_STRING 	= "Authorization";
	
	@Builder.Default
	private long expiration 	= 0;
	
	@Builder.Default
	private String dateExpire 	= "";

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

	// generate token for user
	public String generateToken(User user) {
		Map<String, Object> claims 	= new HashMap<>();
		Date expiration 			= new Date(System.currentTimeMillis()+EXPIRATION_TIME);
		this.setExpiration(expiration.getTime());
		this.setDateExpire(DateFormat.getDateTimeInstance().format(expiration));
		return doGenerateToken(claims, user.getUsername(), expiration);
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject, Date expiration) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
	}

	// validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
