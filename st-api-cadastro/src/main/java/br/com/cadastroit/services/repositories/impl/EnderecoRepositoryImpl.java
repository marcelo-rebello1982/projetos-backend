package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cadastroit.services.api.domain.Endereco;

public interface EnderecoRepositoryImpl extends JpaRepository<Endereco, Long> {
}

