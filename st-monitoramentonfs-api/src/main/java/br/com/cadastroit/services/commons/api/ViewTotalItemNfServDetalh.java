package br.com.cadastroit.services.commons.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewTotalItemNfServDetalh {

	private Integer qtdTotalDeEstados;
	private Integer qtdTotalDeCidades;
	private Double somatoriaVlTotServ;
	private List<ViewTotalItemNfServ> listaItemNfServs;

	
}
