package br.com.cadastroit.services.mongodb.domain;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(value="relatoriodesifdata")
public class CallRelatorioDesifApi {
	
	private String ID;
	private Integer STATUS; //0-Em PROCESSAMENTO, 1-Concluido, 2-Erro na chamada;
	private Long EMPRESA_ID;
	private String DESCRIPTION;
	private String MESSAGES;
	private String URLS3;
	private String TYPEARCHIVE; // CSV, ZIP , XLSX
	private Long TIMEOUT;
	private String NAMEARCHIVE;
	private String CREATIONDATE;
	private String NROPROTOCOLO; //nroProtocolo;
	private Map<String, String>  REFERENCE;
		
	public CallRelatorioDesifApi(){}
	
	public CallRelatorioDesifApi(
			String ID,
			Integer STATUS,
			String TYPE,
			Long EMPRESA_ID,
			String DESCRIPTION,
			String MESSAGES,
			String URLS3,
			String TYPEARCHIVE,
			Long TIMEOUT,
			String NAMEARCHIVE,
			String CREATIONDATE,
			String NROPROTOCOLO,
			Map<String, String>  REFERENCE) {
		this.ID = ID;
		this.STATUS = STATUS;
		this.EMPRESA_ID = EMPRESA_ID;
		this.DESCRIPTION = DESCRIPTION;
		this.MESSAGES = MESSAGES;
		this.URLS3 = URLS3;
		this.TYPEARCHIVE = TYPEARCHIVE;
		this.TIMEOUT = TIMEOUT;
		this.NAMEARCHIVE = NAMEARCHIVE;
		this.CREATIONDATE = CREATIONDATE;
		this.NROPROTOCOLO = NROPROTOCOLO;
		this.REFERENCE = REFERENCE;
	}
	
	public Map<String, Object> toMap(CallRelatorioDesifApi callRel){
		Map<String, Object> map = new HashMap<>();
		map.put("ID", callRel.getId());
		map.put("STATUS", callRel.getSTATUS());
		map.put("EMPRESA_ID", callRel.getEMPRESA_ID());
		map.put("DESCRIPTION", callRel.getDESCRIPTION());
		map.put("MESSAGES", callRel.getMESSAGES());
		map.put("URLS3", callRel.getURLS3());
		map.put("TYPEARCHIVE", callRel.getTYPEARCHIVE());	
		map.put("NAMEARCHIVE", callRel.getNAMEARCHIVE());
		map.put("CREATIONDATE", callRel.getCREATIONDATE());
		map.put("NROPROTOCOLO", callRel.getNROPROTOCOLO());
		map.put("REFERENCE", callRel.getREFERENCE());
		return map;
	}
	
	public String getId() {
		return ID;
	}

	public void setId(String idRelDesif) {
		ID = idRelDesif;
	}

	public Integer getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(Integer sTATUS) {
		STATUS = sTATUS;
	}

	public Long getEMPRESA_ID() {
		return EMPRESA_ID;
	}

	public void setEMPRESA_ID(Long eMPRESA_ID) {
		EMPRESA_ID = eMPRESA_ID;
	}

	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}

	public String getMESSAGES() {
		return MESSAGES;
	}

	public void setMESSAGES(String mESSAGES) {
		MESSAGES = mESSAGES;
	}
	
	public String getURLS3() {
		return URLS3;
	}

	public void setURLS3(String uRLS3) {
		URLS3 = uRLS3;
	}

	public String getTYPEARCHIVE() {
		return TYPEARCHIVE;
	}

	public void setTYPEARCHIVE(String tYPEARCHIVE) {
		TYPEARCHIVE = tYPEARCHIVE;
	}

	public Long getTIMEOUT() {
		return TIMEOUT;
	}

	public void setTIMEOUT(Long tIMEOUT) {
		TIMEOUT = tIMEOUT;
	}

	public String getNAMEARCHIVE() {
		return NAMEARCHIVE;
	}

	public void setNAMEARCHIVE(String nAMEARCHIVE) {
		NAMEARCHIVE = nAMEARCHIVE;
	}

	public String getCREATIONDATE() {
		return CREATIONDATE;
	}

	public void setCREATIONDATE(String cREATIONDATE) {
		CREATIONDATE = cREATIONDATE;
	}
	
	public String getNROPROTOCOLO() {
		return NROPROTOCOLO;
	}

	public void setNROPROTOCOLO(String nROPROTOCOLO) {
		NROPROTOCOLO = nROPROTOCOLO;
	}

	public Map<String, String> getREFERENCE() {
		return REFERENCE;
	}

	public void setREFERENCE(Map<String, String> rEFERENCE) {
		REFERENCE = rEFERENCE;
	}
}