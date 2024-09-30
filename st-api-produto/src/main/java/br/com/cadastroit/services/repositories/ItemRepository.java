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
import javax.persistence.NonUniqueResultException;
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
import br.com.cadastroit.services.api.domain.Item;
import br.com.cadastroit.services.api.domain.Item_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.DepartamentoException;
import br.com.cadastroit.services.exceptions.ItemException;
import br.com.cadastroit.services.repositories.impl.ItemRepositoryImpl;
import br.com.cadastroit.services.web.dto.ItemDTO;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ItemRepository implements Serializable {

	private static final long serialVersionUID = 371410669357239669L;
	
	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "ITEM";
	private static final String ORDER = "order";

	@Autowired
	private ItemRepositoryImpl itemRepositoryImpl;

	public Long maxIdJPQl(EntityManagerFactory entityManagerFactory) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}
	
	public Long maxId(EntityManagerFactory entityManagerFactory, Long empresaId) throws ItemException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Item> from = cq.from(Item.class);
			TypedQuery<Long> tQuery = em
					.createQuery(cq.select(cb.max(from.get(Item_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new ItemException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", empresaId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory) throws ItemException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Item> from = cq.from(Item.class);
			TypedQuery<Long> tQuery = em.createQuery(cq.select(cb.max(from.get(Item_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new ItemException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", ""));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Item findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> from = cq.from(Item.class);
			TypedQuery<Item> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Item_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new DepartamentoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Item findById(Long empresaId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> from = cq.from(Item.class);
			Join<Item, Empresa> joinEmpresa = from.join(Item_.empresa, JoinType.INNER);
			TypedQuery<Item> result = em
					.createQuery(cq.select(from).where(cb.equal(joinEmpresa.get(Empresa_.id), empresaId), cb.equal(from.get(Item_.id), id)));
			return result.getSingleResult();
		} catch (NoResultException ex) {
			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
		} catch (NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long count(Long empresaId, ItemDTO entityDto, EntityManagerFactory entityManagerFactory) throws ItemException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Item> from = cq.from(Item.class);
			predicates = createPredicates(empresaId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new ItemException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Item> findAll(Long empresaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws ItemException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> from = cq.from(Item.class);
			Join<Item, Empresa> joinEmpresa = from.join(Item_.empresa, JoinType.INNER);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Item_.id))
							: cb.asc(from.get(Item_.id))));
				}
			}

			TypedQuery<Item> tQuery = em.createQuery(cq.select(from).where(cb.equal(joinEmpresa.get(Empresa_.id), empresaId)).orderBy(orderBy));

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ItemException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Item> findByFilters(Long empresaId, ItemDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws ItemException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> from = cq.from(Item.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Item_.id))
							: cb.asc(from.get(Item_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(empresaId, dto, from, cb);
			TypedQuery<Item> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ItemException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long empresaId, ItemDTO entityDto, Root<Item> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		Join<Item, Empresa> joinEmpresa = from.join(Item_.empresa, JoinType.INNER);

		predicates.add(cb.equal(joinEmpresa.get(Empresa_.id), empresaId));

		if (entityDto != null) {

			checkIsNull(entityDto.getCodBarra()).ifPresent(field -> predicates.add(cb.equal(from.get(Item_.codBarra).as(String.class), field)));

			checkIsNull(entityDto.getCodItem()).ifPresent(field -> predicates.add(cb.equal(from.get(Item_.codItem).as(String.class), field)));

			checkIsNull(entityDto.getDescrItem()).ifPresent(field -> predicates.add(cb.equal(from.get(Item_.descrItem).as(String.class), field)));

			// if (entityDto.getDataInicio() != null) {
			// predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), entityDto.getDataInicio()));
			//
			// }
			//
			// if (entityDto.getDataFinal() != null) {
			// predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), entityDto.getDataFinal()));
		}
		return predicates;

	}

	public Optional<Item> findById(Long id) {

		return itemRepositoryImpl.findById(id);
	}

	public <S extends Item> S save(S entity) {

		return itemRepositoryImpl.save(entity);
	}

	public void delete(Item entity) {

		itemRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}
}
