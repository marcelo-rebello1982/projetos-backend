package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.cadastroit.services.api.domain.Pessoa;

public interface PessoaRepositoryImpl extends JpaRepository<Pessoa, Long> {

	@Query("SELECT p FROM Pessoa p JOIN p.departamento d WHERE p.departamento.id = :deptoId AND p.id = :id AND d.descr = :descr ORDER BY p.id DESC ")
	Pessoa findByQueryParam(@Param("id") Long id, @Param("deptoId") Long deptoId, @Param("descr") String descr);
}
