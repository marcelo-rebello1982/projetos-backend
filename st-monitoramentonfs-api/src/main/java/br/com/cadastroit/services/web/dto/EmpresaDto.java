package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDto  {

	private Long id;
	
	@NotNull(message = "Campo multorgCd sem preenchimento")
	@Size(min = 1, max = 10, message = "Campo multorgCd nao pode ultrapassar o total de 10 caracteres.")
	private String multorgCd;

	@NotNull(message = "Campo cnpj sem preenchimento")
	@Size(min = 1, max = 14, message = "Campo cnpj nao pode ultrapassar o total de 14 caracteres.")
	private String cnpj;

	@NotNull(message = "Campo im sem preenchimento")
	@Size(min = 1, max = 15, message = "Campo im nao pode ultrapassar o total de 15 caracteres.")
	private String im;

	@NotNull(message = "Campo nome sem preenchimento")
	@Size(min = 1, max = 120, message = "Campo nome nao pode ultrapassar o total de 120 caracteres.")
	private String nome;

	@Size(min = 1, max = 120, message = "Campo fantasia nao pode ultrapassar o total de 120 caracteres.")
	private String fantasia;

	@NotNull(message = "Campo lograd sem preenchimento")
	@Size(min = 1, max = 60, message = "Campo lograd nao pode ultrapassar o total de 60 caracteres.")
	private String lograd;

	@NotNull(message = "Campo nro sem preenchimento")
	@Size(min = 1, max = 60, message = "Campo nro nao pode ultrapassar o total de 60 caracteres.")
	private String nro;

	@Size(min = 1, max = 60, message = "Campo compl nao pode ultrapassar o total de 60 caracteres.")
	private String compl;

	@NotNull(message = "Campo bairro sem preenchimento")
	@Size(min = 1, max = 60, message = "Campo bairro nao pode ultrapassar o total de 60 caracteres.")
	private String bairro;

	@Size(min = 1, max = 7, message = "Campo ibgeCidade nao pode ultrapassar o total de 7 caracteres.")
	private String ibgeCidade;
	
	//usado apenas nos filros
	private String[] ibgeCidadeValues;


	@NotNull(message = "Campo descrCidade sem preenchimento")
	@Size(min = 1, max = 60, message = "Campo descrCidade nao pode ultrapassar o total de 60 caracteres.")
	private String descrCidade;

	@NotNull(message = "Campo uf sem preenchimento")
	@Size(min = 1, max = 2, message = "Campo uf nao pode ultrapassar o total de 2 caracteres.")
	private String uf;

	@NotNull(message = "Campo uf sem preenchimento")
	@Size(min = 1, max = 8, message = "Campo cep nao pode ultrapassar o total de 8 caracteres.")
	private String cep;

	private String fone;

	@NotNull(message = "Campo email sem preenchimento")
	@Size(min = 1, max = 1000, message = "Campo email nao pode ultrapassar o total de 1000 caracteres.")
	private String email;

	@NotNull(message = "Campo dmSituacao sem preenchimento")
	private Integer dmSituacao;

	@NotNull(message = "Campo dmTpAmb sem preenchimento")
	private Integer dmTpAmb;

	@Size(min = 1, max = 1000, message = "Campo pathLogotipo nao pode ultrapassar o total de 1000 caracteres.")
	private String pathLogotipo;

	@Size(min = 1, max = 1000, message = "Campo pathCertificado nao pode ultrapassar o total de 1000 caracteres.")
	private String pathCertificado;

	private String senhaCertificado;

	private Date validadeCertificado;
	
	@Size(min = 1, max = 1000, message = "Campo nomeImpressora nao pode ultrapassar o total de 1000 caracteres.")
	private String nomeImpressora;
	
	private Integer dmImprAuto;
	
	@NotNull(message = "Campo maxQtdNfLote sem preenchimento")
	private BigDecimal maxQtdNfLote;
	
	@NotNull(message = "Campo nroTentativaComunic sem preenchimento")
	private BigDecimal nroTentativaComunic;
	
	@NotNull(message = "Campo dmAjustaTotInf sem preenchimento")
	private Integer dmAjustaTotInf;
	
	@Size(min = 1, max = 100, message = "Campo usuarioNfse nao pode ultrapassar o total de 100 caracteres.")
	private String usuarioNfse;
	
	@Size(min = 1, max = 100, message = "Campo senhaNfse nao pode ultrapassar o total de 100 caracteres.")
	private String senhaNfse;
	
	@NotNull(message = "Campo dmGeraTotTrib sem preenchimento")
	private Integer dmGeraTotTrib;
	
	private Integer emailTemplateId;
}
