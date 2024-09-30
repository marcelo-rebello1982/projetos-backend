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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.api.domain.Pedido_;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Pessoa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.repositories.impl.PedidoRepositoryImpl;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.web.httpclient.PedidoClient;
import br.com.cadastroit.services.web.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PedidoRepository implements Serializable {

	private static final long serialVersionUID = -930708211613381479L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PEDIDO";
	private static final String ORDER = "order";

	@Autowired
	private PedidoRepositoryImpl pedidoRepositoryImpl;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private Gson gson;

	@Autowired
	private PedidoClient pedidoClient;

	private final ObjectMapper mapperJson = new ObjectMapper();

	protected final PedidoMapper pedidoMapper = Mappers.getMapper(PedidoMapper.class);

	public Long maxId(EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pedido> from = cq.from(Pedido.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(Pedido_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new PedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
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
			Root<Pedido> from = cq.from(Pedido.class);
			TypedQuery<Long> tQuery = em.createQuery(cq.select(cb.max(from.get(Pedido_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new PedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pessoaId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Pedido findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
			Root<Pedido> from = cq.from(Pedido.class);
			TypedQuery<Pedido> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Pedido_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Pedido findById(Long pedidoId, Long pessoaId, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
			Root<Pedido> from = cq.from(Pedido.class);
			Join<Pedido, Pessoa> joinPessoa = from.join(Pedido_.pessoa, JoinType.INNER);
			TypedQuery<Pedido> tQuery = em.createQuery(cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pedidoId));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long count(Long pessoaId, PedidoDTO entityDto, EntityManagerFactory entityManagerFactory) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Pedido> from = cq.from(Pedido.class);
			predicates = this.createPredicates(pessoaId, entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new PedidoException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Pedido> findAll(EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			AtomicLong quantidadeDePessoas = new AtomicLong(0L);
			AtomicLong quantidadeDeTarefas = new AtomicLong(0L);

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
			Root<Pedido> from = cq.from(Pedido.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Pedido_.id))
							: cb.asc(from.get(Pedido_.id))));
				}
			}

			TypedQuery<Pedido> tQuery = em.createQuery(cq.select(from).orderBy(orderBy));
			List<Pedido> departamentos = tQuery.getResultList();

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
			throw new PedidoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Pedido> findByFilters(Long pessoaId, PedidoDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
			Root<Pedido> from = cq.from(Pedido.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Pedido_.id))
							: cb.asc(from.get(Pedido_.id))));
				}
			}

			List<Predicate> predicates = this.createPredicates(pessoaId, dto, from, cb);
			TypedQuery<Pedido> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PedidoException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Predicate> createPredicates(Long pessoaId, PedidoDTO entityDto, Root<Pedido> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		if (pessoaId != 0L && pessoaId > 0L) {
			Join<Pedido, Pessoa> joinPessoa = from.join(Pedido_.pessoa, JoinType.INNER);
			predicates.add(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId));
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

	public Pedido handleUpdateAprovedPayment(Pedido entity, EntityManagerFactory entityManagerFactory) throws JsonProcessingException {

		pedidoService.validarPedido(entity);

		Pedido pedidoAtualizado = pedidoClient.handleUpdateAprovedPayment(entity.getId(), pedidoService.convertToJson(entity));

		return pedidoAtualizado;
	}

	public Optional<Pedido> findById(Long id) {

		return pedidoRepositoryImpl.findById(id);
	}

	public <S extends Pedido> S save(S entity) {

		return pedidoRepositoryImpl.save(entity);
	}

	public void delete(Pedido entity) {

		pedidoRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
