package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.repositories.impl.ParametroEmpresaRepositoryImpl;
import br.com.cadastroit.services.web.mapper.EnderecoMapper;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ParamentroEmpresaRepository implements Serializable {

	private static final long serialVersionUID = -4955956379049741951L;
	
	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PARAMETROEMPRESA";
	private static final String ORDER = "order";

	@Autowired
	private ParametroEmpresaRepositoryImpl parametroEmpresaRepositoryImpl;

	protected final EnderecoMapper enderecoMapper = Mappers.getMapper(EnderecoMapper.class);

	public Long maxId(EntityManagerFactory entityManagerFactory, String cd) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}
	
//	public List<ParametroEmpresa> obterParametrosPorEntidade(EntityManagerFactory entityManagerFactory, Long entidade, List<ParametroChaveType> chaves) {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//
//			List<Order> orderBy = new ArrayList<>();
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ParametroEmpresa> cq = cb.createQuery(ParametroEmpresa.class);
//			Root<ParametroEmpresa> from = cq.from(ParametroEmpresa.class);
//			Join<ParametroEmpresa, Empresa> joinEmpresa = from.join(ParametroEmpresa_.empresa, JoinType.INNER);
//
//			for (Entry<String, String> entry : requestParams.entrySet()) {
//				if (entry.getKey().startsWith(ORDER)) {
//					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Endereco_.id))
//							: cb.asc(from.get(Endereco_.id))));
//				}
//			}
//
//			TypedQuery<Endereco> tQuery = em.createQuery(cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId)).orderBy(orderBy));
//
//			tQuery.setFirstResult((page - 1) * length);
//			tQuery.setMaxResults(length);
//			return tQuery.getResultList();
//		} catch (Exception ex) {
//			throw new EnderecoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}

//	public Endereco findById(Long id, EntityManagerFactory entityManagerFactory) {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Endereco> cq = cb.createQuery(Endereco.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//			TypedQuery<Endereco> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Endereco_.id), id)));
//			return tQuery.getSingleResult();
//		} catch (NoResultException ex) {
//			throw new EnderecoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public Endereco findById(Long pessoaId, Long id, EntityManagerFactory entityManagerFactory) {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Endereco> cq = cb.createQuery(Endereco.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//			Join<Endereco, Pessoa> joinPessoa = from.join(Endereco_.pessoa, JoinType.INNER);
//			TypedQuery<Endereco> tQuery = em
//					.createQuery(cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId), cb.equal(from.get(Endereco_.id), id)));
//			return tQuery.getSingleResult();
//		} catch (NoResultException ex) {
//			throw new EnderecoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public Long maxId(EntityManagerFactory entityManagerFactory) {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(Endereco_.id))));
//			return result.getSingleResult();
//		} catch (Exception ex) {
//			throw new EnderecoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public Long maxId(EntityManagerFactory entityManagerFactory, Long pessoaId) throws EnderecoException {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//			Join<Endereco, Pessoa> joinPessoa = from.join(Endereco_.pessoa, JoinType.INNER);
//			TypedQuery<Long> tQuery = em.createQuery(cq.select(cb.max(from.get(Endereco_.id))).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId)));
//			return tQuery.getSingleResult();
//		} catch (Exception ex) {
//			throw new EnderecoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pessoaId));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public Long count(Long pessoaId, EnderecoDto entityDto, EntityManagerFactory entityManagerFactory) throws EnderecoException {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		List<Predicate> predicates = new ArrayList<>();
//
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//			predicates = this.createPredicates(pessoaId, entityDto != null ? entityDto : null, from, cb);
//
//			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();
//
//		} catch (Exception ex) {
//			throw new EnderecoException(String.format(MODE, "count", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public List<Endereco> findAll(Long pessoaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
//			throws EnderecoException {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//
//			List<Order> orderBy = new ArrayList<>();
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Endereco> cq = cb.createQuery(Endereco.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//			Join<Endereco, Pessoa> joinPessoa = from.join(Endereco_.pessoa, JoinType.INNER);
//
//			for (Entry<String, String> entry : requestParams.entrySet()) {
//				if (entry.getKey().startsWith(ORDER)) {
//					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Endereco_.id))
//							: cb.asc(from.get(Endereco_.id))));
//				}
//			}
//
//			TypedQuery<Endereco> tQuery = em.createQuery(cq.select(from).where(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId)).orderBy(orderBy));
//
//			tQuery.setFirstResult((page - 1) * length);
//			tQuery.setMaxResults(length);
//			return tQuery.getResultList();
//		} catch (Exception ex) {
//			throw new EnderecoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public List<Endereco> findByFilters(Long pessoaId, EnderecoDto dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
//			throws EnderecoException {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//
//		try {
//
//			List<Order> orderBy = new ArrayList<>();
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Endereco> cq = cb.createQuery(Endereco.class);
//			Root<Endereco> from = cq.from(Endereco.class);
//
//			requestParams.entrySet().stream().filter(entry -> entry.getKey().startsWith(ORDER)).map(e -> {
//				return (e.getValue() == null || e.getValue().equals("desc")) ? cb.desc(from.get(Endereco_.id)) : cb.asc(from.get(Endereco_.id));
//
//			}).forEach(orderBy::add);
//
//			List<Predicate> predicates = createPredicates(pessoaId, dto, from, cb);
//			TypedQuery<Endereco> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
//			if (page != 0 && max != 0) {
//				tQuery.setFirstResult((page - 1) * max);
//				tQuery.setMaxResults(max);
//			}
//			return tQuery.getResultList();
//		} catch (Exception ex) {
//			throw new EnderecoException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//
//	public List<Predicate> createPredicates(Long pessoaId, EnderecoDto entityDto, Root<Endereco> from, CriteriaBuilder cb) {
//
//		List<Predicate> predicates = new ArrayList<>();
//
//		Join<Endereco, Pessoa> joinPessoa = from.join(Endereco_.pessoa, JoinType.INNER);
//
//		predicates.add(cb.equal(joinPessoa.get(Pessoa_.id), pessoaId));
//
//		if (entityDto != null) {
//
//			// checkIsNull(entityDto.getNome()).ifPresent(field ->
//			// predicates.add(cb.like(from.get(Endereco_.complemento).as(String.class), "%" + field + "%")));
//			//
//			// if (entityDto.getDataInicio() != null) {
//			// predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), entityDto.getDataInicio()));
//			//
//			// }
//			//
//			// if (entityDto.getDataFinal() != null) {
//			// predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), entityDto.getDataFinal()));
//			// }
//
//		}
//
//		return predicates;
//	}
//
//	public Optional<Endereco> findById(Long id) {
//
//		return enderecoRepositoryImpl.findById(id);
//	}
//
//	public <S extends Endereco> S save(S entity) {
//
//		return enderecoRepositoryImpl.save(entity);
//	}
//
//	public void delete(Endereco entity) {
//
//		enderecoRepositoryImpl.delete(entity);
//	}
//
//	public <T> Optional<T> checkIsNull(T field) {
//
//		return Optional.ofNullable(field);
//	}

}
