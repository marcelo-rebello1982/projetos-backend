package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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
import br.com.cadastroit.services.api.domain.Telefone;
import br.com.cadastroit.services.api.domain.Telefone_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.exceptions.TelefoneException;
import br.com.cadastroit.services.repositories.impl.TelefoneRepositoryImpl;
import br.com.cadastroit.services.web.dto.TelefoneDTO;
import br.com.cadastroit.services.web.mapper.TelefoneMapper;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class TelefoneRepository implements Serializable {

	private static final long serialVersionUID = 8761006319086433784L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "TELEFONE";
	private static final String ORDER = "order";

	@Autowired
	private TelefoneRepositoryImpl telefoneRepositoryImpl;

	protected final TelefoneMapper departamento = Mappers.getMapper(TelefoneMapper.class);

	public Long maxId(EntityManagerFactory entityManagerFactory) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}

	public Long maxId_(EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Telefone> from = cq.from(Telefone.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(Telefone_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new TelefoneException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory, Long pessoaId) throws TelefoneException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Telefone> from = cq.from(Telefone.class);
			Join<Telefone, Pessoa> joinPessoa = from.join(Telefone_.pessoa, JoinType.INNER);
			TypedQuery<Long> tQuery = em.createQuery(cq.select(cb.max(from.get(Telefone_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new TelefoneException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pessoaId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Telefone findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Telefone> cq = cb.createQuery(Telefone.class);
			Root<Telefone> from = cq.from(Telefone.class);
			TypedQuery<Telefone> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Telefone_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new TelefoneException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Telefone findById(Long pessoaId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Telefone> cq = cb.createQuery(Telefone.class);
			Root<Telefone> from = cq.from(Telefone.class);
			TypedQuery<Telefone> tQuery = em
					.createQuery(cq.select(from).where(cb.equal(from.get(Telefone_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new TelefoneException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long count(Long pessoaId, TelefoneDTO entityDto, EntityManagerFactory entityManagerFactory) throws TelefoneException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Telefone> from = cq.from(Telefone.class);
			predicates = this.createPredicates(pessoaId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new TelefoneException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Telefone> findAll(Long pessoaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			AtomicLong quantidadeDePessoas = new AtomicLong(0L);
			AtomicLong quantidadeDeTarefas = new AtomicLong(0L);

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Telefone> cq = cb.createQuery(Telefone.class);
			Root<Telefone> from = cq.from(Telefone.class);


			List<Order> orderBy = requestParams.entrySet()
					.stream()
					.filter(entry -> entry.getKey().startsWith(ORDER))
					.map(entry -> (entry.getValue() == null || "desc".equalsIgnoreCase(entry.getValue())) ? cb.desc(from.get(Telefone_.id))
							: cb.asc(from.get(Telefone_.id)))
					.toList();

			TypedQuery<Telefone> tQuery = em.createQuery(cq.select(from).orderBy(orderBy));
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new TelefoneException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Telefone> findByFilters(Long pessoaId, TelefoneDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws TelefoneException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Telefone> cq = cb.createQuery(Telefone.class);
			Root<Telefone> from = cq.from(Telefone.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Telefone_.id))
							: cb.asc(from.get(Telefone_.id))));
				}
			}

			List<Predicate> predicates = createPredicates(pessoaId, dto, from, cb);
			TypedQuery<Telefone> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new TelefoneException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long pessoaId, TelefoneDTO entityDto, Root<Telefone> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();


		if (entityDto != null) {

			// if (entityDto != null && entityDto.getDtIni() != null) {
			// predicates.add(cb.greaterThanOrEqualTo(from.get(ContrCofinsDifPerAnt_.dtIni), entityDto.getDtIni()));
			// }
			//
			// if (entityDto != null && entityDto.getDtFin() != null) {
			// predicates.add(cb.lessThanOrEqualTo(from.get(ContrCofinsDifPerAnt_.dtFin), entityDto.getDtFin()));
			// }

		}

		return predicates;
	}

	public Optional<Telefone> findById(Long id) {

		return telefoneRepositoryImpl.findById(id);
	}

	public <S extends Telefone> S save(S entity) {

		return telefoneRepositoryImpl.save(entity);
	}

	public void delete(Telefone entity) {

		telefoneRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
