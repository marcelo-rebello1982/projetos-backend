package br.com.cadastroit.services.model.commons.api;

import java.math.BigDecimal;
import java.util.Base64;

public class Credential {

	private BigDecimal codigo;
	private String hash;
	private String usuarioId;
	private Long empresaId;
	
	public static class CredentialBuilder{
		private BigDecimal codigo;
		private String hash;
		private String usuarioId;
		private Long empresaId;
		
		public Credential build() {
			Credential credential = new Credential();
			credential.setCodigo(this.codigo);
			credential.setHash(this.hash);
			credential.setUsuarioId(this.usuarioId);
			credential.setEmpresaId(this.empresaId);
			return credential;
		}
		
		public Credential buildDecrypt(String token) {
			byte[] decoder 	= Base64.getDecoder().decode(token);
			String decrypt 	= new String(decoder);
			String[] values = decrypt.split("&");
			
			Credential credential = new Credential();
			credential.setCodigo(new BigDecimal(values[0]));
			credential.setHash(values[1]);
			credential.setUsuarioId(values[2]);
			credential.setEmpresaId(new Long(values[3]));
			
			return credential;
		}
		
		public CredentialBuilder setCodigo(BigDecimal codigo) {
			this.codigo = codigo;
			return this;
		}
		public CredentialBuilder setHash(String hash) {
			this.hash = hash;
			return this;
		}
		public CredentialBuilder setUsuarioId(String usuarioId) {
			this.usuarioId = usuarioId;
			return this;
		}
		public CredentialBuilder setEmpresaId(Long empresaId) {
			this.empresaId = empresaId;
			return this;
		}
	}

	public BigDecimal getCodigo() {
		return codigo;
	}

	public void setCodigo(BigDecimal codigo) {
		this.codigo = codigo;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Long getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}
}
