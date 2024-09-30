package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import br.com.cadastroit.services.api.domain.CsfTipoLog;
import br.com.cadastroit.services.api.domain.LogGenerico;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.LogGenericoException;
import br.com.cadastroit.services.repositories.impl.LogGenericoRepositoryImpl;
import br.com.cadastroit.services.web.dto.LogGenericoDto;
import br.com.complianceit.services.api.domain.CsfTipoLog_;
import br.com.complianceit.services.api.domain.LogGenerico_;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class LogGenericoRepository implements Serializable {
	
    private final CommonsRepository commonsRepository;
	private static final long serialVersionUID = 5838885821843443627L;
	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "LOG_GENERICO";
	private static final String ORDER = "order";

	@Autowired
	private LogGenericoRepositoryImpl LogGenericoRepositoryImpl;

	public LogGenerico findById(Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		try {
			
			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LogGenerico> cq = cb.createQuery(LogGenerico.class);
			Root<LogGenerico> from = cq.from(LogGenerico.class);
			TypedQuery<LogGenerico> tQuery = em.createQuery(
					cq.select(from).orderBy(orderBy));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
            throw new LogGenericoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public LogGenerico findById(Long csfTipoLogId, Long id, EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LogGenerico> cq = cb.createQuery(LogGenerico.class);
			Root<LogGenerico> from = cq.from(LogGenerico.class);
			TypedQuery<LogGenerico> tQuery = null;

			if (csfTipoLogId != null ) {
				Join<LogGenerico, CsfTipoLog> joinCsfTipoLog = from.join(LogGenerico_.csfTipoLog, JoinType.INNER);
				tQuery = em.createQuery(
						cq.select(from).where(cb.equal(joinCsfTipoLog.get(CsfTipoLog_.id), csfTipoLogId)));
			} else {
				tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(LogGenerico_.id), id)));
			}
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new LogGenericoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<LogGenerico> findAll(Long csfTipoLogId, EntityManagerFactory entityManagerFactory,
			Map<String, Object> requestParams, int page, int length) throws LogGenericoException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LogGenerico> cq = cb.createQuery(LogGenerico.class);
			Root<LogGenerico> from = cq.from(LogGenerico.class);
			TypedQuery<LogGenerico> tQuery = null;

			requestParams.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith(ORDER))
              .map(e ->
                      (e.getValue() == null || e.getValue().equals("desc"))
                              ? cb.desc(from.get(LogGenerico_.id))
                              : cb.asc(from.get(LogGenerico_.id)))
            .forEach(orderBy::add);
			
			if (csfTipoLogId != null ) {
				Join<LogGenerico, CsfTipoLog> joinCsfTipoLog = from.join(LogGenerico_.csfTipoLog, JoinType.INNER);
				tQuery = em.createQuery(
						cq.select(from).where(cb.equal(joinCsfTipoLog.get(CsfTipoLog_.id), csfTipoLogId)).orderBy(orderBy));
			} else {
				tQuery = em.createQuery(cq.select(from).orderBy(cb.desc(from.get(LogGenerico_.id))));
			}
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new LogGenericoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<LogGenerico> findByFilters(Long csfTipoLogId, LogGenericoDto entityDto,
			EntityManagerFactory entityManagerFactory, Map<String, Object> requestParams, int page, int max)
			throws LogGenericoException {
		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LogGenerico> cq = cb.createQuery(LogGenerico.class);
			Root<LogGenerico> from = cq.from(LogGenerico.class);

			requestParams.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith(ORDER))
				.map(e -> {
					return (e.getValue() == null || e.getValue().equals("desc"))
						? cb.desc(from.get(LogGenerico_.id))
						: cb.asc(from.get(LogGenerico_.id));
			
				}).forEach(orderBy::add);

			List<Predicate> predicates = createPredicates(csfTipoLogId, entityDto, from, cb);
			TypedQuery<LogGenerico> tQuery = em
					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new LogGenericoException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long csfTipoLogId, LogGenericoDto entityDto, Root<LogGenerico> from, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();
		
		Optional.ofNullable(entityDto).ifPresent(dto -> {

			this.commonsRepository.getUtilities().checkIsNull(dto.getProcessoId()).ifPresent(
					values -> predicates.add(cb.like(from.get(LogGenerico_.processoId).as(String.class),  "%" + values + "%")));

			this.commonsRepository.getUtilities().checkIsNull(dto.getReferenciaId()).ifPresent(
					values -> predicates.add(cb.like(from.get(LogGenerico_.referenciaId).as(String.class),  "%" + values + "%")));

			this.commonsRepository.getUtilities().checkIsNull(dto.getObjReferencia()).ifPresent(
					values -> predicates.add(cb.like(from.get(LogGenerico_.objReferencia).as(String.class),  "%" + values + "%")));
			
			this.commonsRepository.getUtilities().checkIsNull(dto.getMensagem()).ifPresent(
					values -> predicates.add(cb.like(from.get(LogGenerico_.mensagem),  "%" + values + "%")));

		});

		Optional.ofNullable(entityDto).map(LogGenericoDto::getCsfTipoLog).ifPresent(dto -> {
			Join<LogGenerico, CsfTipoLog> joinCsfTipoLog = from.join(LogGenerico_.csfTipoLog, JoinType.INNER);
			predicates.add(cb.equal(joinCsfTipoLog.get(CsfTipoLog_.id),  dto.getId()));

		});

		return predicates;
	}

	public Long count(Long csfTipoLogId, LogGenericoDto entityDto, EntityManagerFactory entityManagerFactory)
			throws LogGenericoException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<LogGenerico> from = cq.from(LogGenerico.class);
			if (entityDto != null) {
				predicates = createPredicates(csfTipoLogId, entityDto, from, cb);
			} else {
				predicates = createPredicates(csfTipoLogId, null, from, cb);
			}
			return em
					.createQuery(
							cq.select(cb.count(from.get(LogGenerico_.id))).where(predicates.toArray(new Predicate[] {})))
					.getSingleResult();
		} catch (Exception ex) {
			throw new LogGenericoException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Optional<LogGenerico> findById(Long id) {
		return LogGenericoRepositoryImpl.findById(id);
	}
}