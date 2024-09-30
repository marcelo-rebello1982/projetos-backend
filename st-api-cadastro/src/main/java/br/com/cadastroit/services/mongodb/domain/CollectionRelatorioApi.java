package br.com.cadastroit.services.mongodb.domain;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Document(value = "relatoriocontasdata")
public class CollectionRelatorioApi {

	private String ID;
	private Integer STATUS; // 0-Em PROCESSAMENTO, 1-Concluido, 2-Erro na chamada;
	private Long PESSOA_ID;
	private String DESCRIPTION;
	private String MESSAGES;
	private String URLS3;
	private String TYPEARCHIVE; // CSV, ZIP , XLSX
	private Long TIMEOUT;
	private String NAMEARCHIVE;
	private String CREATIONDATE;
	private String NROPROTOCOLO; // nroProtocolo;
	private Map<String, String> REFERENCE;

	public Map<String, Object> toMap(CollectionRelatorioApi callRel) {

		Map<String, Object> map = new HashMap<>();
		map.put("ID", callRel.getID());
		map.put("STATUS", callRel.getSTATUS());
		map.put("PESSOA_ID", callRel.getPESSOA_ID());
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
}