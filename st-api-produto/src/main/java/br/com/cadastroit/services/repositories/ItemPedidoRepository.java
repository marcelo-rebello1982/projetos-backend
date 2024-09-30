package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.api.domain.ItemPedido;
import br.com.cadastroit.services.api.domain.ItemPedido_;
import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.api.domain.Pedido_;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Pessoa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.ItemPedidoException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.repositories.impl.ItemPedidoRepositoryImpl;
import br.com.cadastroit.services.web.dto.ItemPedidoDTO;
import br.com.cadastroit.services.web.mapper.ItemPedidoMapper;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ItemPedidoRepository implements Serializable {

	private static final long serialVersionUID = -930708211613381479L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "ITEMPEDIDO";
	private static final String ORDER = "order";

	@Autowired
	private ItemPedidoRepositoryImpl itemPedidoRepositoryImpl;

	protected final ItemPedidoMapper itemPedidoMapper = Mappers.getMapper(ItemPedidoMapper.class);

	public Long maxId(EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(ItemPedido_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new ItemPedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory, Long pessoaId) throws TarefaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);
			TypedQuery<Long> tQuery = em.createQuery(cq.select(cb.max(from.get(ItemPedido_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new ItemPedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pessoaId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public ItemPedido findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);
			TypedQuery<ItemPedido> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(ItemPedido_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new ItemPedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public ItemPedido findById(Long pedidoId, Long pessoaId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);
			Join<ItemPedido, Pedido> joinPedido = from.join(ItemPedido_.pedido, JoinType.INNER);
			Join<Pedido, Pessoa> joinPessoa = joinPedido.join(Pedido_.pessoa, JoinType.INNER);
			Join<Pessoa, Empresa> joinEmpresa = joinPessoa.join(Pessoa_.empresa, JoinType.INNER);
			TypedQuery<ItemPedido> tQuery = null;

			if (pedidoId != null) {
				tQuery = em
						.createQuery(cq.select(from).where(cb.equal(joinEmpresa.get(Empresa_.id), pedidoId), cb.equal(from.get(ItemPedido_.id), id)));
			} else if (pessoaId != null) {
				tQuery = em
						.createQuery(cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId), cb.equal(from.get(ItemPedido_.id), id)));
			}

			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new ItemPedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long count(Long pedidoId, ItemPedidoDTO entityDto, EntityManagerFactory entityManagerFactory) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);
			predicates = this.createPredicates(pedidoId, null, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new ItemPedidoException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<ItemPedido> findAll(EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			AtomicLong quantidadeDePessoas = new AtomicLong(0L);
			AtomicLong quantidadeDeTarefas = new AtomicLong(0L);

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(ItemPedido_.id))
							: cb.asc(from.get(ItemPedido_.id))));
				}
			}

			TypedQuery<ItemPedido> tQuery = em.createQuery(cq.select(from).orderBy(orderBy));
			List<ItemPedido> departamentos = tQuery.getResultList();

			departamentos.stream().forEach(depto -> {

				// quantidadeDeTarefas.getAndAdd(depto.getTarefas().size());
				// quantidadeDePessoas.getAndAdd(depto.getPessoas().size());

				// depto.setQuantidadePessoas(quantidadeDePessoas.longValue());
				// depto.setQuantidadeTarefas(quantidadeDeTarefas.longValue());

			});

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return departamentos;
		} catch (Exception ex) {
			throw new ItemPedidoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<ItemPedido> findByFilters(ItemPedidoDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ItemPedido> cq = cb.createQuery(ItemPedido.class);
			Root<ItemPedido> from = cq.from(ItemPedido.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(ItemPedido_.id))
							: cb.asc(from.get(ItemPedido_.id))));
				}
			}

			List<Predicate> predicates = createPredicates(null, null, dto, from, cb);
			TypedQuery<ItemPedido> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ItemPedidoException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long pedidoId, Long pessoaId, ItemPedidoDTO entityDto, Root<ItemPedido> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		Join<ItemPedido, Pedido> joinPedido = from.join(ItemPedido_.pedido, JoinType.INNER);
		Join<Pedido, Pessoa> joinPessoa = joinPedido.join(Pedido_.pessoa, JoinType.INNER);

		if (pessoaId != null && pessoaId > 0L) {

			predicates.add(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId));

		} else if (pedidoId > 0L && pedidoId != null) {

			predicates.add(cb.equal(joinPedido.get(Pedido_.id), pedidoId));

		}

		if (entityDto != null) {

			// if (entityDto.getDataInicio() != null) {
			// predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), entityDto.getDataInicio()));
			//
			// }

			// if (entityDto.getDataFinal() != null) {
			// predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), entityDto.getDataFinal()));
			// }

		}

		return predicates;
	}

	public Optional<ItemPedido> findById(Long id) {

		return itemPedidoRepositoryImpl.findById(id);
	}

	public <S extends ItemPedido> S save(S entity) {

		return itemPedidoRepositoryImpl.save(entity);
	}

	public void delete(ItemPedido entity) {

		itemPedidoRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
