package br.com.cadastroit.services.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cadastroit.services.controllers.model.CryptQuery;
import br.com.cadastroit.services.controllers.model.UserDetailsJwt;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin(value = {"*"})
@RestController
@RequestMapping(path = "security")
public class SecurityController {

	@Autowired
	private MongoTemplate mongoTemplate;

	@ApiOperation(value = "Return if services are online and check your authorization code.")
	@GetMapping(path = "/info")
	public ResponseEntity<Object> info(@ApiKeyAuthDefinition(description = "Token was provided by JWT System.", name = "token", in = ApiKeyLocation.HEADER, key = "Bearer") 
									   @RequestHeader(name="Authorization") String token) {
		boolean valid 	= token.contains("Bearer");
		if(!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is wrong format, please check it...");
		String jwtToken = token.replace("Bearer", "").trim();
		UserDetailsJwt userDetailsJwt = this.mongoTemplate.findOne(new Query(Criteria.where("jwttoken").is(jwtToken)), UserDetailsJwt.class);
		if (userDetailsJwt != null) {
			Long expire = System.currentTimeMillis();
			if (userDetailsJwt.getExpire() > expire) {// Token valido
				return ResponseEntity.ok("It's online");
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token is invalid...");
			}
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is invalid...");
		}
	}
	
	@ApiOperation(value = "A single host do crypt queries using Base64 algorithm")
	@PostMapping("/crypt-query")
	public ResponseEntity<String> cryptQuery(
			@ApiKeyAuthDefinition(description = "Token was provided by JWT System.", name = "token", in = ApiKeyLocation.HEADER, key = "Bearer")
			@RequestHeader(name="Authorization") String token,
			@ApiParam(value = "A query-string that should be crypt using Base64 Algorithm. To crypt, fill only \"qs\" field")
			@RequestBody CryptQuery cryptQuery){
		
		try {
			boolean valid 	= token.contains("Bearer");
			if(!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is wrong format, please check it...");
			String jwtToken = token.replace("Bearer", "").trim();
			UserDetailsJwt userDetailsJwt = this.mongoTemplate.findOne(new Query(Criteria.where("jwttoken").is(jwtToken)),UserDetailsJwt.class);
			if(userDetailsJwt != null) {
				Long expire = System.currentTimeMillis();
				if(userDetailsJwt.getExpire() > expire){//Token valido
					String qsCrypt = "";
					int i = 0;
					while(i < 3) {
						qsCrypt = qsCrypt.equals("") ? Base64.getEncoder().encodeToString(cryptQuery.getQs().getBytes())
													 : Base64.getEncoder().encodeToString(qsCrypt.getBytes());
						i++;
					}
					return ResponseEntity.ok(qsCrypt);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token that was used "+token+" was expired...Please refresh token and try again...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token "+token+" was not found...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error on build crypt-query, [error] = "+ex.getMessage());
		}
	}
	
	@ApiOperation(value = "A single host do decrypt queries that was crypt by Base64 algorithm")
	@PostMapping("/decrypt-query")
	public ResponseEntity<String> decryptQuery(
			@ApiKeyAuthDefinition(description = "Token was provided by JWT System.", name = "token", in = ApiKeyLocation.HEADER, key = "Bearer")
			@RequestHeader(name = "Authorization") String token,
			@ApiParam(value = "A query-string that should be validate using Base64 Algorithm. To decrypt, fill only \"cryptQuery\" field")
		    @RequestBody CryptQuery cryptQuery){
		try {
			boolean valid 	= token.contains("Bearer");
			if(!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is wrong format, please check it...");
			
			String jwtToken = token.replace("Bearer", "").trim();
			UserDetailsJwt userDetailsJwt = this.mongoTemplate.findOne(new Query(Criteria.where("jwttoken").is(jwtToken)),UserDetailsJwt.class);
			if(userDetailsJwt != null) {
				Long expire = System.currentTimeMillis();
				if(userDetailsJwt.getExpire() > expire){//Token valido
					String qs = "";
					int i = 0;
					while(i < 3) {
						qs = qs.equals("") ? new String(Base64.getDecoder().decode(cryptQuery.getCryptQuery()))
										   : new String(Base64.getDecoder().decode(qs));
						i++;
					}
					return ResponseEntity.ok(qs);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token that was used "+token+" was expired...Please refresh token and try again...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token "+token+" was not found...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error on build crypt-query, [error] = "+ex.getMessage());
		}
	}

}
