package br.com.cadastroit.services.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
@Table(name = "SOLICITACAODOCARQUIVO_ANEXADO")
@SequenceGenerator(name = "SOLICITACAODOCARQUIVO_ANEXADO_SEQ", sequenceName = "SOLICITACAODOCARQUIVO_ANEXADO_SEQ", allocationSize = 1)
public class SolicitacaoDocumentoArquivoAnexado implements Serializable {

	private static final long serialVersionUID = 4990962406710887217L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOLICITACAODOCARQUIVO_ANEXADOSEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "NOME_ORIGINAL")
	private String nomeOriginal;

	@Column(name = "NOME_SALVO")
	private String nomeSalvo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOLICITACAODOCUMENTO_ID")
	private SolicitacaoDocumento solicitacaoDocumento;

}
