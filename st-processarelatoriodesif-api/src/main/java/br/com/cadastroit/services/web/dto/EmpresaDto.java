package br.com.cadastroit.services.web.dto;
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
public class EmpresaDto  {

	private Long id;
}
