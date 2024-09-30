package br.com.complianceit.services.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Data
public class Auth {

	private String username;
	private String password;
	private String group;
	private String token;
	private String jwttoken;
	private Long expire;
	private String base64;
	
	public Auth() {}
	
}
