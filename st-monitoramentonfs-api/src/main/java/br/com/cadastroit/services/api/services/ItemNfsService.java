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

import br.com.cadastroit.services.api.domain.ItemNfs;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.exceptions.ItemNfsException;
import br.com.cadastroit.services.repositories.impl.ItemNfsRepositoryImpl;
import br.com.cadastroit.services.web.dto.ItemNfsDto;
import br.com.complianceit.services.api.domain.ItemNfs_;
import br.com.complianceit.services.api.domain.NfServ_;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ItemNfsService {

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "ITEM_NFS";
	private static final String ORDER = "order";
	
	private ItemNfsRepositoryImpl repository;


	public Long maxId(EntityManagerFactory entityManagerFactory) throws ItemNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ItemNfs> from = cq.from(ItemNfs.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(ItemNfs_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new ItemNfsException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public ItemNfs findById(Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemNfs> cq = cb.createQuery(ItemNfs.class);
			Root<ItemNfs> from = cq.from(ItemNfs.class);
			TypedQuery<ItemNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(ItemNfs_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}

	public ItemNfs findById(Long nfServId, Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemNfs> cq = cb.createQuery(ItemNfs.class);
			Root<ItemNfs> from = cq.from(ItemNfs.class);
			Join<ItemNfs, NfServ> joinNfServ = from.join(ItemNfs_.nfServ, JoinType.INNER);
			TypedQuery<ItemNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(ItemNfs_.id), id),
					cb.equal(joinNfServ.get(NfServ_.id), nfServId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<ItemNfs> findAll(Long nfServId, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int length) throws ItemNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemNfs> cq = cb.createQuery(ItemNfs.class);
			Root<ItemNfs> from = cq.from(ItemNfs.class);
			Join<ItemNfs, NfServ> joinNfServ = from.join(ItemNfs_.nfServ, JoinType.INNER);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(ItemNfs_.id))
							: cb.asc(from.get(ItemNfs_.id))));
				}
			}

			TypedQuery<ItemNfs> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinNfServ.get(NfServ_.id), nfServId)).orderBy(orderBy));
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ItemNfsException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<ItemNfs> findByFilters(Long nfServId, ItemNfsDto entityDto, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int max) throws ItemNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemNfs> cq = cb.createQuery(ItemNfs.class);
			Root<ItemNfs> from = cq.from(ItemNfs.class);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(ItemNfs_.id))
							: cb.asc(from.get(ItemNfs_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(nfServId, entityDto, from, cb);
			TypedQuery<ItemNfs> tQuery = em
					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ItemNfsException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long nfServId, ItemNfsDto entityDto, Root<ItemNfs> from,
			CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		Join<ItemNfs, NfServ> joinNfServ = from.join(ItemNfs_.nfServ, JoinType.INNER);
		predicates.add(cb.equal(joinNfServ.get(NfServ_.id), nfServId));
		
		Optional.ofNullable(entityDto).ifPresent(dto -> {

			if (dto.getAliquotaIss() != null) {
				predicates.add(cb.equal(from.get(ItemNfs_.aliquotaIss), entityDto.getAliquotaIss()));
			}

		});
		
		return predicates;
	}

	public Long count(Long nfServId, ItemNfsDto entityDto, EntityManagerFactory entityManagerFactory)
			throws ItemNfsException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ItemNfs> from = cq.from(ItemNfs.class);
			if (entityDto != null) {
				predicates = this.createPredicates(nfServId, entityDto, from, cb);
			} else {
				predicates = this.createPredicates(nfServId, null, from, cb);
			}
			return em
					.createQuery(
							cq.select(cb.count(from.get(ItemNfs_.id))).where(predicates.toArray(new Predicate[] {})))
					.getSingleResult();
		} catch (Exception ex) {
			throw new ItemNfsException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Optional<ItemNfs> findById(Long id) {
		return repository.findById(id);
	}

	public <S extends ItemNfs> S save(S entity) {
		return repository.save(entity);
	}

	public void delete(ItemNfs entity) {
		repository.delete(entity);
	}
}
