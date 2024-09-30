package br.com.cadastroit.services.controllers.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_detail_jwt")
public class UserDetailsJwt {
	
	@Id
	private ObjectId _id;
	
	private String username;
	private String password;
	private String base64;
	private String jwttoken;
	private Long expire;
	private String dateExpire;
	
	@DBRef
	private UserGroupJwt userGroupJwt;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJwttoken() {
		return jwttoken;
	}

	public void setJwttoken(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public String getDateExpire() {
		return dateExpire;
	}

	public void setDateExpire(String dateExpire) {
		this.dateExpire = dateExpire;
	}

	public UserGroupJwt getUserGroupJwt() {
		return userGroupJwt;
	}

	public void setUserGroupJwt(UserGroupJwt userGroupJwt) {
		this.userGroupJwt = userGroupJwt;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}
}
