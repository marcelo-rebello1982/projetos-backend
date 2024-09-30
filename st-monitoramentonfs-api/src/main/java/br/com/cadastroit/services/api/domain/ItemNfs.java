package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ITEM_NFS")
public class ItemNfs implements Serializable {

	private static final long serialVersionUID = -4935270527977972837L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "NFSERV_ID")
	private NfServ nfServ;

	@Column(name = "NRO_ITEM", nullable = false)
	private Integer nroItem;

	@Column(name = "COD_ITEM", nullable = false)
	private String codItem;

	@Column(name = "DESCRICAO", nullable = false)
	private String descricao;

	@Column(name = "CNAE")
	private String cnae;

	@Column(name = "CD_LISTA_SERV", nullable = false)
	private String cdListaServ;

	@Column(name = "COD_TRIB_MUNICIPIO")
	private String codTribMunicipio;

	@Column(name = "CODIGO_NBS")
	private String codigoNbs;

	@Column(name = "DM_LOC_INCID_ISS", nullable = false)
	private Integer dmLocIncidIss;

	@Column(name = "CIDADE_IBGE")
	private String cidadeIbge;

	@Column(name = "CODIGO_PAIS", nullable = false)
	private String codigoPais;

	@Column(name = "CD_CIDADE_BENEFIC_FISCAL")
	private String cdCidadeBeneficFiscal;

	@Column(name = "VL_SERVICO", nullable = false)
	private BigDecimal vlServico;
	
	@Column(name = "VL_DESC_INCONDICIONADO")
	private BigDecimal vlDescIncondicionado;

	@Column(name = "VL_DESC_CONDICIONADO")
	private BigDecimal vlDescCondicionado;

	@Column(name = "VL_DEDUCAO")
	private BigDecimal vlDeducao;

	@Column(name = "VL_OUTRA_RETENCAO")
	private BigDecimal vlOutraRetencao;

	@Column(name = "CONTEUDO_INF_ADIC")
	private String conteudoInfAdic;

	@Column(name = "VL_PIS_RET")
	private BigDecimal vlPisRet;

	@Column(name = "VL_COFINS_RET")
	private BigDecimal vlCofinsRet;

	@Column(name = "VL_INSS_RET")
	private BigDecimal vlInssRet;

	@Column(name = "VL_IR_RET")
	private BigDecimal vlIrRet;

	@Column(name = "VL_CSLL_RET")
	private BigDecimal vlCsllRet;

	@Column(name = "VL_TOT_TRIBUTOS")
	private BigDecimal vlTotTributos;

	@Column(name = "VL_BASE_CALC_ISS")
	private BigDecimal vlBaseCalcIss;

	@Column(name = "ALIQUOTA_ISS")
	private BigDecimal aliquotaIss;

	@Column(name = "VL_ISS")
	private BigDecimal vlIss;

	@Column(name = "VL_ISS_RETIDO")
	private BigDecimal vlIssRetido;
}
