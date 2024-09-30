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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.api.domain.Parametro;
import br.com.cadastroit.services.api.domain.ParametroBoolean;
import br.com.cadastroit.services.api.domain.ParametroBoolean_;
import br.com.cadastroit.services.api.domain.ParametroChaveType;
import br.com.cadastroit.services.api.domain.ParametroEmpresa;
import br.com.cadastroit.services.api.domain.ParametroEmpresa_;
import br.com.cadastroit.services.api.domain.ParametroInteger;
import br.com.cadastroit.services.api.domain.ParametroInteger_;
import br.com.cadastroit.services.api.domain.ParametroNumber;
import br.com.cadastroit.services.api.domain.ParametroNumber_;
import br.com.cadastroit.services.api.domain.ParametroString;
import br.com.cadastroit.services.api.domain.ParametroString_;
import br.com.cadastroit.services.api.domain.Parametro_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.ParametroException;
import br.com.cadastroit.services.repositories.impl.ParametroBooleanRepository;
import br.com.cadastroit.services.repositories.impl.ParametroEmpresaRepositoryImpl;
import br.com.cadastroit.services.repositories.impl.ParametroIntegerRepository;
import br.com.cadastroit.services.repositories.impl.ParametroNumberRepository;
import br.com.cadastroit.services.repositories.impl.ParametroRepositoryImpl;
import br.com.cadastroit.services.repositories.impl.ParametroStringRepository;
import br.com.cadastroit.services.web.dto.ParametroDTO;
import br.com.cadastroit.services.web.mapper.ParametroMapper;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ParametroRepository implements Serializable {

	private static final long serialVersionUID = -6227698163572590602L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PARAMETRO";
	private static final String ORDER = "order";

	@Autowired
	private ParametroRepositoryImpl parametroRepositoryImpl;
	
	@Autowired
	private ParametroBooleanRepository parametroBooleanRepository;
	
	@Autowired
	private ParametroIntegerRepository parametroIntegerRepository;
	
	@Autowired
	private ParametroNumberRepository parametroNumberRepository;
	
	@Autowired
	private ParametroStringRepository parametroStringRepository;
	
	@Autowired
	private ParametroEmpresaRepositoryImpl parametroEmpresaRepositoryImpl;

	protected final ParametroMapper parametroMapper = Mappers.getMapper(ParametroMapper.class);

	public Long maxId(EntityManagerFactory entityManagerFactory, String cd) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}

	public Parametro findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Parametro> cq = cb.createQuery(Parametro.class);
			Root<Parametro> from = cq.from(Parametro.class);
			TypedQuery<Parametro> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Parametro_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new ParametroException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Parametro findById(Long empresaId, Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Parametro> cq = cb.createQuery(Parametro.class);
			Root<Parametro> from = cq.from(Parametro.class);
			TypedQuery<Parametro> tQuery = em
					.createQuery(cq.select(from).where(cb.equal(from.get(Parametro_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new ParametroException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Long maxId(EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Parametro> from = cq.from(Parametro.class);
			TypedQuery<Long> result = em.createQuery(cq.select(cb.max(from.get(Parametro_.id))));
			return result.getSingleResult();
		} catch (Exception ex) {
			throw new ParametroException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", null));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long count(ParametroDTO dto, Long empresaId, EntityManagerFactory entityManagerFactory) throws ParametroException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Parametro> from = cq.from(Parametro.class);
			predicates = this.createPredicates(dto, empresaId, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new ParametroException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Parametro> obterPorChave(ParametroDTO dto, Long empresaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams)
			throws ParametroException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Parametro> cq = cb.createQuery(Parametro.class);
			Root<Parametro> from = cq.from(Parametro.class);
			TypedQuery<Parametro> tQuery;

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Parametro_.id))
							: cb.asc(from.get(Parametro_.id))));
				}
			}

			if (empresaId != null) {
				tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Parametro_.id), empresaId)).orderBy(orderBy));
			} else {
				tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Parametro_.chave), dto.getChave())).orderBy(orderBy));
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ParametroException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Parametro> findAll(Long empresaId, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws ParametroException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Parametro> cq = cb.createQuery(Parametro.class);
			Root<Parametro> from = cq.from(Parametro.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Parametro_.id))
							: cb.asc(from.get(Parametro_.id))));
				}
			}

			TypedQuery<Parametro> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Parametro_.id), empresaId)).orderBy(orderBy));

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ParametroException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<Parametro> findByFilters(Long empresaId, ParametroDTO dto, EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int max)
			throws ParametroException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Parametro> cq = cb.createQuery(Parametro.class);
			Root<Parametro> from = cq.from(Parametro.class);

			requestParams.entrySet().stream().filter(entry -> entry.getKey().startsWith(ORDER)).map(e -> {
				return (e.getValue() == null || e.getValue().equals("desc")) ? cb.desc(from.get(Parametro_.id)) : cb.asc(from.get(Parametro_.id));

			}).forEach(orderBy::add);

			List<Predicate> predicates = this.createPredicates(dto, empresaId, from, cb);
			TypedQuery<Parametro> tQuery = em.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new ParametroException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	
	
	
	
	public List<ParametroDTO> obterParametros() {
		
		List<ParametroDTO> parametros = new ArrayList<>();

		List<ParametroBoolean> booleans = this. parametroBooleanRepository.findAll();
		List<ParametroInteger> integers = this.parametroIntegerRepository.findAll();
		List<ParametroNumber> numbers = this.parametroNumberRepository.findAll();
		List<ParametroString> strings = this.parametroStringRepository.findAll();


		if (CollectionUtils.isNotEmpty(booleans)) {
			parametros.addAll(this.parametroMapper.booleanToDto(booleans));
		}

		if (CollectionUtils.isNotEmpty(integers)) {
			parametros.addAll(this.parametroMapper.integerToDto(integers));
		}

		if (CollectionUtils.isNotEmpty(numbers)) {
			parametros.addAll(this.parametroMapper.numberToDto(numbers));
		}

		if (CollectionUtils.isNotEmpty(strings)) {
			parametros.addAll(this.parametroMapper.stringToDto(strings));
		}

		return parametros;
	}
	
	public List<ParametroDTO> obterParametros(List<ParametroChaveType> chaves) {

		List<ParametroDTO> parametros = new ArrayList<>();

		for (ParametroChaveType chave : chaves) {

			List<ParametroDTO> param = this.obterParametros(chave);

			if (CollectionUtils.isNotEmpty(param))
				parametros.addAll(param);
		}

		return parametros;
	}
	
	@SuppressWarnings("serial")
	public List<ParametroDTO> obterParametros(final ParametroChaveType chave) {
		

		switch (chave.getTipo()) {

			case BOOLEAN:
				Specification<ParametroBoolean> specB = new Specification<ParametroBoolean>() {

					@Override
					public Predicate toPredicate(Root<ParametroBoolean> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

						Predicate predicate = cb.conjunction();
						predicate.getExpressions().add(cb.equal(root.get(ParametroBoolean_.chave), chave));

						return predicate;
					}
				};

				List<ParametroBoolean> paramsB = this.parametroBooleanRepository.findAll(specB);

				return this.parametroMapper.booleanToDto(paramsB);

			case INTEGER:
				Specification<ParametroInteger> specI = new Specification<ParametroInteger>() {

					@Override
					public Predicate toPredicate(Root<ParametroInteger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

						Predicate predicate = cb.conjunction();
						predicate.getExpressions().add(cb.equal(root.get(ParametroInteger_.chave), chave));

						return predicate;
					}
				};

				List<ParametroInteger> paramsI = this.parametroIntegerRepository.findAll(specI);

				return this.parametroMapper.integerToDto(paramsI);

			case NUMBER:
				Specification<ParametroNumber> specN = new Specification<ParametroNumber>() {

					@Override
					public Predicate toPredicate(Root<ParametroNumber> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

						Predicate predicate = cb.conjunction();
						predicate.getExpressions().add(cb.equal(root.get(ParametroNumber_.chave), chave));

						return predicate;
					}
				};

				List<ParametroNumber> paramsN = this.parametroNumberRepository.findAll(specN);

				return this.parametroMapper.numberToDto(paramsN);

			case STRING:
				Specification<ParametroString> specS = new Specification<ParametroString>() {

					@Override
					public Predicate toPredicate(Root<ParametroString> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

						Predicate predicate = cb.conjunction();
						predicate.getExpressions().add(cb.equal(root.get(ParametroString_.chave), chave));

						return predicate;
					}
				};

				List<ParametroString> paramsS = this.parametroStringRepository.findAll(specS);

				return this.parametroMapper.stringToDto(paramsS);

			default:
				break;

		}

		return new ArrayList<>();
	}
	
	public ParametroDTO obterPorChaveAndEmpresa(Long empresaId, ParametroChaveType chave, EntityManagerFactory entityManagerFactory) {

		ParametroEmpresa parametroEmpresa = this.findByChave(empresaId, chave, entityManagerFactory);

		if (parametroEmpresa == null)
			return null;

		ParametroDTO dto = ParametroDTO.builder()
				.id(parametroEmpresa.getParametro().getId())
				.chave(parametroEmpresa.getParametro().getChave())
				.valor(parametroEmpresa.getParametro().getValor())
				.build();

		return dto;
	}
	
	public List<ParametroDTO> obterPorChavesAndEmpresa(Long empresaId, List<ParametroChaveType> chaves, EntityManagerFactory entityManagerFactory) {

		List<ParametroDTO> dtos = new ArrayList<>();
		List<ParametroEmpresa> parametros = this.findByChave(empresaId, chaves, entityManagerFactory);

		for (ParametroEmpresa parametroEmpresa : parametros) {

			ParametroDTO dto = ParametroDTO.builder()
					.id(parametroEmpresa.getParametro().getId())
					.chave(parametroEmpresa.getParametro().getChave())
					.valor(parametroEmpresa.getParametro().getValor())
					.build();

			dtos.add(dto);
		}

		return dtos;
	}
	
	public ParametroEmpresa findByChave(Long empresaId, ParametroChaveType chave, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ParametroEmpresa> cq = cb.createQuery(ParametroEmpresa.class);
			Root<ParametroEmpresa> from = cq.from(ParametroEmpresa.class);
			Join<ParametroEmpresa, Empresa> joinEmpresa = from.join(ParametroEmpresa_.empresa, JoinType.INNER);
			Join<ParametroEmpresa, Parametro> joinParametro = from.join(ParametroEmpresa_.parametro, JoinType.INNER);
			TypedQuery<ParametroEmpresa> tQuery = em.createQuery(
					cq.select(from).where(cb.equal(joinEmpresa.get(Empresa_.id), empresaId), cb.equal(joinParametro.get(Parametro_.chave), chave)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new ParametroException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", empresaId));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<ParametroEmpresa> findByChave(Long empresaId, List<ParametroChaveType> chaves, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ParametroEmpresa> cq = cb.createQuery(ParametroEmpresa.class);
			Root<ParametroEmpresa> from = cq.from(ParametroEmpresa.class);
			List<Predicate> predicates = this.createPredicates(empresaId, chaves, from, cb);
			TypedQuery<ParametroEmpresa> tQuery = em
					.createQuery(cq.select(from).where(predicates.toArray(new Predicate[] {})).orderBy(cb.desc(from.get(ParametroEmpresa_.id))));
			return tQuery.getResultList();
		} catch (NoResultException ex) {
			throw new ParametroException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", empresaId));
		} finally {
			em.clear();
			em.close();
		}
	}
		
	public List<Predicate> createPredicates(Long empresaId, List<ParametroChaveType> chaves, Root<ParametroEmpresa> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();
		
		Join<ParametroEmpresa, Empresa> joinEmpresa = from.join(ParametroEmpresa_.empresa, JoinType.INNER);
		Join<ParametroEmpresa, Parametro> joinParametro = from.join(ParametroEmpresa_.parametro, JoinType.INNER);
		In<ParametroChaveType> in = cb.in(joinParametro.get(Parametro_.chave));

		for (ParametroChaveType chave : chaves) {
			predicates.add(cb.and(in.value(chave)));
		}

		predicates.add((cb.equal(joinEmpresa.get(Empresa_.id), empresaId)));

		return predicates;
	}

	public List<Predicate> createPredicates(ParametroDTO entityDto, Long empresaId, Root<Parametro> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		if (entityDto != null) {

		    	checkIsNull(entityDto.getChave()).ifPresent(field -> predicates.add(cb.equal(from.get(Parametro_.chave), field)));
		}

		return predicates;

	}
	
	public Optional<Parametro> findById(Long id) {

		return parametroRepositoryImpl.findById(id);
	}

	public <S extends Parametro> S save(S entity) {

		return parametroRepositoryImpl.save(entity);
	}

	public void delete(Parametro entity) {

		parametroRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
