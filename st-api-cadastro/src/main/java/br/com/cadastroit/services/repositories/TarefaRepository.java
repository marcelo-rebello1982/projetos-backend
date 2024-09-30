package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Pessoa_;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.api.domain.Tarefa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.repositories.impl.TarefaRepositoryImpl;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import br.com.cadastroit.services.web.mapper.TarefaMapper;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class TarefaRepository implements Serializable {

	private static final long serialVersionUID = 2453626260069776656L;
	
	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "TAREFA";
	private static final String ORDER = "order";
	
	@Autowired
	private TarefaRepositoryImpl tarefaRepositoryImpl;

	protected final TarefaMapper tarefaMapper = Mappers.getMapper(TarefaMapper.class);
	
	public Long maxId(EntityManagerFactory entityManagerFactory, String cd) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}
	
	public Tarefa findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tarefa> cq = cb.createQuery(Tarefa.class);
			Root<Tarefa> from = cq.from(Tarefa.class);
			TypedQuery<Tarefa> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Tarefa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new TarefaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Tarefa findById(Long pessoaId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tarefa> cq = cb.createQuery(Tarefa.class);
			Root<Tarefa> from = cq.from(Tarefa.class);
			Join<Tarefa, Pessoa> joinPessoa = from.join(Tarefa_.pessoa, JoinType.INNER);
			TypedQuery<Tarefa> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId), cb.equal(from.get(Tarefa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new TarefaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long maxId(EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Tarefa> from = cq.from(Tarefa.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(Tarefa_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new TarefaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory, Long pessoaId) throws TarefaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Tarefa> from = cq.from(Tarefa.class);
			Join<Tarefa, Pessoa> joinPessoa = from.join(Tarefa_.pessoa, JoinType.INNER);
			TypedQuery<Long> tQuery = em
					.createQuery(cq.select(cb.max(from.get(Tarefa_.id))).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId)));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new TarefaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pessoaId));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long count(Long pessoaId, TarefaDTO entityDto, EntityManagerFactory entityManagerFactory) throws TarefaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Tarefa> from = cq.from(Tarefa.class);
			predicates = this.createPredicates(pessoaId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new TarefaException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Tarefa> findAll(Long pessoaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws TarefaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			List<Predicate> predicates = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tarefa> cq = cb.createQuery(Tarefa.class);
			Root<Tarefa> from = cq.from(Tarefa.class);
			Join<Tarefa, Pessoa> joinPessoa = from.join(Tarefa_.pessoa, JoinType.LEFT);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Tarefa_.id))
							: cb.asc(from.get(Tarefa_.id))));
				}
			}
			
			Predicate p1 = Boolean.parseBoolean(requestParams.get("tarefaSemResponsavel"))
				    ? cb.isNull(joinPessoa.get(Pessoa_.id))
				    : pessoaId != null ? cb.equal(joinPessoa.get(Pessoa_.id), pessoaId) : null;
			
			predicates.add(p1);
			
			predicates = this.createPredicates(pessoaId, null != null ? null : null, from, cb);
			
	        TypedQuery<Tarefa> tQuery = em.createQuery(cq.select(from)
	                .where(predicates.toArray(new Predicate[] {}))
	                .orderBy(orderBy));

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new TarefaException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
		
	public List<Tarefa> findByFilters(Long pessoaId, TarefaDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws TarefaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tarefa> cq = cb.createQuery(Tarefa.class);
			Root<Tarefa> from = cq.from(Tarefa.class);

			requestParams.entrySet().stream().filter(entry -> entry.getKey().startsWith(ORDER)).map(e -> {
				return (e.getValue() == null || e.getValue().equals("desc")) ? cb.desc(from.get(Tarefa_.id)) : cb.asc(from.get(Tarefa_.id));
			}).forEach(orderBy::add);

			List<Predicate> predicates = createPredicates(pessoaId, dto, from, cb);
			TypedQuery<Tarefa> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new TarefaException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Predicate> createPredicates(Long pessoaId, TarefaDTO entityDto, Root<Tarefa> from, CriteriaBuilder cb) {

		 List<Predicate> predicates = new ArrayList<>();
		 
		 
			if (pessoaId == null) {
				predicates.add(cb.isNotNull(from.get(Tarefa_.id)));

			} else {
				Join<Tarefa, Pessoa> joinPessoa = from.join(Tarefa_.pessoa, JoinType.INNER);
				predicates.add(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId));
			}

		    if (entityDto != null) {
		    	
		    	checkIsNull(entityDto.getDescr()).ifPresent(field -> predicates.add(cb.like(from.get(Tarefa_.descr),"%" + field + "%")));
		    	
		    	checkIsNull(entityDto.getEncerrado()).ifPresent(field -> predicates.add(cb.equal(from.get(Tarefa_.encerrado), field)));

		 	    
		    }
			
		return predicates;
	}
	
	public Optional<Tarefa> findById(Long id) {

		return tarefaRepositoryImpl.findById(id);
	}

	public <S extends Tarefa> S save(S entity) {

		return tarefaRepositoryImpl.save(entity);
	}

	public void delete(Tarefa entity) {

		tarefaRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
