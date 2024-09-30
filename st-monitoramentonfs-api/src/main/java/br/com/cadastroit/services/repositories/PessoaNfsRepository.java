package br.com.cadastroit.services.repositories;

import java.io.Serializable;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.api.domain.PessoaNfs;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.ConstrNfsException;
import br.com.cadastroit.services.exceptions.ItemNfsException;
import br.com.cadastroit.services.exceptions.PessoaNfsException;
import br.com.cadastroit.services.repositories.impl.PessoaNfsRepositoryImpl;
import br.com.cadastroit.services.web.dto.PessoaNfsDto;
import br.com.complianceit.services.api.domain.NfServ_;
import br.com.complianceit.services.api.domain.PessoaNfs_;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PessoaNfsRepository implements Serializable {

    private final CommonsRepository commonsRepository;
	private static final long serialVersionUID = -7659543508653135005L;
	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PESSOA_NFS";
	private static final String ORDER = "order";
	
	@Autowired
	private PessoaNfsRepositoryImpl pessoaNfsRepositoryImpl;
	
	public Long maxId(EntityManagerFactory entityManagerFactory) throws ConstrNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<PessoaNfs> from = cq.from(PessoaNfs.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(PessoaNfs_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new ConstrNfsException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory, Long nfServId) throws ItemNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb 													 = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq												 = cb.createQuery(Long.class);
			Root<PessoaNfs> from											     = cq.from(PessoaNfs.class);
			Join<PessoaNfs, NfServ> joinNfServ       	  						 = from.join(PessoaNfs_.nfServ, JoinType.INNER);
			TypedQuery<Long> tQuery                                   		     = em
					.createQuery(cq.select(cb.max(
							from.get(PessoaNfs_.id)))
							.where(cb.equal(joinNfServ.get(
									NfServ_.id), nfServId)));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new ConstrNfsException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public PessoaNfs findById(Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaNfs> cq = cb.createQuery(PessoaNfs.class);
			Root<PessoaNfs> from = cq.from(PessoaNfs.class);
			TypedQuery<PessoaNfs> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(PessoaNfs_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
            throw new PessoaNfsException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public PessoaNfs findById(Long nfServId, Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaNfs> cq = cb.createQuery(PessoaNfs.class);
			Root<PessoaNfs> from = cq.from(PessoaNfs.class);
			Join<PessoaNfs, NfServ> joinNfServ = from.join(PessoaNfs_.nfServ, JoinType.INNER);
			TypedQuery<PessoaNfs> tQuery = em
					.createQuery(cq.select(from)
							.where(cb.equal(from.get(
									PessoaNfs_.id), id),
									cb.equal(joinNfServ.get(NfServ_.id), nfServId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaNfsException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<PessoaNfs> findAll(Long nfServId, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int length) throws PessoaNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaNfs> cq = cb.createQuery(PessoaNfs.class);
			Root<PessoaNfs> from = cq.from(PessoaNfs.class);
			Join<PessoaNfs, NfServ> joinNfServ = from.join(PessoaNfs_.nfServ, JoinType.INNER);

			  requestParams.entrySet().stream()
              .filter(entry -> entry.getKey().startsWith(ORDER))
              .map(e ->
                      (e.getValue() == null || e.getValue().equals("desc"))
                              ? cb.desc(from.get(PessoaNfs_.id))
                              : cb.asc(from.get(PessoaNfs_.id)))
              .forEach(orderBy::add);

			TypedQuery<PessoaNfs> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinNfServ.get(NfServ_.id), nfServId)).orderBy(orderBy));
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PessoaNfsException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<PessoaNfs> findByFilters(Long nfServId, PessoaNfsDto entityDto, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int max) throws PessoaNfsException {
		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PessoaNfs> cq = cb.createQuery(PessoaNfs.class);
			Root<PessoaNfs> from = cq.from(PessoaNfs.class);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc")
							? cb.desc(from.get(PessoaNfs_.id))
							: cb.asc(from.get(PessoaNfs_.id))));
				}
			}

			List<Predicate> predicates = createPredicates(nfServId, entityDto, from, cb);
			TypedQuery<PessoaNfs> tQuery = em
					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PessoaNfsException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long nfServId, PessoaNfsDto entityDto, Root<PessoaNfs> from,
			CriteriaBuilder cb) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		Join< PessoaNfs, NfServ> joinNfServ = from.join(PessoaNfs_.nfServ, JoinType.INNER);
     	predicates.add(cb.equal(joinNfServ.get(NfServ_.id), nfServId));

		 Optional.ofNullable(entityDto).ifPresent(entity -> {
			 
				this.commonsRepository.getUtilities().checkIsNull(entity.getCodPart()).ifPresent(
	                    values -> predicates.add(cb.and(cb.equal(from.get(PessoaNfs_.codPart), values))));

	            this.commonsRepository.getUtilities().checkIsNull(entity.getNomeTomador()).ifPresent(
	            		values -> predicates.add(cb.equal(from.get(PessoaNfs_.nomeTomador), values)));
	            
	            this.commonsRepository.getUtilities().checkIsNull(entity.getIbgeCidadeTomador())
                .ifPresent(values -> predicates.add(cb.equal(from.get(PessoaNfs_.ibgeCidadeTomador), values)));
	            
	            Optional.ofNullable(entityDto).map(PessoaNfsDto::getNfServ).ifPresent(nfs -> {
	            

	            });
	        });

		return predicates;
	}

	public Long count(Long nfServId, PessoaNfsDto entityDto, EntityManagerFactory entityManagerFactory)
			throws PessoaNfsException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<PessoaNfs> from = cq.from(PessoaNfs.class);
			if (entityDto != null) {
				predicates = this.createPredicates(nfServId, entityDto, from, cb);
			} else {
				predicates = this.createPredicates(nfServId, null, from, cb);
			}
			return em
					.createQuery(
							cq.select(cb.count(from.get(PessoaNfs_.id))).where(predicates.toArray(new Predicate[] {})))
					.getSingleResult();
		} catch (Exception ex) {
			throw new PessoaNfsException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Optional<PessoaNfs> findById(Long id) {
		return pessoaNfsRepositoryImpl.findById(id);
	}

	public <S extends PessoaNfs> S save(S entity) {
		return pessoaNfsRepositoryImpl.save(entity);
	}

	public void delete(PessoaNfs entity) {
		pessoaNfsRepositoryImpl.delete(entity);
	}
}
