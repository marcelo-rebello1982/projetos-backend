package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cadastroit.services.api.domain.ParametroEmpresa;

public interface ParametroEmpresaRepositoryImpl extends JpaRepository<ParametroEmpresa, Long> {
	
	// @Query("SELECT p FROM PessoaEmpresa p JOIN p.departamento d WHERE p.departamento.id = :deptoId AND p.id = :id AND d.descr = :descr ORDER BY p.id DESC ")
	// Pessoa findByQueryParam(@Param("id") Long id, @Param("deptoId") Long deptoId, @Param("descr") String descr);
}
