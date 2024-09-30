package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Pensamento;
import br.com.cadastroit.services.api.domain.Pensamento_;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Pessoa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.PensamentoException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.impl.PensamentoRepositoryImpl;
import br.com.cadastroit.services.web.dto.PensamentoDTO;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PensamentoRepository implements Serializable {

	private static final long serialVersionUID = 526089755767275908L;
	
	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PENSAMENTO";
	private static final String ORDER = "order";
	
	@Autowired
	private PensamentoRepositoryImpl pensamentoRepositoryImpl;

	public Long maxId(EntityManagerFactory entityManagerFactory) throws PensamentoException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pensamento> from = cq.from(Pensamento.class);
			TypedQuery<Long> tQuery = em
					.createQuery(cq.select(cb.max(from.get(Pensamento_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new PensamentoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", ""));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Pensamento findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pensamento> cq = cb.createQuery(Pensamento.class);
			Root<Pensamento> from = cq.from(Pensamento.class);
			TypedQuery<Pensamento> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Pensamento_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PensamentoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	
	public Long maxId(EntityManagerFactory entityManagerFactory, Long pessoaId) throws PensamentoException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pensamento> from = cq.from(Pensamento.class);
			TypedQuery<Long> tQuery = em.createQuery(cq.select(cb.max(from.get(Pensamento_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new PensamentoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pessoaId));
		} finally {
			em.clear();
			em.close();
		}
	}


	public Pensamento findById(Long pedidoId, Long pessoaId, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pensamento> cq = cb.createQuery(Pensamento.class);
			Root<Pensamento> from = cq.from(Pensamento.class);
			Join<Pensamento, Pessoa> joinPessoa = from.join(Pensamento_.pessoa, JoinType.INNER);
			TypedQuery<Pensamento> tQuery = em.createQuery(cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PensamentoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pedidoId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long count(Long pessoaId, PensamentoDTO entityDto, EntityManagerFactory entityManagerFactory) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pensamento> from = cq.from(Pensamento.class);
			predicates = this.createPredicates(pessoaId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new PensamentoException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Pensamento> findAll(EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pensamento> cq = cb.createQuery(Pensamento.class);
			Root<Pensamento> from = cq.from(Pensamento.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Pensamento_.id))
							: cb.asc(from.get(Pensamento_.id))));
				}
			}

			TypedQuery<Pensamento> tQuery = em.createQuery(cq.select(from).orderBy(orderBy));
			List<Pensamento> pensamentos = tQuery.getResultList();

			pensamentos.stream().forEach(pensamento -> {
			});

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return pensamentos;
		} catch (Exception ex) {
			throw new PensamentoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Pensamento> findByFilters(Long pessoaId, PensamentoDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pensamento> cq = cb.createQuery(Pensamento.class);
			Root<Pensamento> from = cq.from(Pensamento.class);

			// o parametro order via pathVariable apenas para ordenação asc ou desc conforme seleção do usuário em tela.
			// http://{{SERVER}}:{{GATEWAY_PORT}}/st-api-cadastro-pensamento/administracao/pensamento/all/1/10?order=desc
			
			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Pensamento_.id))
							: cb.asc(from.get(Pensamento_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(pessoaId, dto, from, cb);
			TypedQuery<Pensamento> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PensamentoException(String.format(MODE, "findByFilters", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	
	
	public List<Predicate> createPredicates(Long pessoaId, PensamentoDTO entityDto, Root<Pensamento> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		// aqui eu poderia fazer um join filtrando por pensamento de uma determinada pessoa somente.

		if (pessoaId != null && pessoaId > 0L) {
			Join<Pensamento, Pessoa> joinPessoa = from.join(Pensamento_.pessoa, JoinType.INNER);
			predicates.add(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId));
		}

	    if (entityDto != null) {
	    	
	    	checkIsNull(entityDto.isFavorito()).ifPresent(field -> predicates.add(cb.equal(from.get(Pensamento_.favorito), field)));
	    	checkIsNull(entityDto.getConteudo()).ifPresent(field -> predicates.add(cb.like(from.get(Pensamento_.conteudo), "%" + field + "%")));
	    	
	    }
	 	    

		// if (entityDto.getDataInicio() != null) {
		// predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), entityDto.getDataInicio()));
		//
		// }

		// if (entityDto.getDataFinal() != null) {
		// predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), entityDto.getDataFinal()));
		// }

		return predicates;
	}

	
	public Optional<Pensamento> findById(Long id) {

		return pensamentoRepositoryImpl.findById(id);
	}

	public <S extends Pensamento> S save(S entity) {

		return pensamentoRepositoryImpl.save(entity);
	}

	public void delete(Pensamento entity) {

		pensamentoRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}
}
