package br.com.cadastroit.services.web.dto;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import br.com.cadastroit.services.api.enums.Status;
import br.com.cadastroit.services.api.enums.TipoDocumento;
import br.com.cadastroit.services.api.enums.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
	
	private Long id;
	
	@Size(min = 1, max = 255, message = "Campo nome não pode ultrapassar o total de 255 caracteres.")
	private String nome;

	private String email;
	
	private String fone;

	private Date dataNascimento;
	
	public Double mediaDeHorasPorTarefa;
	
	private String numeroTelefone;
	
	private DepartamentoDTO departamento;
	
	private LocalDateTime createAt;
	
	private LocalDateTime updateAt;
	
	private TipoDocumento tipoDocumento;
	
	private TipoPessoa tipoPessoa;
	
	private Status status;
	
	private ContatoDTO contato;
	
	private Date dataInicio;

	private Date dataFinal;
	
	private List<EnderecoDTO> enderecos;
	
	private List<TelefoneDTO> telefone;
	
	private List<EmailDTO> emailList;
}