package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.NotNull;

import br.com.cadastroit.services.api.enums.TipoArquivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO  {

	private Long id;
	
	@NotNull(message = "Campo titulo da tarefa sem preenchimento")
	private String titulo;

	@NotNull(message = "Campo descricao da tarefa sem preenchimento")
	private String descr;
	
	private String tempoGasto;
	
	private Date prazo;
	
	private Date dataInicio;

	private Date dataFinal;
	
	private LocalDateTime horaInicio;

	private LocalDateTime horaFinal;

	private Boolean encerrado;
	
	private Long departamentoId;
	
	private Date dtUpdate;

	private Long pessoaId;
	
	private BigDecimal valorInicial;
	
	private TipoArquivo tipoArquivo;

}
