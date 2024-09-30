package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;

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
public class ItemNfsDto {

	private Long id;

	@NotNull(message = "Campo nfServ sem preenchimento")
	private NfServDto nfServ;

	@NotNull(message = "Campo codItem sem preenchimento")
	private Integer nroItem;

	@NotNull(message = "Campo codItem sem preenchimento")
	@Size(min = 1, max = 60, message = "Campo codItem nao pode ultrapassar o total de 60 caracteres.")
	private String codItem;

	@Size(min = 1, max = 2000, message = "Campo descricao nao pode ultrapassar o total de 2000 caracteres.")
	private String descricao;

	private String cnae;

	@NotNull(message = "Campo cdListaServ sem preenchimento")
	@Size(min = 1, max = 4, message = "Campo cdListaServ nao pode ultrapassar o total de 4 caracteres.")
	private String cdListaServ;

	@Size(min = 1, max = 20, message = "Campo codTribMunicipio nao pode ultrapassar o total de 20 caracteres.")
	private String codTribMunicipio;

	@Size(min = 1, max = 9, message = "Campo codigoNbs nao pode ultrapassar o total de 9 caracteres.")
	private String codigoNbs;

	@NotNull(message = "Campo dmLocIncidIss sem preenchimento")
	private Integer dmLocIncidIss;

	@Size(min = 1, max = 7, message = "Campo cidadeIbge nao pode ultrapassar o total de 7 caracteres.")
	private String cidadeIbge;

	@Size(min = 1, max = 4, message = "Campo codigoPais nao pode ultrapassar o total de 4 caracteres.")
	private String codigoPais;

	@Size(min = 1, max = 3, message = "Campo cdCidadeBeneficFiscal nao pode ultrapassar o total de 3 caracteres.")
	private String cdCidadeBeneficFiscal;

	@NotNull(message = "Campo vlServico sem preenchimento")
	private BigDecimal vlServico;
	
	private BigDecimal vlDescIncondicionado;

	private BigDecimal vlDescCondicionado;

	private BigDecimal vlDeducao;

	private BigDecimal vlOutraRetencao;

	@Size(min = 1, max = 4000, message = "Campo conteudo_inf_adic nao pode ultrapassar o total de 4000 caracteres.")
	private String conteudoInfAdic;

	private BigDecimal vlPisRet;

	private BigDecimal vlCofinsRet;

	private BigDecimal vlInssRet;

	private BigDecimal vlIrRet;

	private BigDecimal vlCsllRet;

	private BigDecimal vlTotTributos;

	private BigDecimal vlBaseCalcIss;

	private BigDecimal aliquotaIss;

	private BigDecimal vlIss;

	private BigDecimal vlIssRetido;
}
