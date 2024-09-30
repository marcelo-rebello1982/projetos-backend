package br.com.cadastroit.services.api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.api.domain.DesifCadPcCosif;
import br.com.cadastroit.services.api.domain.DesifCadPcCosif_;
import br.com.cadastroit.services.api.domain.DesifPlanoConta;
import br.com.cadastroit.services.api.domain.DesifPlanoConta_;
import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.exceptions.DesifPlanoContaException;
import br.com.cadastroit.services.web.dto.DesifPlanoContaDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class DesifPlanoContaService {

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "DESIF_PLANO_CONTA";
	private static final String ORDER = "order";

	public List<DesifPlanoConta> findAll(Long empresaId, EntityManagerFactory entityManagerFactory,
			Map<String, String> requestParams, int page, int length) throws DesifPlanoContaException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<DesifPlanoConta> cq = cb.createQuery(DesifPlanoConta.class);
			Root<DesifPlanoConta> from = cq.from(DesifPlanoConta.class);

			orderBy.add((requestParams.get(ORDER) == null || requestParams.get(ORDER).equals("desc")
					? cb.desc(from.get(DesifPlanoConta_.id))
					: cb.asc(from.get(DesifPlanoConta_.id))));

			TypedQuery<DesifPlanoConta> tQuery = entityManager.createQuery(
					cq.select(from).where(cb.equal(joinEmpresa(from).get(Empresa_.id), empresaId)).orderBy(orderBy));
			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new DesifPlanoContaException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			entityManager.clear();
			entityManager.close();
		}
	}

	public List<DesifPlanoConta> findByFilters(Long empresaId, DesifPlanoContaDto entityDto,
			EntityManagerFactory entityManagerFactory,
			@RequestParam(required = false) Map<String, String> requestParams, int page, int max)
			throws DesifPlanoContaException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<DesifPlanoConta> cq = cb.createQuery(DesifPlanoConta.class);
			Root<DesifPlanoConta> from = cq.from(DesifPlanoConta.class);
			orderBy.add((requestParams.get(ORDER) == null || requestParams.get(ORDER).equals("desc")
					? cb.desc(from.get(DesifPlanoConta_.id))
					: cb.asc(from.get(DesifPlanoConta_.id))));
			predicates = this.createPredicates(empresaId, entityDto, from, cb);
			TypedQuery<DesifPlanoConta> tQuery = entityManager
					.createQuery(cq.select(from).where(predicates.stream().toArray(Predicate[]::new)).orderBy(orderBy));
			if (page != 0 && max != 0) {
				tQuery.setFirstResult((page - 1) * max);
				tQuery.setMaxResults(max);
			}
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new DesifPlanoContaException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
		} finally {
			entityManager.clear();
			entityManager.close();
		}
	}

	public List<Predicate> createPredicates(Long empresaId, DesifPlanoContaDto entityDto, Root<DesifPlanoConta> from,
			CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		Join<DesifPlanoConta, DesifCadPcCosif> joinDesifCadPcCosif = from.join(DesifPlanoConta_.desifCadPcCosIf,
				JoinType.INNER);
		predicates.add(
				cb.equal(from.get(DesifPlanoConta_.desifCadPcCosIf), joinDesifCadPcCosif.get(DesifCadPcCosif_.id)));

		if (entityDto.getCodCta() != null) {
			predicates.add(cb.like(from.get(DesifPlanoConta_.codCta), "%" + entityDto.getCodCta() + "%"));
		}

		if (entityDto.getDmSituacao() != null) {
			predicates.add(cb.equal(from.get(DesifPlanoConta_.dmSituacao), entityDto.getDmSituacao()));
		}

		if (entityDto.getContaReduzidaValues() != null && entityDto.getContaReduzidaValues().length > 0) {
			In<Integer> inContaReduzidaValues = cb.in(from.get(DesifPlanoConta_.contaReduzida).as(Integer.class));
			for (Integer contaReduzidaValue : entityDto.getContaReduzidaValues()) {
				inContaReduzidaValues.value(contaReduzidaValue);
			}
			predicates.add(cb.and(inContaReduzidaValues));
		}

		if (empresaId != null) {
			predicates.add(cb.equal(joinEmpresa(from).get(Empresa_.id), empresaId));
		}

		return predicates;
	}

	public Long countFindAll(Long empresaId, EntityManagerFactory entityManagerFactory)
			throws DesifPlanoContaException {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<DesifPlanoConta> from = cq.from(DesifPlanoConta.class);
			return em
					.createQuery(
							cq.select(cb.count(from)).where(cb.equal(joinEmpresa(from).get(Empresa_.id), empresaId)))
					.getSingleResult();
		} catch (Exception ex) {
			throw new DesifPlanoContaException(String.format(MODE, "CountFindAll", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	private Join<DesifPlanoConta, Empresa> joinEmpresa(Root<DesifPlanoConta> from) {
		return from.join(DesifPlanoConta_.empresa, JoinType.INNER);
	}

	public Long countByFilters(Long empresaId, DesifPlanoContaDto entityDto, EntityManagerFactory entityManagerFactory)
			throws DesifPlanoContaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<DesifPlanoConta> from = cq.from(DesifPlanoConta.class);
			predicates = this.createPredicates(empresaId, entityDto, from, cb);
			return em.createQuery(cq.select(cb.count(from)).where(predicates.stream().toArray(Predicate[]::new)))
					.getSingleResult();
		} catch (Exception ex) {
			throw new DesifPlanoContaException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public DesifPlanoConta retornaObjetoDesifPlanoConta(String message)
			throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		DesifPlanoConta desifPlanoConta = mapper.readValue(message, DesifPlanoConta.class);
		return desifPlanoConta;
	}
	
	protected String converterCampoDm(Integer value) {
		Map<Integer, String> status = new HashMap<>();
		status.put(0, "0-Não validado");
		status.put(1, "1-Validado");
		status.put(2, "2-Erro de validação");
		return status.entrySet().stream().filter(p -> p.getKey().equals(value)).findFirst().get().getValue();
	}
}