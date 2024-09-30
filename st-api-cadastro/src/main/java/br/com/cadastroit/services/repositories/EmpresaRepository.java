package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.DepartamentoException;
import br.com.cadastroit.services.exceptions.EmpresaException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.impl.EmpresaRepositoryImpl;
import br.com.cadastroit.services.web.dto.EmpresaDTO;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class EmpresaRepository implements Serializable {

	private static final long serialVersionUID = -6226504130558319561L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "EMPRESA";
	private static final String ORDER = "order";
	
	@Autowired
	private EmpresaRepositoryImpl empresaRepositoryImpl;

	public Long maxIdJPQl(EntityManagerFactory entityManagerFactory) throws SQLException {

		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
		return ((Number) query.getSingleResult()).longValue();
	}
	
	public Long maxId(EntityManagerFactory entityManagerFactory) throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Empresa> from = cq.from(Empresa.class);
			TypedQuery<Long> tQuery = em
					.createQuery(cq.select(cb.max(from.get(Empresa_.id))));
			return tQuery.getSingleResult();
		} catch (Exception ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", ""));
		} finally {
			em.clear();
			em.close();
		}
	}

	public Empresa findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);
			Root<Empresa> from = cq.from(Empresa.class);
			TypedQuery<Empresa> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Empresa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new DepartamentoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long count(EmpresaDTO entityDto, EntityManagerFactory entityManagerFactory) throws EmpresaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		List<Predicate> predicates = new ArrayList<>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Empresa> from = cq.from(Empresa.class);
			predicates = this.createPredicates(entityDto != null ? entityDto : null, from, cb);

			return em.createQuery(cq.select(cb.count(from)).where(predicates.toArray(new Predicate[] {}))).getSingleResult();

		} catch (Exception ex) {
			throw new EmpresaException(String.format(MODE, "count", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public List<Empresa> findAll(EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, int page, int length)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {

			AtomicLong quantidadeDePessoas = new AtomicLong(0L);
			AtomicLong quantidadeDeTarefas = new AtomicLong(0L);
			
			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);
			Root<Empresa> from = cq.from(Empresa.class);

			for (Entry<String, String> entry : requestParams.entrySet()) {
				if (entry.getKey().startsWith(ORDER)) {
					orderBy.add((entry.getValue() == null || entry.getValue().equals("desc") ? cb.desc(from.get(Empresa_.id))
							: cb.asc(from.get(Empresa_.id))));
				}
			}
			
			TypedQuery<Empresa> tQuery = em.createQuery(cq.select(from).orderBy(orderBy));
			List<Empresa> empresas = tQuery.getResultList();
			
			empresas.stream().forEach( depto -> {
				
				
				
			});

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return empresas;
		} catch (Exception ex) {
			throw new DepartamentoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	// monitoramento NFS - OracleAPI
	
//	 @SuppressWarnings({ "static-access", "unchecked"}) 
//		public List<ViewTotalItemNfServ> vDashTotItemNfServ(Filters filters, Map<String, String> requestParams, int page,
//	                                                        int length, EntityManagerFactory entityManagerFactory) throws SQLException {
//
//	        UtilDate utilDate = new UtilDate();
//	        AtomicBoolean order = new AtomicBoolean(true);
//	        AtomicReference<List<ViewTotalItemNfServ>> itemNfServ = new AtomicReference<>(new ArrayList<>());
//	        EntityManager em = entityManagerFactory.createEntityManager();
//
//	        try {
//
//	            requestParams.entrySet().stream()
//	                    .filter(entry -> entry.getKey()
//	                            .equalsIgnoreCase("ORDER"))
//	                    .forEach(entry -> {
//	                        if (entry.getValue() == null || entry.getValue().equals("desc")) {
//	                            order.set(true);
//	                        } else {
//	                            order.set(false);
//	                        }
//	                    });
//
//	            if (filters.getSiglaEstado() == null) {
//
//	            }
//	            
//	            StringBuilder toSelect = new StringBuilder();
//	            toSelect.append("V.NFSERV_ID, V.MULTORG_CD, V.CNPJ_EMIT, V.IM_EMIT, V.SERIE, V.NRO_NF, ")
//	                    .append("V.DT_EMISS, V.HR_EMISS, V.DT_EXE_SERV, V.DM_ST_PROC, V.DESCR_CIDADE, V.UF,")
//	                    .append("V.IBGE_CIDADE, V.DM_DB_DESTINO, V.NRO_DOC_TOMADOR, V.NOME_TOMADOR, ")
//	                    .append("V.VL_TOT_SERV, V.VL_TOT_DESCONTO, V.VL_TOT_RETIDO, V.VL_TOT_ISS, V.VL_TOTAL_NF FROM ");
//
//	            
//	            StringBuilder sqlQryString = this.commonsRepository.getUtilities().createQuery(false, toSelect.toString(), "V_DASH_TOT_ITEM_NFSERV", 
//	                            this.commonsRepository.createPredicates(filters != null ?
//	                                            true : false, // no predicates,apenas confere  se o filtro é null.
//	                                    filters))
//	                    .append(" ORDER BY V.DT_EMISS")
//	                    .append(order.get() == true ?
//	                            " DESC" : " ASC");
//
//	            Query query = em.createNativeQuery(sqlQryString.toString());
//	            query.setFirstResult((page - 1) * length);
//	            query.setMaxResults(length);
//
//				List<Object[]> result = query.getResultList();
//
//	            result.stream()
//	            
//	                    .map( obj ->
//
//	                            ViewTotalItemNfServ.builder()
//
//	                                    .nfServId(obj[0].toString())
//	                                    .multOrgCd(obj[1].toString())
//	                                    .cnpjEmit(obj[2].toString())
//	                                    .imEmit(obj[3].toString())
//	                                    .serie(obj[4].toString())
//	                                    .nroNf(Integer.valueOf(obj[5].toString()))
//	                                    .dtEmissao(obj[6].toString())
//	                                    .hrEmissao(obj[7] != null ? obj[7].toString() : null)
//	                                    .dtExeServ(utilDate.convertStringDateToTimestamp(obj[8].toString(), "yyyy-MM-dd HH:mm:ss.S"))
//	                                    .dmStProc(new BigDecimal(obj[9].toString()))
//	                                    .descrCidade(obj[10].toString().toUpperCase())
//	                                    .uf(obj[11].toString())
//	                                    .ibgeCidade(obj[12].toString())
//	                                    .dmDbDestino(Integer.valueOf(obj[13].toString()))
//	                                    .nroDocTomador(obj[14] != null ? obj[14].toString() : null)
//	                                    .nomeTomador(obj[15] != null ? obj[15].toString().toUpperCase() : null)
//	                                    .vlTotServ(new Double(obj[16].toString()))
//	                                    .vlTotDesconto(new Double(obj[17].toString()))
//	                                    .vlTotRetido(new Double(obj[18].toString()))
//	                                    .vlTotIss(new Double(obj[19].toString()))
//	                                    .vlTotalNf(new Double(obj[20].toString()))
//	                                    .build())
//
//	                    .forEach( obj ->
//	                    
//	                            itemNfServ.get()
//	                                    .add(obj));
//
//	            return itemNfServ.get();
//
//	        } catch (Exception ex) {
//	            throw new NfServException(String.format(MODE, "V_DASH_TOT_ITEM_NFSERV", OBJECT, ex.getMessage()));
//	        } finally {
//	            em.clear();
//	            em.close();
//	        }
//	    }
	
	public List<Predicate> createPredicates(EmpresaDTO entityDto, Root<Empresa> from, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<>();

		if (entityDto != null) {

		//	if (entityDto.getDataInicio() != null) {
		//		predicates.add(cb.greaterThanOrEqualTo(joinTarefa.get(Tarefa_.dataInicio), entityDto.getDataInicio()));
        //
		//	}

		//	if (entityDto.getDataFinal() != null) {
		//		predicates.add(cb.lessThanOrEqualTo(joinTarefa.get(Tarefa_.dataFinal), entityDto.getDataFinal()));
		//	}
			
		}
		
		return predicates;
	}
	
	public Optional<Empresa> findById(Long id) {

		return empresaRepositoryImpl.findById(id);
	}

	public <S extends Empresa> S save(S entity) {

		return empresaRepositoryImpl.save(entity);
	}

	public void delete(Empresa entity) {

		empresaRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}
}
