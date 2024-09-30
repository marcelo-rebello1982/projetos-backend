package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cadastroit.services.api.domain.Pessoa;

public interface PessoaRepositoryImpl extends JpaRepository<Pessoa, Long> {
}

