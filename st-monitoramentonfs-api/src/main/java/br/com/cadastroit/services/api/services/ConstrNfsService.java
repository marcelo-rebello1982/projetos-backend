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

import br.com.cadastroit.services.api.domain.ConstrNfs;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.exceptions.ConstrNfsException;
import br.com.cadastroit.services.repositories.impl.ConstrNfsRepositoryImpl;
import br.com.cadastroit.services.web.dto.ConstrNfsDto;
import br.com.complianceit.services.api.domain.ConstrNfs_;
import br.com.complianceit.services.api.domain.NfServ_;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ConstrNfsService {

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "CONSTR_NFS";
	private static final String ORDER = "order";

	private ConstrNfsRepositoryImpl repository;

	public Long maxId(EntityManagerFactory entityManagerFactory) throws ConstrNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ConstrNfs> from = cq.from(ConstrNfs.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(ConstrNfs_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new ConstrNfsException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public ConstrNfs findById(Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ConstrNfs> cq = cb.createQuery(ConstrNfs.class);
			Root<ConstrNfs> from = cq.from(ConstrNfs.class);
			TypedQuery<ConstrNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(ConstrNfs_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}

	public ConstrNfs findById(Long nfServId, Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ConstrNfs> cq = cb.createQuery(ConstrNfs.class);
			Root<ConstrNfs> from = cq.from(ConstrNfs.class);
			Join<ConstrNfs, NfServ> joinNfServ = from.join(ConstrNfs_.nfServ, JoinType.INNER);
			TypedQuery<ConstrNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(ConstrNfs_.id), id),
					cb.equal(joinNfServ.get(NfServ_.id), nfServId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<ConstrNfs> findAll(Long nfServId, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int length) throws ConstrNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ConstrNfs> cq = cb.createQuery(ConstrNfs.class);
			Root<ConstrNfs> from = cq.from(ConstrNfs.class);
			Join<ConstrNfs, NfServ> joinNfServ = from.join(ConstrNfs_.nfServ, JoinType.INNER);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(ConstrNfs_.id))
							: cb.asc(from.get(ConstrNfs_.id))));
				}
			}

			TypedQuery<ConstrNfs> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinNfServ.get(NfServ_.id), nfServId)).orderBy(orderBy));
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ConstrNfsException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<ConstrNfs> findByFilters(Long nfServId, ConstrNfsDto entityDto,
			EntityManagerFactory entityManagerFactory, Map<String, Object> requestParams, int page, int max)
			throws ConstrNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ConstrNfs> cq = cb.createQuery(ConstrNfs.class);
			Root<ConstrNfs> from = cq.from(ConstrNfs.class);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(ConstrNfs_.id))
							: cb.asc(from.get(ConstrNfs_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(nfServId, entityDto, from, cb);
			TypedQuery<ConstrNfs> tQuery = em
					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ConstrNfsException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long nfServId, ConstrNfsDto entityDto, Root<ConstrNfs> from,
			CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		Join<ConstrNfs, NfServ> joinNfServ = from.join(ConstrNfs_.nfServ, JoinType.INNER);
		predicates.add(cb.equal(joinNfServ.get(NfServ_.id), nfServId));

		Optional.ofNullable(entityDto).ifPresent(dto -> {

			if (dto.getCodObra() != null) {
				predicates.add(cb.equal(from.get(ConstrNfs_.codObra), entityDto.getCodObra()));
			}
			 
		});
		
		return predicates;
	}

	public Long count(Long nfServId, ConstrNfsDto entityDto, EntityManagerFactory entityManagerFactory)
			throws ConstrNfsException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ConstrNfs> from = cq.from(ConstrNfs.class);
			if (entityDto != null) {
				predicates = this.createPredicates(nfServId, entityDto, from, cb);
			} else {
				predicates = this.createPredicates(nfServId, null, from, cb);
			}
			return em
					.createQuery(
							cq.select(cb.count(from.get(ConstrNfs_.id))).where(predicates.toArray(new Predicate[] {})))
					.getSingleResult();
		} catch (Exception ex) {
			throw new ConstrNfsException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Optional<ConstrNfs> findById(Long id) {
		return repository.findById(id);
	}

	public <S extends ConstrNfs> S save(S entity) {
		return repository.save(entity);
	}

	public void delete(ConstrNfs entity) {
		repository.delete(entity);
	}
}
