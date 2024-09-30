package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Date;

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
public class DesifCadCodTribMunDto {

	@JsonView({ View.isDisabled.class })
	private Long id;

	private String codTribMun;

	private BigDecimal desMista;

	private Date dtIniVig;

	private Date dtFimVig;

	private Date dtUpdate;
}
