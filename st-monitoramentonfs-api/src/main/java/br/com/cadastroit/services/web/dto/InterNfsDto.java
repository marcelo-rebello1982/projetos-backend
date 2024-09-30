package br.com.cadastroit.services.web.dto;

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
public class InterNfsDto {

	private Long id;

	@NotNull(message = "Campo nfServ sem preenchimento")
	private NfServDto nfServ;

	@NotNull(message = "Campo nome_intermed sem preenchimento")
	@Size(min = 1, max = 120, message = "Campo nomeIntermed nao pode ultrapassar o total de 120 caracteres.")
	private String nomeIntermed;

	@Size(min = 1, max = 15, message = "Campo imIntermed nao pode ultrapassar o total de 15 caracteres.")
	private String imIntermed;

	private String cpfIntermed;

	private String cnpjIntermed;

	private String ibgeIntermed;
}
