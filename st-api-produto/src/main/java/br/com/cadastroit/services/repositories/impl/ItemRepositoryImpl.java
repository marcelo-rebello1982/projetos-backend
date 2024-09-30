package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cadastroit.services.api.domain.Item;

public interface ItemRepositoryImpl extends JpaRepository<Item, Long> {
}

