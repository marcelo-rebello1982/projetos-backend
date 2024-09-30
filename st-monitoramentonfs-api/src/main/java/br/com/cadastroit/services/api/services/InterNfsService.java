//package br.com.cadastroit.services.api.services;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Optional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.NoResultException;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Join;
//import javax.persistence.criteria.JoinType;
//import javax.persistence.criteria.Order;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import br.com.cadastroit.services.api.domain.InterNfs;
//import br.com.cadastroit.services.api.domain.InterNfs_;
//import br.com.cadastroit.services.api.domain.NfServ;
//import br.com.cadastroit.services.api.domain.NfServ_;
//import br.com.cadastroit.services.exceptions.InterNfsException;
//import br.com.cadastroit.services.repositories.impl.InterNfsRepositoryImpl;
//import br.com.cadastroit.services.web.dto.InterNfsDto;
//import lombok.AllArgsConstructor;
//
//@Service
//@AllArgsConstructor
//@Transactional(propagation = Propagation.REQUIRED)
//public class InterNfsService {
//
//	private static final String MODE = "Error on %s mode to %s, [error] = %s";
//	private static final String OBJECT = "ITEM_NFS";
//	private static final String ORDER = "order";
//	
//	private InterNfsRepositoryImpl repository;
//
//
//	public Long maxId(EntityManagerFactory entityManagerFactory) throws InterNfsException {
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//			Root<InterNfs> from = cq.from(InterNfs.class);
//			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(InterNfs_.id))));
//			return result.getSingleResult();
//		} catch (Exception ex) {
//			throw new InterNfsException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public InterNfs findById(Long id, EntityManagerFactory entityManagerFactory) {
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<InterNfs> cq = cb.createQuery(InterNfs.class);
//			Root<InterNfs> from = cq.from(InterNfs.class);
//			TypedQuery<InterNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(InterNfs_.id), id)));
//			return tQuery.getSingleResult();
//		} catch (NoResultException ex) {
//			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public InterNfs findById(Long nfServId, Long id, EntityManagerFactory entityManagerFactory) {
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<InterNfs> cq = cb.createQuery(InterNfs.class);
//			Root<InterNfs> from = cq.from(InterNfs.class);
//			Join<InterNfs, NfServ> joinNfServ = from.join(InterNfs_.nfServ, JoinType.INNER);
//			TypedQuery<InterNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(InterNfs_.id), id),
//					cb.equal(joinNfServ.get(NfServ_.id), nfServId)));
//			return tQuery.getSingleResult();
//		} catch (NoResultException ex) {
//			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public List<InterNfs> findAll(Long nfServId, EntityManagerFactory entityManagerFactory,
//			Map<String, Object> requestParams, int page, int length) throws InterNfsException {
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//
//			List<Order> orderBy = new ArrayList<>();
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<InterNfs> cq = cb.createQuery(InterNfs.class);
//			Root<InterNfs> from = cq.from(InterNfs.class);
//			Join<InterNfs, NfServ> joinNfServ = from.join(InterNfs_.nfServ, JoinType.INNER);
//
//			for (Entry<String, Object> entry : requestParams.entrySet()) {
//				if (entry.getKey().startsWith(ORDER)) {
//					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
//							? cb.desc(from.get(InterNfs_.id))
//							: cb.asc(from.get(InterNfs_.id))));
//				}
//			}
//
//			TypedQuery<InterNfs> tQuery = em.createQuery(
//					cq.select(from).where(cb.equal(joinNfServ.get(NfServ_.id), nfServId)).orderBy(orderBy));
//			tQuery.setFirstResult((page - 1) * length);
//			tQuery.setMaxResults(length);
//			return tQuery.getResultList();
//		} catch (Exception ex) {
//			throw new InterNfsException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public List<InterNfs> findByFilters(Long nfServId, InterNfsDto entityDto, EntityManagerFactory entityManagerFactory,
//			Map<String, Object> requestParams, int page, int max) throws InterNfsException {
//		EntityManager em = entityManagerFactory.createEntityManager();
//
//		try {
//
//			List<Order> orderBy = new ArrayList<>();
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<InterNfs> cq = cb.createQuery(InterNfs.class);
//			Root<InterNfs> from = cq.from(InterNfs.class);
//
//			for (Entry<String, Object> entry : requestParams.entrySet()) {
//				if (entry.getKey().startsWith(ORDER)) {
//					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
//							? cb.desc(from.get(InterNfs_.id))
//							: cb.asc(from.get(InterNfs_.id))));
//				}
//			}
//
//			List<Predicate> predicates = this.createPredicates(nfServId, entityDto, from, cb);
//			TypedQuery<InterNfs> tQuery = em
//					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
//			if (page != 0 && max != 0) {
//				tQuery.setFirstResult((page - 1) * max);
//				tQuery.setMaxResults(max);
//			}
//			return tQuery.getResultList();
//		} catch (Exception ex) {
//			throw new InterNfsException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public List<Predicate> createPredicates(Long nfServId, InterNfsDto entityDto, Root<InterNfs> from,
//			CriteriaBuilder cb) {
//		List<Predicate> predicates = new ArrayList<>();
//
//		Join<InterNfs, NfServ> joinNfServ = from.join(InterNfs_.nfServ, JoinType.INNER);
//		predicates.add(cb.equal(joinNfServ.get(NfServ_.id), nfServId));
//		
//		Optional.ofNullable(entityDto).ifPresent(dto -> {
//
//			if (dto.getCnpjIntermed() != null) {
//				predicates.add(cb.equal(from.get(InterNfs_.cnpjIntermed), entityDto.getCnpjIntermed()));
//			}
//		});
//
//		return predicates;
//	}
//
//	public Long count(Long nfServId, InterNfsDto entityDto, EntityManagerFactory entityManagerFactory)
//			throws InterNfsException {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		List<Predicate> predicates = new ArrayList<>();
//
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//			Root<InterNfs> from = cq.from(InterNfs.class);
//			if (entityDto != null) {
//				predicates = this.createPredicates(nfServId, entityDto, from, cb);
//			} else {
//				predicates = this.createPredicates(nfServId, null, from, cb);
//			}
//			
//			return em
//					.createQuery(
//							cq.select(cb.count(from.get(InterNfs_.id))).where(predicates.toArray(new Predicate[] {})))
//					.getSingleResult();
//		} catch (Exception ex) {
//			throw new InterNfsException(String.format(MODE, "count", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//	
//	public Optional<InterNfs> findById(Long id) {
//		return repository.findById(id);
//	}
//
//	public <S extends InterNfs> S save(S entity) {
//		return repository.save(entity);
//	}
//
//	public void delete(InterNfs entity) {
//		repository.delete(entity);
//	}
//}
package br;


