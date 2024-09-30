package br.com.complianceit.services.commons.flux;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestController {

	 protected String token;
	 protected String multOrgCd;
	 protected String hashCode;
	 protected String base64;
	 protected String objIntegrCd;
	 protected String tipoObjIntegrCd;
	 protected String typeOf;
	 protected String exchange;
	 
	 public RequestController withToken(String token) {
		 this.token = token;
		 return this;
	 }
	
	 public RequestController withMultOrgCd(String multorgCd) {
		 this.multOrgCd = multorgCd;
		 return this;
	 }
	 
	 public RequestController withHashCode(String hashCode) {
		 this.hashCode = hashCode;
		 return this;
	 }
	 
	 public RequestController withBase64(String base64) {
		 this.base64 = base64;
		 return this;
	 }
	 
	 public RequestController withObjIntegrCd(String objIntegrCd) {
		 this.objIntegrCd = objIntegrCd;
		 return this;
	 }
	 
	 public RequestController withTipoObjIntegrCd(String tipoObjIntegrCd) {
		 this.tipoObjIntegrCd = tipoObjIntegrCd;
		 return this;
	 }
	 
	 public RequestController withTypeOf(String typeOf) {
		 this.typeOf = typeOf;
		 return this;
	 }
	 
	 public RequestController withExchange(String exchange) {
		 this.exchange = exchange;
		 return this;
	 }
}
