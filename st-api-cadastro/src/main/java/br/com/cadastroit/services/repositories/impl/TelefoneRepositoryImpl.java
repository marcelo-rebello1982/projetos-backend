package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cadastroit.services.api.domain.Telefone;

public interface TelefoneRepositoryImpl extends JpaRepository<Telefone, Long> {
}

