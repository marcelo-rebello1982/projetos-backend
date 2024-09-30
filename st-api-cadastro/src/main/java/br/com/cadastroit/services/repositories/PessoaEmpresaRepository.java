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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.api.domain.PessoaEmpresa;
import br.com.cadastroit.services.api.domain.PessoaEmpresa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.PessoaEmpresaException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.impl.PessoaEmpresaRepositoryImpl;
import br.com.cadastroit.services.web.dto.PessoaEmpresaDTO;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PessoaEmpresaRepository implements Serializable {

	private static final long serialVersionUID = -8191000189406752342L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PESSOAEMPRESA";
	private static final String ORDER = "order";

	@Autowired
	private PessoaEmpresaRepositoryImpl pessoaEmpresaRepositoryImpl;

	public Long maxId(EntityManagerFactory entityManagerFactory, String cd) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}

	public PessoaEmpresa findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaEmpresa> cq = cb.createQuery(PessoaEmpresa.class);
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);
			Join<PessoaEmpresa, Empresa> joinEmpresa = from.join(PessoaEmpresa_.empresa, JoinType.INNER);
			TypedQuery<PessoaEmpresa> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(PessoaEmpresa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaEmpresaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

//	public PessoaEmpresa findByQueryParam(Long id, Long empresaId, String descr) {
//
//		try {
//			PessoaEmpresa pessoaEmpresa = pessoaEmpresaRepositoryImpl.findByQueryParam(id, empresaId, descr);
//			return pessoaEmpresa;
//		} catch (NoResultException ex) {
//			throw new PessoaEmpresaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
//		} finally {
//		}
//	}

	public PessoaEmpresa findById(Long empresaId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaEmpresa> cq = cb.createQuery(PessoaEmpresa.class);
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);
			Join<PessoaEmpresa, Empresa> joinEmpresa = from.join(PessoaEmpresa_.empresa, JoinType.INNER);
			TypedQuery<PessoaEmpresa> tQuery = em
					.createQuery(cq.select(from).where(cb.equal(joinEmpresa.get(Empresa_.id), empresaId), cb.equal(from.get(PessoaEmpresa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaEmpresaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
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
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(PessoaEmpresa_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new PessoaEmpresaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory, Long empresaId) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);
			Join<PessoaEmpresa, Empresa> joinEmpresa = from.join(PessoaEmpresa_.empresa, JoinType.INNER);
			TypedQuery<Long> tQuery = em
					.createQuery(cq.select(cb.max(from.get(PessoaEmpresa_.id))).where(cb.equal(joinEmpresa.get(Empresa_.id), empresaId)));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new PessoaEmpresaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", empresaId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long count(Long empresaId, PessoaEmpresaDTO entityDto, EntityManagerFactory entityManagerFactory) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);
			predicates = this.createPredicates(empresaId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new PessoaEmpresaException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<PessoaEmpresa> findAll(Long empresaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaEmpresa> cq = cb.createQuery(PessoaEmpresa.class);
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);
			Join<PessoaEmpresa, Empresa> joinEmpresa = from.join(PessoaEmpresa_.empresa, JoinType.INNER);

			requestParams.entrySet().stream().filter(entry -> entry.getKey().startsWith(ORDER)).map(e -> {
				return (e.getValue() == null || e.getValue().equals("desc")) ? cb.desc(from.get(PessoaEmpresa_.id))
						: cb.asc(from.get(PessoaEmpresa_.id));

			}).forEach(orderBy::add);

			TypedQuery<PessoaEmpresa> tQuery = em
					.createQuery(cq.select(from).where(cb.equal(joinEmpresa.get(Empresa_.id), empresaId)).orderBy(orderBy));

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PessoaEmpresaException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<PessoaEmpresa> findByFilters(Long empresaId, PessoaEmpresaDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaEmpresa> cq = cb.createQuery(PessoaEmpresa.class);
			Root<PessoaEmpresa> from = cq.from(PessoaEmpresa.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(PessoaEmpresa_.id))
							: cb.asc(from.get(PessoaEmpresa_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(empresaId, dto, from, cb);
			TypedQuery<PessoaEmpresa> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PessoaEmpresaException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long empresaId, PessoaEmpresaDTO entityDto, Root<PessoaEmpresa> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		if (empresaId != null) {

			Join<PessoaEmpresa, Empresa> joinEmpresa = from.join(PessoaEmpresa_.empresa, JoinType.INNER);
			predicates.add(cb.equal(joinEmpresa.get(Empresa_.id), empresaId));

		}

		if (entityDto != null) {

			checkIsNull(entityDto.getNome())
					.ifPresent(field -> predicates.add(cb.like(from.get(PessoaEmpresa_.fone).as(String.class), "%" + field + "%")));
			// checkIsNull(entityDto.getDataInicio())
			// .ifPresent(field -> predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), field)));
			// checkIsNull(entityDto.getDataFinal()).ifPresent(field ->
			// predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), field)));
			// checkIsNull(entityDto.getTelefone().getNumero())
			// .ifPresent(field -> predicates.add(cb.like(joinTelefone.get(Telefone_.numero), "%" + field + "%")));
			// checkIsNull(entityDto.getTelefone().getTipo()).ifPresent(field ->
			// predicates.add(cb.equal(joinTelefone.get(Telefone_.tipo), field)));

		}

		return predicates;
	}

	public Optional<PessoaEmpresa> findById(Long id) {

		return pessoaEmpresaRepositoryImpl.findById(id);
	}

	public <S extends PessoaEmpresa> S save(S entity) {

		return pessoaEmpresaRepositoryImpl.save(entity);
	}

	public void delete(PessoaEmpresa entity) {

		pessoaEmpresaRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}
}
