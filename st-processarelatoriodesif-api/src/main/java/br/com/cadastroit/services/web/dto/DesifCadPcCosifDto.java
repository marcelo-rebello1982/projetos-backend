package br.com.cadastroit.services.web.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonView({ View.allEnable.class })
public class DesifCadPcCosifDto {

	private Long id;

	private String codCta;

	private String nomeConta;
	private Date dtCriacao;
	private Date dtExtincao;
	private String codCtaSup;

	@JsonView({ View.isDisabled.class })
	private String descrFuncCta;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ", locale = "pt_BR")
	private Date dtUpdate;
	
	private String contaCosif;

}
