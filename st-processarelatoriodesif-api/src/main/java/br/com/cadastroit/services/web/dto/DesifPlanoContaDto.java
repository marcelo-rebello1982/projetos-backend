package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesifPlanoContaDto {

	private String id;

	private EmpresaDto empresa;

	private String codCta;

	private BigDecimal desMista;

	private String nome;

	private String descrCta;

	private String codCtaSup;

	private DesifCadPcCosifDto desifCadPcCosIf;

	private DesifCadCodTribMunDto desifCadCodTribMun;

	private Integer contaReduzida;

	private Integer[] contaReduzidaValues;

	@JsonProperty("status")
	private String dmSituacao;

	private Map<String, String> requestParams;

	private int page;

	private int lenght;

	private Long nroProtocolo;

}