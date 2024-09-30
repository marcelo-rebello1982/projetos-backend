package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import br.com.cadastroit.services.api.enums.SolicitacaoDocumentoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "SOLICITACAODOCUMENTO")
@SequenceGenerator(name = "SOLICITACAODOCUMENTO_SEQ", sequenceName = "SOLICITACAODOCUMENTO_SEQ", allocationSize = 1)
public class SolicitacaoDocumento implements Serializable {

	private static final long serialVersionUID = 99131933725191754L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOLICITACAODOCUMENTO_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "VALOR_PAGAMENTO", precision = 2)
	private BigDecimal valorPagamento;

	@Column(name = "EXCLUIDA")
	private boolean excluida;
	
	@Column(name = "DESCRICAO_PAGAMENTO")
	private String descricaoPagamento;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PESSOA_ID", referencedColumnName = "ID")
	private Pessoa pessoa;
	
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "VENDA_ID", referencedColumnName = "ID")
	private Venda venda;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SolicitacaoDocumentoStatus status = SolicitacaoDocumentoStatus.EM_AGUARDO;

	@OneToMany(mappedBy = "solicitacaoDocumento", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<SolicitacaoDocumentoArquivoAnexado> arquivosAnexados;

	public List<SolicitacaoDocumentoArquivoAnexado> getArquivosAnexados() {

		if (arquivosAnexados == null)
			arquivosAnexados = new ArrayList<>();

		return arquivosAnexados;
	}

	public void setArquivosAnexados(List<SolicitacaoDocumentoArquivoAnexado> arquivosAnexados) {

		this.getArquivosAnexados().clear();

		if (CollectionUtils.isNotEmpty(arquivosAnexados)) {

			this.arquivosAnexados.addAll(arquivosAnexados);

			for (SolicitacaoDocumentoArquivoAnexado arquivo : arquivosAnexados) {

				arquivo.setSolicitacaoDocumento(this);

			}
		}
	}

	public void addArquivosAnexados(List<SolicitacaoDocumentoArquivoAnexado> arquivosAnexar) {

		if (CollectionUtils.isEmpty(arquivosAnexar))
			return;

		for (SolicitacaoDocumentoArquivoAnexado arquivo : arquivosAnexar)
			arquivo.setSolicitacaoDocumento(this);

		this.getArquivosAnexados().addAll(arquivosAnexar);
	}
}