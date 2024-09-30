package br.com.cadastroit.services.repositories.impl;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.cadastroit.services.api.domain.SolicitacaoDocumento;

public interface SolicitacaoDocumentoRepositoryImpl	extends JpaRepository<SolicitacaoDocumento, Long> {

	@Query("FROM SolicitacaoDocumento sd WHERE LOWER(sd.descricaoPagamento) like %:descrPagto%")
	Page<SolicitacaoDocumento> searchAllPage(@Param("descrPagto") String descrPagto, Pageable pageable);

	@Query(value = "SELECT s.* , sum(value) as value FROM SolicitacaoDocumento s " + "	where s.pessoa_id = :personId group by 1", nativeQuery = true)
	List<SolicitacaoDocumento> searchSumExpense(@Param("personId") Long personId);

	@Query(value = "SELECT s.*, sum(value) as value FROM SolicitacaoDocumento group by 1", nativeQuery = true)
	List<SolicitacaoDocumento> searchSumExpenseTotal();

}