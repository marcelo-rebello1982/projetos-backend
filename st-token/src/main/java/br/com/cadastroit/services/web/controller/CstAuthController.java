package br.com.cadastroit.services.web.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cadastroit.services.config.domain.Authority;
import br.com.cadastroit.services.config.domain.AuthorityUser;
import br.com.cadastroit.services.config.domain.User;
import br.com.cadastroit.services.config.security.TokenCriteria;
import br.com.cadastroit.services.web.model.AuthDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin(value = {"*"})
@RequestMapping(path = "auth/usuario")
@RestController
public class CstAuthController {

	private final MongoTemplate mongoTemplate;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public CstAuthController(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder){
		this.mongoTemplate  = mongoTemplate;
		this.passwordEncoder= passwordEncoder;
	}

	@ApiOperation(value = "Crie um novo usuario")
	@PostMapping(value = "/criar")
	public ResponseEntity<?> createUser(@ApiParam(required = true, value = "Criando novos usuarios. As roles sao: ADMIN, CUSTOMER ou USER")
										@RequestBody AuthDTO authDTO) throws Exception {
		try {
			User user = this.findByUsername(authDTO.getUsername());
			if(user == null) {
				Authority authority = this.mongoTemplate.findOne(new Query(Criteria.where("role").is("ROLE_"+authDTO.getRole().toUpperCase())),Authority.class);
				if(authority != null) {
					user = User.builder().username(authDTO.getUsername())
							.accountNonExpired(false)
							.accountNonLocked(false)
							.credentialNonExpired(false)
							.enabled(false)
							.uuid(UUID.randomUUID())
							.password(this.passwordEncoder.encode(authDTO.getPassword()))
							.expireAt(0l)
							.dateExpire(null)
							.build();
					user = this.mongoTemplate.save(user);
					if(user.getId() != null) {
						AuthorityUser authorityUser = AuthorityUser.builder().authority(authority)
								.user(user)
								.uuid(UUID.randomUUID())
								.build();
						this.mongoTemplate.save(authorityUser);
						return ResponseEntity.status(HttpStatus.OK).body("Usuario registrado...");
					}else{
						return ResponseEntity.status(HttpStatus.OK).body("Falha no registro do usuario...");
					}
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Regra nao encontrada %s...", authDTO.getRole()));
				}
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Nome de usuario nao disponivel %s...",authDTO.getUsername()));
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "Solicitando token (Validade 24 horas)")
	@PostMapping(value = "/token")
	public ResponseEntity<?> createAuthenticationToken(
			@ApiParam(required = true, value = "Informe o usuario e a senha")
			@RequestBody AuthDTO authDTO) throws Exception {
		try {
			long expire  = System.currentTimeMillis();
			User userDTO = this.findByUsername(authDTO.getUsername());//Validando a existencia do usuario
			if(userDTO != null) {
				boolean matchPassword = passwordEncoder.matches(authDTO.getPassword(), userDTO.getPassword());
				if(matchPassword) {
					if(userDTO.getToken() == null) {
						userDTO = createUserToken(userDTO);
						return ResponseEntity.ok(userDTO.getToken());
					} else {
						if(authDTO.getExpire() > expire) {
							return ResponseEntity.ok(authDTO.getToken());
						} else {
							return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado, acesse o path '/atualizar/token'...");
						}
					}
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado, usuario ou senha invalidos...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado, usuario ou senha invalidos...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "Atualizando token. Se token informado e valido, um novo token e gerado e devolvido ao usuario")
	@PostMapping(value = "/atualizar/token")
	public ResponseEntity<?> createRefreshToken(
			@ApiParam(required = true, value = "Preencha o AuthDTO apenas com o token")
			@RequestBody AuthDTO authDTO)throws Exception {
		try {
			User user 		  = this.findByToken(authDTO.getToken());
			authDTO.setUsername(user.getUsername());
			if(user != null && user.getId() != null) {
				user 	= createUserToken(user);
				authDTO = this.createAuthDTO(user, authDTO);
				return ResponseEntity.ok(authDTO);
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado...dados invalidos!");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "Para recuperacao de token. Path para recuperacao de token por usuario e senha")
	@PostMapping(value = "/recuperar/token")
	public ResponseEntity<?> recoveryAuthenticationToken(
			@ApiParam(required = true, value = "Preencha o objeto AuthDTO com usuario e senha")
			@RequestBody AuthDTO authDTO) throws Exception {
		try {
			User user = this.findByUsername(authDTO.getUsername());
			if(user != null && user.getId() != null) {
				boolean matchPassword = passwordEncoder.matches(authDTO.getPassword(), user.getPassword());
				if(matchPassword) {
					user 	= createUserToken(user);
					authDTO = this.createAuthDTO(user, authDTO);
					return ResponseEntity.ok(authDTO);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado, usuario ou senha invalido...");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado, usuario ou senha invalido...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}

	@ApiOperation(value = "Para troca de senha.")
	@PutMapping(value = "/atualizar/senha")
	public ResponseEntity<?> updateUser(
			@ApiParam(required = true, value = "Preencha o objeto AuthDTO com usuario e senha")
			@RequestBody AuthDTO authDTO) throws Exception {
		try {
			User user = this.findByUsername(authDTO.getUsername());
			if(user != null) {
				user.setPassword(passwordEncoder.encode(authDTO.getPassword()));
				user.setEnabled(false);
				user.setToken("");
				user.setDateExpire("");
				this.mongoTemplate.save(user);
				return ResponseEntity.status(HttpStatus.OK).body("Password atualizado...Recupere o token acessando o path '/recuperar/token'");
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario "+authDTO.getUsername()+" nao foi encontrado...");
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "Criptografando password")
	@PostMapping(value = "/crypt/password")
	public ResponseEntity<?> cryptPassword(
			@ApiParam(required = true, value = "Informe apenas o password")
			@RequestBody AuthDTO authDTO) throws Exception {
		try {
			String password = "";
    		int count = 0;
    		while (count <= 2) {
    			password = count == 0 ? Base64.getEncoder().encodeToString(authDTO.getPassword().getBytes())
    								  : Base64.getEncoder().encodeToString(password.getBytes());
    			count++;
    		}
			return ResponseEntity.ok(password);
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	@ApiOperation(value = "Informe o nome de usuario a ser removido.")
	@DeleteMapping(value = "/remover/usuario")
	public ResponseEntity<?> dropUser(
			@ApiParam(required = true, value = "Informe apenas o nome de usuario ('username')")
			@RequestBody AuthDTO authDTO) throws Exception {
		try {
			User user = this.findByUsername(authDTO.getUsername());
			if(user != null){
				List<AuthorityUser> collection = this.mongoTemplate.findAllAndRemove(new Query(Criteria.where("user").is(user)),AuthorityUser.class);
				this.mongoTemplate.remove(user);

				String regrasRevogadas = "";
				if(collection.size() > 0){
					regrasRevogadas = String.format(" Total de regras revogadas: %s", collection.size());
				}
				return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body("Usuario removido..."+regrasRevogadas);
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("User "+authDTO.getUsername()+" not found...", authDTO.getUsername()));
			}
		}catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
	}
	
	private User findByUsername(String username) {
		Optional<User> user = Optional.ofNullable(this.mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), User.class));
		if(user.isPresent()) {
			return user.get();
		}
		return null;
	}

	private User findByToken(String token) {
		Optional<User> user = Optional.ofNullable(this.mongoTemplate.findOne(new Query(Criteria.where("token").is(token)), User.class));
		if(user.isPresent()) {
			return user.get();
		}
		return null;
	}
	
	private User createUserToken(User user) throws Exception{
		try {
			TokenCriteria tokenCriteria = TokenCriteria.builder().build();
			user = this.findByUsername(user.getUsername());
			if(user != null) {
				final String token = tokenCriteria.generateToken(user);

				user.setToken(token);
				user.setDateExpire(tokenCriteria.getDateExpire());
				user.setExpireAt(tokenCriteria.getExpiration());
				user.setEnabled(true);

				user = this.mongoTemplate.save(user);
				return user;
			}else{
				throw new Exception(String.format("Credenciais invalidas..."));
			}
		} catch (Exception e) {
			throw new Exception (e);
		}
	}

	private AuthDTO createAuthDTO(User user, AuthDTO authDTO){
		authDTO.setToken(user.getToken());
		authDTO.setExpire(user.getExpireAt());
		authDTO.setDateExpire(user.getDateExpire());
		return authDTO;
	}
}
