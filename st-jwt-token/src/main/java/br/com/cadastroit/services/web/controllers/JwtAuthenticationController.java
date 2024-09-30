package br.com.cadastroit.services.web.controllers;

import br.com.cadastroit.services.config.security.JwtTokenUtil;
import br.com.cadastroit.services.config.security.JwtUserDetailsService;
import br.com.cadastroit.services.config.security.model.UserDetailsJwt;
import br.com.cadastroit.services.config.security.model.UserGroupJwt;
import br.com.cadastroit.services.repository.UserDetailJwtRepository;
import br.com.cadastroit.services.web.controllers.dto.JwtRequest;
import br.com.cadastroit.services.web.controllers.dto.JwtResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@CrossOrigin(value = {"*"})
@RequestMapping(path = "authenticate")
@RestController
@RequiredArgsConstructor
public class JwtAuthenticationController {

	private final MongoTemplate mongoTemplate;
	private final PasswordEncoder passwordEncoder;
	private final UserDetailJwtRepository userDetailJwtRepository;
	private JwtTokenUtil jwtTokenUtil;
	private JwtUserDetailsService userDetailsService;
	
	@ApiOperation(value = "Check username / password and returns a access-token that has 24 hours of validity")
	@PostMapping(value = "/token")
	public ResponseEntity<?> createAuthenticationToken(
			@ApiParam(required = true, value = "Fill the object JwtRequest with username and password only")
			@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			long expire = System.currentTimeMillis();
			Criteria criteria = Criteria.where("username").is(jwtRequest.getUsername());
			UserDetailsJwt userDetailsJwt = this.validarUsuarioNoSQL(criteria, jwtRequest);
			if(userDetailsJwt != null && userDetailsJwt.get_id() != null) {
				boolean matchPassword = passwordEncoder.matches(jwtRequest.getPassword(), userDetailsJwt.getPassword());
				if(matchPassword) {
					if(userDetailsJwt.getJwttoken() == null) {
						String token = createUserToken(userDetailsJwt, jwtRequest);
						return ResponseEntity.ok(new JwtResponse(token, token, jwtTokenUtil().getExpiration(), jwtTokenUtil().getDateExpire()));
					} else {
						if(userDetailsJwt.getExpire() > expire) {
							return ResponseEntity.ok(new JwtResponse(userDetailsJwt.getJwttoken(), userDetailsJwt.getJwttoken(), userDetailsJwt.getExpire(), userDetailsJwt.getDateExpire()));
						} else {
							return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired, please refresh it...");
						}
					}
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied, credentials are invalid...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied, credentials are invalid...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "Check validity from access-token.")
	@PostMapping(value = "/refresh")
	public ResponseEntity<?> createRefreshToken(
			@ApiParam(required = true, value = "Fill the object JwtRequest with jwttoken only")
			@RequestBody JwtRequest jwtRequest)throws Exception {
		try {
			Criteria criteria = Criteria.where("jwttoken").is(jwtRequest.getToken());
			UserDetailsJwt userDetailsJwt = this.validarUsuarioNoSQL(criteria, jwtRequest);
			jwtRequest.setUsername(userDetailsJwt.getUsername());
			
			if(userDetailsJwt != null && userDetailsJwt.get_id() != null) {
				String token = createUserToken(userDetailsJwt, jwtRequest);
				return ResponseEntity.ok(new JwtResponse(token, token, jwtTokenUtil().getExpiration(), jwtTokenUtil().getDateExpire()));
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied, credentials are invalid!");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "To recovery a token use that endpoint. It's necessary a valid username to request data")
	@PostMapping(value = "/recovery")
	public ResponseEntity<?> recoveryAuthenticationToken(
			@ApiParam(required = true, value = "Fill the object JwtRequest with username and password only")
			@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			Criteria criteria = Criteria.where("username").is(jwtRequest.getUsername());
			UserDetailsJwt userDetailsJwt = this.validarUsuarioNoSQL(criteria, jwtRequest);
			if(userDetailsJwt != null && userDetailsJwt.get_id() != null) {
				boolean matchPassword = passwordEncoder.matches(jwtRequest.getPassword(), userDetailsJwt.getPassword());
				if(matchPassword) {
					String token = createUserToken(userDetailsJwt, jwtRequest);
					return ResponseEntity.ok(new JwtResponse(token, token, jwtTokenUtil().getExpiration(), jwtTokenUtil().getDateExpire()));
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied, credentials are invalid...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied, credentials are invalid...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}

	@ApiOperation(value = "To change password. After that, the user needs to request data to get a new token.")
	@PutMapping(value = "/update-password")
	public ResponseEntity<?> updateUser(
			@ApiParam(required = true, value = "Fill the object JwtRequest with username and the new password only")
			@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			Criteria criteria 			  = Criteria.where("username").is(jwtRequest.getUsername());
			UserDetailsJwt userDetailsJwt = this.validarUsuarioNoSQL(criteria, jwtRequest);
			if(userDetailsJwt != null) {
				userDetailsJwt.setPassword(passwordEncoder.encode(jwtRequest.getPassword()));
				userDetailsJwt.setExpire(0l);
				userDetailsJwt.setJwttoken("");
				userDetailsJwt.setDateExpire("");
				this.mongoTemplate.save(userDetailsJwt);
				return ResponseEntity.status(HttpStatus.OK).body("Password has updated successfully...You should recovery your token, the older value that has associated with your user has erased...");
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Credentials are invalid...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "To create a new user. Fill group, username and password")
	@PostMapping(value = "/create-user")
	public ResponseEntity<?> createUser(@ApiParam(required = true, value = "Fill the object JwtRequest with username, password and group only. For while, for group use \"user-roles\"")
										@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			Criteria criteria 			  = Criteria.where("username").is(jwtRequest.getUsername());
			UserDetailsJwt userDetailsJwt = this.validarUsuarioNoSQL(criteria, jwtRequest);
			if(userDetailsJwt == null) {
				UserGroupJwt userGroupJwt = this.mongoTemplate.findOne(new Query(Criteria.where("group").is(jwtRequest.getGroup().toLowerCase())),UserGroupJwt.class);
				if(userGroupJwt != null && userGroupJwt.getGroup() != null) {
					UserDetailsJwt userDetailsJwtCreate = new UserDetailsJwt();
					userDetailsJwtCreate.setUsername(jwtRequest.getUsername());
					userDetailsJwtCreate.setPassword(passwordEncoder.encode(jwtRequest.getPassword()));
					userDetailsJwtCreate.setUserGroupJwt(userGroupJwt);
					this.mongoTemplate.save(userDetailsJwtCreate);
					return ResponseEntity.status(HttpStatus.OK).body("User has created successfully...");
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User group hasn't founded...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username "+jwtRequest.getUsername()+" is not availble, please choice another...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "To crypt a password. Use that endpoint following the specs that has used in project.")
	@PostMapping(value = "/crypt-password")
	public ResponseEntity<?> cryptPassword(
			@ApiParam(required = true, value = "Fill the object JwtRequest with password only")
			@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			String password = "";
    		int count 		= 0;
    		while (count <= 2) {
    			password = count == 0 ? Base64.getEncoder().encodeToString(jwtRequest.getPassword().getBytes())
    								  : Base64.getEncoder().encodeToString(password.getBytes());
    			count++;
    		}
			return ResponseEntity.ok(password);
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "To drop user. Inform only username.")
	@DeleteMapping(value = "/drop-user")
	public ResponseEntity<?> dropUser(
			@ApiParam(required = true, value = "Fill the object JwtRequest with username only")
			@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			Criteria criteria 			  = Criteria.where("username").is(jwtRequest.getUsername());
			UserDetailsJwt userDetailsJwt = this.validarUsuarioNoSQL(criteria, jwtRequest);
			if(userDetailsJwt != null) {
				this.mongoTemplate.remove(userDetailsJwt);
				return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body("User has removed successfully...");
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied, credentials are invalid...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}

	private UserDetailsJwt validarUsuarioNoSQL(Criteria criteria, JwtRequest jwtRequest) {
		UserDetailsJwt userDetailsJwt = this.mongoTemplate.findOne(new Query(criteria), UserDetailsJwt.class);
		return userDetailsJwt;
	}

	private String createUserToken(UserDetailsJwt userDetailsJwt, JwtRequest jwtRequest) throws Exception{
		try {
			this.jwtUserDetailsService().setUser(userDetailsJwt.getUsername());
			this.jwtUserDetailsService().setPassword(userDetailsJwt.getPassword());
			this.jwtUserDetailsService().setTextPlainPass(jwtRequest.getPassword());
		
			final UserDetails userDetails = jwtUserDetailsService().loadUserByUsername(userDetailsJwt.getUsername());
			final String token 			  = jwtTokenUtil().generateToken(userDetails);
			
			userDetailsJwt.setJwttoken(token);
			userDetailsJwt.setDateExpire(jwtTokenUtil().getDateExpire());
			userDetailsJwt.setExpire(jwtTokenUtil().getExpiration());
			
			this.userDetailJwtRepository.save(userDetailsJwt);
			return token;
		} catch (Exception e) {
			throw new Exception (e);
		}
	}

	private JwtUserDetailsService jwtUserDetailsService(){
		if(this.userDetailsService == null){
			this.userDetailsService = JwtUserDetailsService.builder().mongoTemplate(this.mongoTemplate).encoder(this.passwordEncoder).build();
		}
		return this.userDetailsService;
	}

	private JwtTokenUtil jwtTokenUtil(){
		if(this.jwtTokenUtil == null){
			this.jwtTokenUtil = JwtTokenUtil.builder().build();
		}
		return  jwtTokenUtil;
	}
}
