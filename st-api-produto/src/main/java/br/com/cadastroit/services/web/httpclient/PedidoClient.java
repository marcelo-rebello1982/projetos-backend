package br.com.cadastroit.services.web.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.cadastroit.services.api.domain.Pedido;

@FeignClient("st-api-cadastro")
public interface PedidoClient {

	@RequestMapping(method = RequestMethod.PUT, value = "/administracao/pedido/updateaprovedpayments/{pedidoId}")
	Pedido handleUpdateAprovedPayment(@PathVariable Long pedidoId, @RequestBody String pedido);

}
