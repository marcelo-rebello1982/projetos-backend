package br.com.cadastroit.services.repositories.impl;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import br.com.cadastroit.services.api.domain.Parametro;

@NoRepositoryBean
public interface ParametroGenericRepositoryImpl<T extends Parametro<?>> extends JpaRepository<T, Long> , JpaSpecificationExecutor<T> {
}

