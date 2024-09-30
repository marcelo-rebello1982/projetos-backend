package br.com.cadastroit.services.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.FaturaNfs;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.exceptions.FaturaNfsException;
import br.com.cadastroit.services.repositories.impl.FaturaNfsRepositoryImpl;
import br.com.cadastroit.services.web.dto.FaturaNfsDto;
import br.com.complianceit.services.api.domain.FaturaNfs_;
import br.com.complianceit.services.api.domain.NfServ_;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class FaturaNfsService {

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "FATURA_NFS";
	private static final String ORDER = "order";

	private FaturaNfsRepositoryImpl repository;

	public Long maxId(EntityManagerFactory entityManagerFactory) throws FaturaNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<FaturaNfs> from = cq.from(FaturaNfs.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(FaturaNfs_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new FaturaNfsException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public FaturaNfs findById(Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FaturaNfs> cq = cb.createQuery(FaturaNfs.class);
			Root<FaturaNfs> from = cq.from(FaturaNfs.class);
			TypedQuery<FaturaNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(FaturaNfs_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}

	public FaturaNfs findById(Long nfServId, Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FaturaNfs> cq = cb.createQuery(FaturaNfs.class);
			Root<FaturaNfs> from = cq.from(FaturaNfs.class);
			Join<FaturaNfs, NfServ> joinNfServ = from.join(FaturaNfs_.nfServ, JoinType.INNER);
			TypedQuery<FaturaNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(FaturaNfs_.id), id),
					cb.equal(joinNfServ.get(NfServ_.id), nfServId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<FaturaNfs> findAll(Long nfServId, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int length) throws FaturaNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FaturaNfs> cq = cb.createQuery(FaturaNfs.class);
			Root<FaturaNfs> from = cq.from(FaturaNfs.class);
			Join<FaturaNfs, NfServ> joinNfServ = from.join(FaturaNfs_.nfServ, JoinType.INNER);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(FaturaNfs_.id))
							: cb.asc(from.get(FaturaNfs_.id))));
				}
			}

			TypedQuery<FaturaNfs> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinNfServ.get(NfServ_.id), nfServId)).orderBy(orderBy));
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new FaturaNfsException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<FaturaNfs> findByFilters(Long nfServId, FaturaNfsDto entityDto,
			EntityManagerFactory entityManagerFactory, Map<String, Object> requestParams, int page, int max)
			throws FaturaNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FaturaNfs> cq = cb.createQuery(FaturaNfs.class);
			Root<FaturaNfs> from = cq.from(FaturaNfs.class);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(FaturaNfs_.id))
							: cb.asc(from.get(FaturaNfs_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(nfServId, entityDto, from, cb);
			TypedQuery<FaturaNfs> tQuery = em
					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new FaturaNfsException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long nfServId, FaturaNfsDto entityDto, Root<FaturaNfs> from,
			CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		Join<FaturaNfs, NfServ> joinNfServ = from.join(FaturaNfs_.nfServ, JoinType.INNER);
		predicates.add(cb.equal(joinNfServ.get(NfServ_.id), nfServId));
		
		Optional.ofNullable(entityDto).ifPresent(dto -> {

			if (dto.getNroFatura() != null) {
				predicates.add(cb.equal(from.get(FaturaNfs_.nroFatura), entityDto.getNroFatura()));
			}

		});

		return predicates;
	}

	public Long count(Long nfServId, FaturaNfsDto entityDto, EntityManagerFactory entityManagerFactory)
			throws FaturaNfsException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<FaturaNfs> from = cq.from(FaturaNfs.class);
			if (entityDto != null) {
				predicates = this.createPredicates(nfServId, entityDto, from, cb);
			} else {
				predicates = this.createPredicates(nfServId, null, from, cb);
			}
			return em
					.createQuery(
							cq.select(cb.count(from.get(FaturaNfs_.id))).where(predicates.toArray(new Predicate[] {})))
					.getSingleResult();
		} catch (Exception ex) {
			throw new FaturaNfsException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Optional<FaturaNfs> findById(Long id) {
		return repository.findById(id);
	}

	public <S extends FaturaNfs> S save(S entity) {
		return repository.save(entity);
	}

	public void delete(FaturaNfs entity) {
		repository.delete(entity);
	}
}
