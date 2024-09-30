package br.com.cadastroit.services.api.domain;
import java.io.Serializable;

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
import javax.persistence.SequenceGenerator;

import br.com.cadastroit.services.api.enums.TipoTelefone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SequenceGenerator(name = "TELEFONE_SEQ", sequenceName = "TELEFONE_SEQ", allocationSize = 1, initialValue = 1)
public class Telefone implements Serializable {
	
	public static final int CODIGO_BRASIL = 55;

	private static final long serialVersionUID = 8765506202749397479L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TELEFONE_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "CODPAIS")
	private int codigoPais;

	@Column(name = "CODCIDADE")
	private int codigoCidade;

	@Column(name = "NUMERO")
	private String numero;

	@Column(name = "RAMAL")
	private int ramal;
	
	@Column(name = "OBSERVACAO")
	private String observacao;

	@ManyToOne
	@JoinColumn(name = "EMAIL_ID")
	private Email email;
	
	@Enumerated(EnumType.STRING)
	private TipoTelefone tipo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
	

	public String getNumeroFormatado() {

		String codPais = String.valueOf(this.codigoPais);
		String codCidade = String.valueOf(this.codigoCidade);
		StringBuilder sb = new StringBuilder();
		sb.append("(" + codPais + ") " + "(" + codCidade + ") " + this.numero);
		return sb.toString();
	}

	public String getNumeroFormatadoPadraoInternacional() {

		String codigoPais = String.valueOf(this.codigoPais);
		String codigoCidade = String.valueOf(this.codigoCidade);
		StringBuilder sb = new StringBuilder();
		sb.append("+" + codigoPais + " (" + codigoCidade + ") " + this.numero);
		return sb.toString();
	}

}
