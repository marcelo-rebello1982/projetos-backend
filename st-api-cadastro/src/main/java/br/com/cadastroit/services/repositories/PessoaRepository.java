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

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Departamento_;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Pessoa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.impl.PessoaRepositoryImpl;
import br.com.cadastroit.services.web.dto.PessoaDTO;
import br.com.cadastroit.services.web.mapper.PessoaMapper;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PessoaRepository implements Serializable {

	private static final long serialVersionUID = -6226504130558319561L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PESSOA";
	private static final String ORDER = "order";
	
	@Autowired
	private PessoaRepositoryImpl pessoaRepositoryImpl;
	
	@Autowired
	private TarefaService tarefaService;

	protected final PessoaMapper pessoaMapper = Mappers.getMapper(PessoaMapper.class);
	
	public Long maxId(EntityManagerFactory entityManagerFactory, String cd) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}
	
	
	public Pessoa findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
			Root<Pessoa> from = cq.from(Pessoa.class);
			TypedQuery<Pessoa> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Pessoa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Pessoa findByQueryParam(Long id, Long deptoId, String descr) {

		try {
			Pessoa pessoa = pessoaRepositoryImpl.findByQueryParam(id, deptoId, descr);
			return pessoa;
		} catch (NoResultException ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
		}
	}
	
	public Pessoa findById(Long departamentoId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
			Root<Pessoa> from = cq.from(Pessoa.class);
			Join<Pessoa, Departamento> joinDepartamento = from.join(Pessoa_.departamento, JoinType.INNER);
			TypedQuery<Pessoa> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinDepartamento.get(Departamento_.id), departamentoId), cb.equal(from.get(Pessoa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
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
			Root<Pessoa> from = cq.from(Pessoa.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(Pessoa_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory, Long departamentoId) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pessoa> from = cq.from(Pessoa.class);
			Join<Pessoa, Departamento> joinDepartamento = from.join(Pessoa_.departamento, JoinType.INNER);
			TypedQuery<Long> tQuery = em
					.createQuery(cq.select(cb.max(from.get(Pessoa_.id))).where(cb.equal(joinDepartamento.get(Departamento_.id), departamentoId)));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", departamentoId));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long count(Long departamentoId, PessoaDTO entityDto, EntityManagerFactory entityManagerFactory) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pessoa> from = cq.from(Pessoa.class);
			predicates = this.createPredicates(departamentoId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new PessoaException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Pessoa> findAll(Long departamentoId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
			Root<Pessoa> from = cq.from(Pessoa.class);
			Join<Pessoa, Departamento> joinDepartamento = from.join(Pessoa_.departamento, JoinType.INNER);

			requestParams.entrySet().stream().filter(entry -> entry.getKey().startsWith(ORDER)).map(e -> {
				return (e.getValue() == null || e.getValue().equals("desc")) ? cb.desc(from.get(Pessoa_.id)) : cb.asc(from.get(Pessoa_.id));

			}).forEach(orderBy::add);

			TypedQuery<Pessoa> tQuery = em.createQuery(cq.select(from).where(cb.equal(joinDepartamento.get(Departamento_.id), departamentoId)).orderBy(orderBy));

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PessoaException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Pessoa> findByFilters(Long departamentoId, PessoaDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
			Root<Pessoa> from = cq.from(Pessoa.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Pessoa_.id))
							: cb.asc(from.get(Pessoa_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(departamentoId, dto, from, cb);
			TypedQuery<Pessoa> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PessoaException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Predicate> createPredicates(Long departamentoId, PessoaDTO entityDto, Root<Pessoa> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

	//	Join<Pessoa, Tarefa> joinTarefa = from.join(Pessoa_.tarefas, JoinType.INNER);
	//	Join<Pessoa, Telefone> joinTelefone = from.join(Pessoa_.telefone, JoinType.INNER);

		if (departamentoId != null) {
			
			Join<Pessoa, Departamento> joinDepartamento = from.join(Pessoa_.departamento, JoinType.INNER);
			predicates.add(cb.equal(joinDepartamento.get(Departamento_.id), departamentoId));
			
		}

		if (entityDto != null) {

			checkIsNull(entityDto.getNome()).ifPresent(field -> predicates.add(cb.like(from.get(Pessoa_.nome).as(String.class), "%" + field + "%")));
		//	checkIsNull(entityDto.getDataInicio())
		//			.ifPresent(field -> predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), field)));
		//	checkIsNull(entityDto.getDataFinal()).ifPresent(field -> predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), field)));
		//	checkIsNull(entityDto.getTelefone().getNumero())
		//			.ifPresent(field -> predicates.add(cb.like(joinTelefone.get(Telefone_.numero), "%" + field + "%")));
		//	checkIsNull(entityDto.getTelefone().getTipo()).ifPresent(field -> predicates.add(cb.equal(joinTelefone.get(Telefone_.tipo), field)));

		}

		return predicates;
	}
	
	public Optional<Pessoa> findById(Long id) {

		return pessoaRepositoryImpl.findById(id);
	}

	public <S extends Pessoa> S save(S entity) {

		return pessoaRepositoryImpl.save(entity);
	}

	public void delete(Pessoa entity) {

		pessoaRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
