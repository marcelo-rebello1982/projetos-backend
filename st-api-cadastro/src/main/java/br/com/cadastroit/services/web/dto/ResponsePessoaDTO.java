package br.com.cadastroit.services.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePessoaDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String     cnpjEmpresa;
	private String     codItem;
	private String     descrItem;
	private Long       aberturaFciId;
	private Long       aberturaFciArqId;
	private Integer    sequencia;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date       dtIni;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date       dtFin;
	private BigDecimal vlrSaida;
	private BigDecimal vlrImportada;
	private BigDecimal coefImport;
	private String     nroFci;
	

}
