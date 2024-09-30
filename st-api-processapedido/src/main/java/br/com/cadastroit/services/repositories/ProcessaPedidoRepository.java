package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import br.com.cadastroit.services.api.db.jdbc.PostgresJdbc;
import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.api.domain.Pedido_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.api.services.ProcessaPedidoService;
import br.com.cadastroit.services.common.util.UtilDate;
import br.com.cadastroit.services.exceptions.BusinessException;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.exceptions.ProcessaPedidoException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.repositories.impl.ProcessaPedidoRepositoryImpl;
import br.com.cadastroit.services.web.dto.FiltersDTO;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.web.dto.PedidoInformacoesEnvioDTO;
import br.com.cadastroit.services.web.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessaPedidoRepository implements Serializable {

	private static final long serialVersionUID = 4749243510185845361L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PEDIDO";
	private static final String ORDER = "order";

	@Autowired
	private ProcessaPedidoRepositoryImpl pedidoRepositoryImpl;

	@Autowired
	private ProcessaPedidoService processaPedidoService;

	@Autowired
	private Gson gson;
	
	@Autowired
	private PostgresJdbc postgresJdbc;

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
	
	
	public Pedido recuperaPedidoPessoa(Long id, Long pessoaId, EntityManagerFactory entityManagerFactory) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		try {
			
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("SELECT e FROM EmpresaDto e ")
					.append(" INNER JOIN e.pessoa p ")
					.append(" INNER JOIN p.fisica f ")
					.append(" WHERE f.numCpf = :numCpf")
					.append(" AND f.digCpf = :numDig");
			
			TypedQuery<Pedido> query = entityManager.createQuery(sqlBuilder.toString(), Pedido.class);

			query.setParameter("numCpf", id);
			query.setParameter("numDig", pessoaId);

			return query.getSingleResult();

		} catch (NoResultException ex) {
			return null;
		} catch (NonUniqueResultException ex) {
			throw new BusinessException("Mais de um objeto de pessoa fisica devolvido para consulta", ex);
		}
	}

	public Pedido findById(Long pedidoId, Long pessoaId, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
			Root<Pedido> from = cq.from(Pedido.class);
			TypedQuery<Pedido> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Pedido_.id), pedidoId)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PedidoException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", pedidoId));
		} finally {
			em.clear();
			em.close();
		}
	}
	
	public Long count(Long pessoaId , Map<String, String> requestParams, int page, int length, boolean rowCount, FiltersDTO filters, EntityManager... managers) throws PedidoException {

	        try {

	            AtomicBoolean order = new AtomicBoolean(true);
	            
	            requestParams.entrySet().stream()
	                    .filter(entry -> entry.getKey()
	                            .equalsIgnoreCase("ORDER"))
	                    .forEach(entry -> {
	                        if (entry.getValue() == null || entry.getValue().equals("desc")) {
	                            order.set(true);
	                        } else {
	                            order.set(false);
	                        }
	                    });

	            StringBuilder sqlRowCount = this.createQuery(rowCount, "PEDIDO", "", this.createPredicates(filters != null ? true : false, filters));
	            
	            sqlRowCount.append(rowCount ? "" :
	                            " ORDER BY P.DT_EMISS ")
	                    .append(rowCount ? ";" : order.get() == true
	                            ? "DESC" : "ASC");

	            Query tRows = managers[1].createNativeQuery(sqlRowCount.toString());
	            Long row = Long.valueOf(tRows.getSingleResult().toString());
	            return row;
	        } catch (Exception ex) {
	            throw new PedidoException(String.format(MODE, "VDASHTOTITEMNFSERV", OBJECT, ex.getMessage()));
	        } finally {
	        	if (managers[1].isOpen()) {
	        		managers[1].clear();
	        		managers[1].close();
	        	}
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

			tQuery.setFirstResult((page - 1) * length);
			tQuery.setMaxResults(length);
			return tQuery.getResultList();
		} catch (Exception ex) {
			throw new PedidoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} finally {
			em.clear();
			em.close();
		}
	}

	public List<PedidoInformacoesEnvioDTO> findAll(EntityManagerFactory entityManagerFactory, Map<String, String> requestParams, Long pessoaId, int page, int length, FiltersDTO filters)
			throws PessoaException {

		
		AtomicBoolean order = new AtomicBoolean(true);
		AtomicReference<List<PedidoInformacoesEnvioDTO>> pedidosResumo = new AtomicReference<>(new ArrayList<>());
		
		try {

			// JDBC / native query monitoramento-oracle-api NfServRepository
			
			final Connection connection = postgresJdbc.getConnection();
			
	        EntityManager em = entityManagerFactory.createEntityManager();

			
			StringBuilder toSelect = new StringBuilder();
				toSelect.append("P.NRO_PEDIDO, ");
		        toSelect.append("P.APROVADO, ");
		        toSelect.append("FROM ");

			StringBuilder sqlQryString = this
					.createQuery(false, toSelect.toString(), "PEDIDO", this.createPredicates(filters != null ? true : false, filters))
					.append(" ORDER BY P.DATACOMPRA")
					.append(order.get() == true ? " DESC" : " ASC");
			
            Query query = em.createNativeQuery(sqlQryString.toString());
            
            @SuppressWarnings({ "static-access", "unchecked"}) 
            List<Object[]> result = query.getResultList();
            
			result.stream()

					.map(obj ->

					PedidoInformacoesEnvioDTO.builder()
						.nroPedido(obj[0].toString()).build())

					.forEach(obj -> pedidosResumo.get().add(obj));

			return pedidosResumo.get();
			
//			String pause = sqlQryString.toString();
//			
//			  try {
//				  
//				  try (Statement statement = connection.createStatement();
//						  ResultSet rSet = statement.executeQuery(sqlQryString.toString())) {
//					  while (rSet.next()) {
//						  
//						  PedidoInformacoesEnvioDTO pedidoInformacoes = PedidoInformacoesEnvioDTO.builder()
//								  .nroPedido(rSet.getString("NRO_PEDIDO"))
//								  .nroPedido(rSet.getString("APROVADO"))
//								  .build();
//						  
//						  pedidosResumo.get().add(pedidoInformacoes);					  }
//				  }
//				} catch (Exception ex) {
//					throw new ProcessaPedidoException(String.format(MODE, "PEDIDO", OBJECT, ex.getMessage()));
//				} finally {
//					if (connection != null && !connection.isClosed()) {
//						connection.close();
//					}
//				}
//		
//			return pedidosResumo.get();
			
		} catch (Exception ex) {
			throw new ProcessaPedidoException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
		} 
	}
	
	public LinkedHashMap<String, String> createPredicates(boolean objectNotNull, FiltersDTO filters) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		String fDateBegin = "";
		String fDateEnd = "";

		if (filters.getDtEmissIni() != null ) fDateBegin = UtilDate.toDateString(UtilDate.addMinHourToDate(UtilDate.toDateString(filters.getDtEmissIni())), "dd/MM/yyyy");
		else return null;
		
		if (filters.getDtEmissFim() != null ) fDateEnd = UtilDate.toDateString(UtilDate.addMaxHourToDate(UtilDate.toDateString(filters.getDtEmissFim())), "dd/MM/yyyy");
		else return null;

		// this.putValuesInMap(map, filters.getNroPedido(), "P.NRO_PEDIDO = '");

		Optional.ofNullable(filters).map(FiltersDTO::getPessoa).ifPresent(entity -> {

			this.putValuesInMap(map, entity.getNome(), "P.UF = '");

		});

//		this.putValuesInMap(map, filters.getDtEmissIni(), "P.DT_EMISS >= TO_DATE ('", fDateBegin.concat(" 23:59:59','DD/MM/YYYY HH24:MI:SS')"));
//		this.putValuesInMap(map, filters.getDtEmissFim(), "P.DT_EMISS <= TO_DATE ('", fDateEnd.concat(" 23:59:59','DD/MM/YYYY HH24:MI:SS')"));

		Optional.ofNullable(filters).ifPresent(entity -> {

			this.putValuesInMap(map, entity.getNroPedido(), "P.NRO_PEDIDO = '");
			this.putValuesInMap(map, entity.getSerie(), "P.APROVADO = '");

			/*
			 * if (entity.getNroNfIni() != null && entity.getNroNfFim() == null && entity.getNroNfValues() == null) {
			 * this.putValuesInMap(map, entity.getNroNfIni(), "V.NRO_NF >= '"); }
			 * 
			 * if (entity.getNroNfIni() != null && entity.getNroNfFim() == null && entity.getNroNfValues() == null) {
			 * this.putValuesInMap(map, entity.getNroNfIni(), "V.NRO_NF >= '"); }
			 * 
			 * if (entity.getNroNfFim() != null && entity.getNroNfIni() == null && entity.getNroNfValues() == null) {
			 * this.putValuesInMap(map, entity.getNroNfFim(), "V.NRO_NF <= '"); }
			 * 
			 * if (entity.getNroNfIni() != null && entity.getNroNfFim() != null && entity.getNroNfValues() == null) {
			 * this.putValuesInMap(map, entity.getNroNfIni() + " AND " + entity.getNroNfFim(), "V.NRO_NF BETWEEN "); }
			 */

		});

		// Optional.ofNullable(filters).map(Filters::getEmpresa).ifPresent(entity -> {
		//
		// // filtro por UF, cidade e IBGE vira de empresa.
		// this.putValuesInMap(map, entity.getUf(), "V.UF = '");
		// this.putValuesInMap(map, entity.getIbgeCidade(), "V.IBGE_CIDADE = '");
		// this.putValuesInMap(map, entity.getDescrCidade(), "V.DESCR_CIDADE = '");
		//
		// });

		return map;
	}

	public List<Pedido> findByFilters(Long pessoaId, PedidoDTO dto, EntityManagerFactory entityManagerFactory, Map<String, Object> requestParams, int page, int max)
			throws PessoaException {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			List<Order> orderBy = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
			Root<Pedido> from = cq.from(Pedido.class);

			for (Entry<String, Object> entry : requestParams.entrySet()) {
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

		if (entityDto != null) {

			// aqui poderia vir outras propriedades para uso no filtro e
			// montar o predicates.

			if (entityDto.getAprovado()) {
				predicates.add(cb.equal(from.get(Pedido_.aprovado), true));
			}

		}

		return predicates;
	}

	@SuppressWarnings("unchecked")
	public Set<String> findByHostsUf(Long estadoId, Long modfiscalId, EntityManagerFactory entityManagerFactory, EntityManager... managers) {

		EntityManager entityManager = managers.length > 0 ? managers[0] : entityManagerFactory.createEntityManager();

		ResultSet resultSet = null;
		AtomicReference<Connection> connection = new AtomicReference<>();
		Statement statement = null;

		String sql = "select " + "	distinct(pw.id), " + "  pw.url " + "	from " + "	  estado_versao_wsdl evw, " + "	  versao_wsdl vw, "
				+ "	  versao_layout vl, " + "	  param_webserv pw " + "	where " + "	  evw.versaowsdl_id = vw.id " + "	and "
				+ "	  vl.versaowsdl_id = vw.id " + "	and " + "	  pw.versaolayout_id = vl.id " + "  and " + "    evw.estado_id = pw.estado_id "
				+ "	and " + "	  evw.dm_situacao = 1 and evw.estado_id = " + estadoId + " and pw.modfiscal_id = " + modfiscalId;

		// nfe-api-files-report-rabbit

		Query query = managers[0].createNativeQuery(sql);

		List<Object[]> notasDatabase = query.getResultList();
		notasDatabase.stream().forEach(o -> {

		});

		Query nc = entityManager.createNativeQuery(sql);
		List<Object[]> listNc = nc.getResultList();

		Set<String> hosts = new HashSet<String>();
		for (Object[] obj : listNc) {
			String item = obj[1].toString();
			hosts.add(getHostName(item));
		}
		return hosts;
	}

	@SuppressWarnings("unchecked")
	public Set<String> findByHosts(EntityManagerFactory entityManagerFactory, EntityManager... em) {

		EntityManager entityManager = em.length > 0 ? em[0] : entityManagerFactory.createEntityManager();

		String sql = "select " + "	distinct(pw.id), " + "  pw.url " + "	from " + "	  estado_versao_wsdl evw, " + "	  versao_wsdl vw, "
				+ "	  versao_layout vl, " + "	  param_webserv pw " + "	where " + "	  evw.versaowsdl_id = vw.id " + "	and "
				+ "	  vl.versaowsdl_id = vw.id " + "	and " + "	  pw.versaolayout_id = vl.id " + "	and " + "	  evw.dm_situacao = 1";

		Query nc = entityManager.createNativeQuery(sql);
		List<Object[]> listNc = nc.getResultList();

		// Hosts formatadas
		Set<String> hosts = new HashSet<String>();
		for (Object[] obj : listNc) {
			String item = obj[1].toString();
			hosts.add(getHostName(item));

		}

		String sqlNFSe = "SELECT DISTINCT(cw.url_wsdl)" + " FROM cidade_webserv_nfse cw" + "  INNER JOIN cidade c ON c.id = cw.cidade_id"
				+ "  INNER JOIN  cidade_nfse cnfs ON cnfs.cidade_id = c.id" + " WHERE cnfs.dm_padrao = 0";
		Query n = entityManager.createNativeQuery(sqlNFSe);

		List<String> listN = n.getResultList();

		for (String str : listN) {
			hosts.add(getHostName(str));

		}
		return hosts;
	}

	public StringBuilder createQuery(Boolean rowCount, String fields, String table, LinkedHashMap<String, String> campoValorCond) {

		StringBuilder sb = new StringBuilder();
		sb.append(rowCount ? "SELECT COUNT ( PEDIDO_ID ) FROM " : "SELECT ").append(fields).append(table).append(" P");
		if (!campoValorCond.isEmpty()) {
			String cond = campoValorCond.entrySet()
					.stream()
					.map(entry -> entry.getKey().toUpperCase() + entry.getValue())
					.collect(Collectors.joining(" AND ", " WHERE ", ""));
			sb.append(cond);
		}
		return sb;
	}

	public void putValuesInMap(Map<String, String> map, String key, String value) {

		Optional.ofNullable(key).ifPresent(v -> map.put(value, !value.contains("BETWEEN") ? v + "'" : v));
	}

	public void putValuesInMap(Map<String, String> map, Date date, String key, String value) {

		Optional.ofNullable(date).map(r -> UtilDate.toDateString(r)).ifPresent(val -> map.put(key, value));
	}

	public static String getHostName(String item) {

		item = item.replace("https://", "");
		String url = item.split("/")[0].trim();
		return url;
	}

	public boolean isEmptyCollection(Object collection) {

		boolean result = true;
		if (collection instanceof Set<?>) {
			Set<?> copyCollection = (Set<?>) collection;
			result = (copyCollection == null || copyCollection.isEmpty()) ? true : false;
		} else if (collection instanceof List<?>) {
			List<?> copyCollection = (List<?>) collection;
			result = (copyCollection == null || copyCollection.isEmpty()) ? true : false;
		} else if (collection instanceof Collection<?>) {
			Collection<?> copyCollection = (Collection<?>) collection;
			result = (copyCollection == null || copyCollection.isEmpty()) ? true : false;
		}
		return result;
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
