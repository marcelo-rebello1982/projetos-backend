package br.com.cadastroit.services.model.commons.api;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.exceptions.CommonsApiException;

public class CommonsModelApi {

	private Credential credential;
	
	public String formatterResultNumber(Double value, String mask) {
		double inSeconds= value/1000;
		NumberFormat formatter = new DecimalFormat(mask,DecimalFormatSymbols.getInstance(new Locale("en", "US")));
		String valueProcess = formatter.format(inSeconds);
		return valueProcess;
	}

	public void logTime(long begin, long end, Logger LOGGER) {
		Long timeExecute = end-begin;
		String seconds	 = this.formatterResultNumber(timeExecute.doubleValue(), "#0.000");
		LOGGER.info("Time process in milliseconds = "+timeExecute+", in seconds = "+seconds);
	}
	
	public String formatDate(Timestamp date, String... format) {
		SimpleDateFormat sdf = null;
		if(format != null && format.length == 1) {
			sdf = new SimpleDateFormat(format[0]);
		}else {
			sdf = new SimpleDateFormat("dd/MM/yyyy");
		}
		return sdf.format(new Date(date.getTime()));
	}
	
	public String toDateTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (date != null) {
			return sdf.format(date);
		}
		return "";
	}
	
	public String toDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (date != null) {
			return sdf.format(date);
		}
		return "";
	}
	
	public String formatDateHour(Timestamp date, String... format) {
		SimpleDateFormat sdf = null;
		if(format != null && format.length == 1) {
			sdf = new SimpleDateFormat(format[0]);
		}else {
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		}
		return sdf.format(new Date(date.getTime()));
	}
	
	public Credential createCredential(String values) throws CommonsApiException {
		try{
			if(values != null) {
				byte[] decoder = Base64.getDecoder().decode(values.getBytes());
				String vDecode = new String(decoder);
				
				String[] splitValues = vDecode.split("&");
				BigDecimal codigo	 = new BigDecimal(splitValues[0].replace("\n", ""));
				String hash		 	 = splitValues[1].replace("\n", "");
				String usuarioId	 = splitValues[2].replace("\n", "");
				Long empresaId		 = new Long(splitValues[3].replace("\n", ""));
				
				Credential credential = new Credential.CredentialBuilder()
													  .setCodigo(codigo)
													  .setHash(hash)
													  .setUsuarioId(usuarioId)
													  .setEmpresaId(empresaId).build();
				this.credential = credential;
				return credential;
			} else {
				throw new CommonsApiException("Credentials are invalid...");
			}
		}catch(CommonsApiException ex) {
			throw new CommonsApiException("Credentials are invalid...");
		}catch( Exception ex) {
			throw new CommonsApiException("Credentials are invalid...");
		}
	}
	
	public String createToken(String values) throws CommonsApiException {
		try{
			if(values != null) {
				byte[] decoder = Base64.getEncoder().encode(values.getBytes());
				String vEncode = new String(decoder);
				return vEncode;
			} else {
				throw new CommonsApiException("Credentials are invalid...");
			}
		}catch(CommonsApiException ex) {
			throw new CommonsApiException("Credentials are invalid...");
		}catch( Exception ex) {
			throw new CommonsApiException("Credentials are invalid...");
		}
	}
	
	private Predicate<Entry<String,Long>> clausureCacheControl(Credential credential) {
		String key = credential.getCodigo()+credential.getHash()+credential.getEmpresaId();
		Long empresaId = credential.getEmpresaId();
		Predicate<Entry<String,Long>> clausure = new Predicate<Entry<String,Long>>() {
			public boolean test(Entry<String, Long> values) {
				return values.getKey().equals(key) && values.getValue().equals(empresaId);
			}
		};
		return clausure;
	}
	
	private void createCacheCredential(Credential credential, Map<String, Long> cacheControl) {
		if(cacheControl.isEmpty()) {
			cacheControl.put(credential.getCodigo()+credential.getHash()+credential.getEmpresaId(), credential.getEmpresaId());
		}else {
			Predicate<Entry<String,Long>> clausure = this.clausureCacheControl(credential);
			if(!cacheControl.entrySet().stream().filter(clausure).findAny().isPresent()) {
				cacheControl.put(credential.getCodigo()+credential.getHash()+credential.getEmpresaId(), credential.getEmpresaId());
			}
		}
	}
	
	private boolean validateKey(Long empresaId, Credential credential, Map<String, Long> cacheControl) throws CommonsApiException {
		if(!empresaId.equals(credential.getEmpresaId())) {
			throw new CommonsApiException("Error, customer id doesn't match with the store customer database, check your app...");
		}
		this.createCacheCredential(credential, cacheControl);
		return true;
	}
	
	public Long validateCredential(String credentials, CommonsJpaAPI commonsJpaAPI, EntityManager entityManager, Map<String, Long> cacheControl,  Logger LOGGER) throws CommonsApiException {
		try {
			Credential credential = this.createCredential(credentials);
			Long key			  = 0l;
			if(!cacheControl.isEmpty()) {
				key = activateCacheControl(credential, cacheControl, LOGGER);
				if(!key.equals(0l)) {
					return key;
				}
			}
			Empresa empresa = commonsJpaAPI.validateInfoMultOrg(credential, entityManager);
			if(this.validateKey(empresa.getId(), credential, cacheControl)) {
				key = empresa.getId();
			}
			return key;
		}catch(Exception ex) {
			throw new CommonsApiException(ex.getMessage(),ex);
		}
	}
	
	private Long activateCacheControl(Credential credential, Map<String, Long> cacheControl, Logger LOGGER) {
		Long empresaID = 0l;
		try {
			Predicate<Entry<String,Long>> clausure = this.clausureCacheControl(credential);
			empresaID = cacheControl.entrySet().stream().filter(clausure).findAny().get().getValue();
			LOGGER.info("Get Key from cacheControl...");
		}catch(Exception ex) {
			LOGGER.info("Key is not present on cacheControl...");
		}
		return empresaID;
	}

	public Credential getCredential() {
		return credential;
	}
}
