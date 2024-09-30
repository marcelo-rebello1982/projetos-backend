package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cadastroit.services.api.domain.Pedido;

public interface ProcessaPedidoRepositoryImpl extends JpaRepository<Pedido, Long> {
}
