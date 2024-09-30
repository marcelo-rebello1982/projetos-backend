package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.api.enums.StatusProcessamento;
import br.com.cadastroit.services.commons.api.ViewTotalItemNfServ;
import br.com.cadastroit.services.exceptions.NfServException;
import br.com.cadastroit.services.repositories.impl.NfServRepositoryImpl;
import br.com.cadastroit.services.utils.UtilDate;
import br.com.cadastroit.services.web.dto.Filters;
import br.com.cadastroit.services.web.dto.NfServDto;
import br.com.complianceit.services.api.domain.Empresa_;
import br.com.complianceit.services.api.domain.NfServ_;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class NfServRepository implements Serializable {

	private final CommonsRepository commonsRepository;
    private static final long serialVersionUID = -3811509347586916889L;
    private static final String MODE = "Error on %s mode to %s, [error] = %s";
    private static final String OBJECT = "NF_SERV";
    private static final String ORDER = "order";

    @Autowired
    private NfServRepositoryImpl nfServRepositoryImpl;

    public Long maxId(EntityManagerFactory entityManagerFactory) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createNativeQuery("select nvl(max(id),1) from " + OBJECT);
        return ((Number) query.getSingleResult()).longValue();
    }

    public Long maxId(EntityManagerFactory entityManagerFactory, Long empresaId) throws NfServException {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	try {

    		CriteriaBuilder cb = em.getCriteriaBuilder();
    		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    		Root<NfServ> from = cq.from(NfServ.class);
    		Join<NfServ, Empresa> joinEmpresa = from.join(NfServ_.empresa, JoinType.INNER);
    		TypedQuery<Long> tQuery = em
    				.createQuery(cq.select(cb.max(from.get(
    						NfServ_.id)))
    						.where(cb.equal(
    								joinEmpresa.get(Empresa_.id),
    								empresaId)));

    		return tQuery.getSingleResult();
    	} catch (Exception ex) {
    		throw new NfServException(String.format(MODE, "maxId", OBJECT, ex.getMessage()));
    	} finally {
    		em.clear();
    		em.close();
    	}
    }

    public NfServ findById(Long id, EntityManagerFactory entityManagerFactory) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NfServ> cq = cb.createQuery(NfServ.class);
            Root<NfServ> from = cq.from(NfServ.class);
            TypedQuery<NfServ> tQuery = em
                    .createQuery(cq.select(from)
                            .where(cb.equal(
                                    from.get(NfServ_.id), id)));
            return tQuery.getSingleResult();
        } catch (NoResultException ex) {
            throw new NfServException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
        } finally {
            em.clear();
            em.close();
        }
    }

    public NfServ findById(Long empresaId, Long id, EntityManagerFactory entityManagerFactory) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NfServ> cq = cb.createQuery(NfServ.class);
            Root<NfServ> from = cq.from(NfServ.class);
            TypedQuery<NfServ> result = em
                    .createQuery(cq.select(from)
                            .where(cb.and(cb.equal(
                                    from.get(NfServ_.id), id))));

            return result.getSingleResult();
        } catch (NoResultException ex) {
            throw new NfServException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
        } finally {
            em.clear();
            em.close();
        }
    }

    public List<NfServ> findAll(EntityManagerFactory entityManagerFactory, Map<String, Object> requestParams, int page,
                                int length) throws NfServException {

        EntityManager em = entityManagerFactory.createEntityManager();
        try {

            List<Order> orderBy = new ArrayList<>();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NfServ> cq = cb.createQuery(NfServ.class);
            Root<NfServ> from = cq.from(NfServ.class);

            requestParams.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(ORDER))
                    .map(e ->
                            (e.getValue() == null || e.getValue().equals("desc"))
                                    ? cb.desc(from.get(NfServ_.id))
                                    : cb.asc(from.get(NfServ_.id)))
                    .forEach(orderBy::add);

//			requestParams.entrySet().stream().filter(entry -> entry.getKey().startsWith(ORDER))
//			.forEach(entry -> {
//				Object value = entry.getValue();
//					orderBy.add(value == null || value.equals("desc")
//						? cb.desc(from.get(AgendIntegr_.id))
//						: cb.asc(from.get(AgendIntegr_.id)));
//			});

            TypedQuery<NfServ> tQuery = em
                    .createQuery(cq.select(from)
                            .orderBy(orderBy));
            tQuery.setFirstResult((page - 1) * length);
            tQuery.setMaxResults(length);
            return tQuery.getResultList();
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "pagination", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    @SuppressWarnings({ "static-access", "unchecked"}) 
	public List<ViewTotalItemNfServ> vDashTotItemNfServ(Filters filters, Map<String, String> requestParams, int page,
                                                        int length, EntityManagerFactory entityManagerFactory) throws SQLException {

        UtilDate utilDate = new UtilDate();
        AtomicBoolean order = new AtomicBoolean(true);
        AtomicReference<List<ViewTotalItemNfServ>> itemNfServ = new AtomicReference<>(new ArrayList<>());
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

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

            if (filters.getSiglaEstado() == null) {

            }
            
            StringBuilder toSelect = new StringBuilder();
            toSelect.append("V.NFSERV_ID, V.MULTORG_CD, V.CNPJ_EMIT, V.IM_EMIT, V.SERIE, V.NRO_NF, ")
                    .append("V.DT_EMISS, V.HR_EMISS, V.DT_EXE_SERV, V.DM_ST_PROC, V.DESCR_CIDADE, V.UF,")
                    .append("V.IBGE_CIDADE, V.DM_DB_DESTINO, V.NRO_DOC_TOMADOR, V.NOME_TOMADOR, ")
                    .append("V.VL_TOT_SERV, V.VL_TOT_DESCONTO, V.VL_TOT_RETIDO, V.VL_TOT_ISS, V.VL_TOTAL_NF FROM ");

            
            StringBuilder sqlQryString = this.commonsRepository.getUtilities().createQuery(false, toSelect.toString(), "V_DASH_TOT_ITEM_NFSERV", 
                            this.commonsRepository.createPredicates(filters != null ?
                                            true : false, // no predicates,apenas confere  se o filtro é null.
                                    filters))
                    .append(" ORDER BY V.DT_EMISS")
                    .append(order.get() == true ?
                            " DESC" : " ASC");

            Query query = em.createNativeQuery(sqlQryString.toString());
            query.setFirstResult((page - 1) * length);
            query.setMaxResults(length);

			List<Object[]> result = query.getResultList();

            result.stream()
            
                    .map( obj ->

                            ViewTotalItemNfServ.builder()

                                    .nfServId(obj[0].toString())
                                    .multOrgCd(obj[1].toString())
                                    .cnpjEmit(obj[2].toString())
                                    .imEmit(obj[3].toString())
                                    .serie(obj[4].toString())
                                    .nroNf(Integer.valueOf(obj[5].toString()))
                                    .dtEmissao(obj[6].toString())
                                    .hrEmissao(obj[7] != null ? obj[7].toString() : null)
                                    .dtExeServ(utilDate.convertStringDateToTimestamp(obj[8].toString(), "yyyy-MM-dd HH:mm:ss.S"))
                                    .dmStProc(new BigDecimal(obj[9].toString()))
                                    .descrCidade(obj[10].toString().toUpperCase())
                                    .uf(obj[11].toString())
                                    .ibgeCidade(obj[12].toString())
                                    .dmDbDestino(Integer.valueOf(obj[13].toString()))
                                    .nroDocTomador(obj[14] != null ? obj[14].toString() : null)
                                    .nomeTomador(obj[15] != null ? obj[15].toString().toUpperCase() : null)
                                    .vlTotServ(new Double(obj[16].toString()))
                                    .vlTotDesconto(new Double(obj[17].toString()))
                                    .vlTotRetido(new Double(obj[18].toString()))
                                    .vlTotIss(new Double(obj[19].toString()))
                                    .vlTotalNf(new Double(obj[20].toString()))
                                    .build())

                    .forEach( obj ->
                    
                            itemNfServ.get()
                                    .add(obj));

            return itemNfServ.get();

        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "V_DASH_TOT_ITEM_NFSERV", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    private String[] castObjectToArray(ViewTotalItemNfServ vServ) {
        String[] array = new String[11];
        array[0] = vServ.getNfServId() == null ? null : String.valueOf(vServ.getNfServId());
        array[1] = vServ.getCnpjEmit();
        array[2] = String.valueOf(vServ.getDmStProc());
        return array;
    }

    public List<NfServ> findByFilters(NfServDto entityDto, EntityManagerFactory entityManagerFactory,
                                      Map<String, Object> requestParams, int page, int max) throws NfServException {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            List<Order> orderBy = new ArrayList<>();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NfServ> cq = cb.createQuery(NfServ.class);
            Root<NfServ> from = cq.from(NfServ.class);
            

            //for (Entry<String, Object> currentEntry : requestParams.entrySet()) {
            //   if (currentEntry.getKey().startsWith(ORDER)) {
            //        if (currentEntry.getValue().equals("dtEmissao")) {
            //            orderBy.add((currentEntry.getValue().equals("dtEmissao")
            //                    ? cb.desc(from.get(NfServ_.dtEmiss))
            //                    : cb.asc(from.get(NfServ_.dtEmiss))));
            //        } else {
            //            orderBy.add((currentEntry.getValue() == null || currentEntry.getValue().equals("desc")
            //                    ? cb.desc(from.get(NfServ_.id))
            //                    : cb.asc(from.get(NfServ_.id))));
            //
            //        }
            //    }
            //}

            for (Entry<String, Object> currentEntry : requestParams.entrySet()) {
                if (currentEntry.getKey().startsWith(ORDER)) {
                    boolean isDesc = "desc".equals(currentEntry.getValue());
                    orderBy.add(isDesc
                            ? cb.desc(from.get(NfServ_.id))
                            : cb.asc(from.get(NfServ_.id)));

                    if ("dtEmissao".equals(currentEntry.getValue())) {
                        orderBy.set(orderBy.size() - 1, isDesc
                                ? cb.desc(from.get(NfServ_.dtEmiss))
                                : cb.asc(from.get(NfServ_.dtEmiss)));
                    }
                }
            }

            List<Predicate> predicates = createPredicates(null, entityDto != null
                    ? entityDto : null, from, cb);
            TypedQuery<NfServ> tQuery = em
                    .createQuery(cq.select(from)
                            .where(predicates.stream()
                                    .toArray(Predicate[]::new))
                            .orderBy(orderBy));

            if (page != 0 && max != 0) {
                tQuery.setFirstResult((page - 1) * max);
                tQuery.setMaxResults(max);
            }
            return tQuery.getResultList();
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    public NfServ findById(String multOrgCd, Long id, EntityManagerFactory entityManagerFactory) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NfServ> cq = cb.createQuery(NfServ.class);
            Root<NfServ> from = cq.from(NfServ.class);
            TypedQuery<NfServ> tQuery = em
                    .createQuery(cq.select(from)
                            .where(cb.equal(from.get(
                                            NfServ_.multorgCd), multOrgCd),
                                    cb.equal(from.get(NfServ_.id), id)));
            return tQuery.getSingleResult();
        } catch (NoResultException ex) {
            throw new NfServException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
        } finally {
            em.clear();
            em.close();
        }
    }

    public Long[] countByDmStProc(Long empresaId, NfServDto entityDto, EntityManagerFactory entityManagerFactory)
            throws NfServException {

        EntityManager em = entityManagerFactory.createEntityManager();
        List<Predicate> predicates = new ArrayList<>();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<NfServ> from = cq.from(NfServ.class);
            if (entityDto != null) {
                //predicates = this.createPredicates(empresaId, entityDto, from, cb);
            } else {
                //predicates = this.createPredicates(empresaId, null, from, cb);
            }

            Expression<Integer> exprProcessamento = cb.sum(cb.<Integer>selectCase()
                    .when(cb.isTrue(from.get(
                                    NfServ_.dmStProc)
                            .in(0, 1, 2, 3, 21)), 1)
                    .otherwise(0));

            Expression<Integer> exprPendente = cb.sum(cb.<Integer>selectCase()
                    .when(cb.isFalse(from.get(
                                    NfServ_.dmStProc)
                            .in(5, 10, 11, 12, 13, 15, 99)), 1)
                    .otherwise(0));

            Expression<Integer> exprAutorizada = cb.sum(cb.<Integer>selectCase()
                    .when(cb.equal(from.get(
                            NfServ_.dmStProc), 4), 1)
                    .otherwise(0));

            Expression<Integer> exprCancelada = cb.sum(cb.<Integer>selectCase()
                    .when(cb.equal(from.get(
                            NfServ_.dmStProc), 7), 1)
                    .otherwise(0));


            cq.select(cb.tuple(

                            exprProcessamento.alias("processamento"),
                            exprPendente.alias("pendencia"),
                            exprAutorizada.alias("autorizada"),
                            exprCancelada.alias("cancelada")))

                    .where(cb.and(cb.equal(from.get(NfServ_.multorgCd), entityDto.getMultorgCd()),
                            cb.greaterThanOrEqualTo(from.get(NfServ_.dtEmiss), entityDto.getDtEmissIni()),
                            cb.lessThanOrEqualTo(from.get(NfServ_.dtEmiss), entityDto.getDtEmissFim())));

            Tuple result = em.createQuery(cq).getSingleResult();

            Long processamento = (Long) result.get("processamento");
            Long pendencia = (Long) result.get("pendencia");
            Long autorizada = (Long) result.get("autorizada");
            Long cancelada = (Long) result.get("cancelada");

            Long resultLong[] = {0L, processamento, pendencia, autorizada, cancelada};
            return resultLong;
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "count", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    public List<Filters> findByDmStProc(NfServDto entityDto, EntityManagerFactory entityManagerFactory,
                                        Map<String, Object> requestParams, int page, int max) throws NfServException {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            List<Order> orderBy = new ArrayList<>();
            List<Filters> listDto = new ArrayList<>();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NfServ> cq = cb.createQuery(NfServ.class);
            Root<NfServ> from = cq.from(NfServ.class);

            requestParams.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(ORDER))
                    .map(e ->
                            (e.getValue() == null || e.getValue().equals("desc"))
                                    ? cb.desc(from.get(NfServ_.id))
                                    : cb.asc(from.get(NfServ_.id)))
                    .forEach(orderBy::add);

            List<Predicate> predicates = this.createPredicates(null, entityDto, from, cb);
            TypedQuery<NfServ> tQuery = em
                    .createQuery(cq.select(from)
                            .where(predicates.stream()
                                    .toArray(Predicate[]::new))
                            .orderBy(orderBy));

            if (page != 0 && max != 0) {
                tQuery.setFirstResult((page - 1) * max);
                tQuery.setMaxResults(max);
            }

            listDto.add(Filters.builder().nfServ(tQuery.getResultList()).build());
            return listDto;

        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "buscar por filtros", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    public Long count(Long empresaId, NfServDto entityDto, EntityManagerFactory entityManagerFactory) throws NfServException {

        EntityManager em = entityManagerFactory.createEntityManager();
        List<Predicate> predicates = new ArrayList<>();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<NfServ> from = cq.from(NfServ.class);
            if (entityDto != null) {
                predicates = this.createPredicates(empresaId, entityDto, from, cb);
            } else {
                predicates = this.createPredicates(empresaId, null, from, cb);
            }
            return em
                    .createQuery(
                            cq.select(cb.count(from.get(NfServ_.id)))
                                    .where(predicates.toArray(new Predicate[]{})))
                    .getSingleResult();
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "count", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    public Long count(Filters filters, Map<String, String> requestParams, int page, int length, boolean rowCount, EntityManager... managers) throws NfServException {

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

            if (filters.getSiglaEstado() == null) {

            }

            StringBuilder sqlRowCount = this.commonsRepository.getUtilities().createQuery(rowCount, "V_DASH_TOT_ITEM_NFSERV", "",
                    this.commonsRepository.createPredicates(filters != null ?
                                    true : false, // no predicates,apenas confere  se o filtro é null.
                            filters));
            sqlRowCount.append(rowCount ? "" :
                            " ORDER BY V.DT_EMISS ")
                    .append(rowCount ? ";" : order.get() == true
                            ? "DESC" : "ASC");

            Query tRows = managers[1].createNativeQuery(sqlRowCount.toString());
            Long row = new Long(tRows.getSingleResult().toString());
            return row;
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "VDASHTOTITEMNFSERV", OBJECT, ex.getMessage()));
        } finally {
        	if (managers[1].isOpen()) {
        		managers[1].clear();
        		managers[1].close();
        	}
        }
    }

    public Long countByEstadoCidade(Long empresaId, NfServDto entityDto, EntityManagerFactory entityManagerFactory)
            throws NfServException {

        EntityManager em = entityManagerFactory.createEntityManager();

        List<Predicate> predicates = new ArrayList<>();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<NfServ> from = cq.from(NfServ.class);
            if (entityDto != null) {
                predicates = this.createPredicates(empresaId, entityDto, from, cb);
            } else {
                predicates = this.createPredicates(empresaId, null, from, cb);
            }

            return em
                    .createQuery(
                            cq.select(cb.count(from.get(NfServ_.id)))
                                    .where(predicates.toArray(new Predicate[]{})))
                    .getSingleResult();
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "count", OBJECT, ex.getMessage()));
        } finally {
            em.clear();
            em.close();
        }
    }

    public List<Predicate> createPredicates(Long empresaId, NfServDto entityDto, Root<NfServ> from,
                                            CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(entityDto).ifPresent(entity -> {

            this.commonsRepository.getUtilities().checkIsNull(entity.getDtEmissIni()).ifPresent(
                    values -> predicates.add(cb.and(cb.greaterThanOrEqualTo(from.get(NfServ_.dtEmiss), values))));

            this.commonsRepository.getUtilities().checkIsNull(entity.getDtEmissFim()).ifPresent(
                    values -> predicates.add(cb.and(cb.lessThanOrEqualTo(from.get(NfServ_.dtEmiss), values))));

            this.commonsRepository.getUtilities().checkIsNull(entity.getChaveNfse())
                    .ifPresent(values -> predicates.add(cb.equal(from.get(NfServ_.chaveNfse), values)));

            this.commonsRepository.getUtilities().checkIsNull(entity.getMultorgCd())
                    .ifPresent(values -> predicates.add(cb.equal(from.get(NfServ_.multorgCd), values)));

            this.commonsRepository.getUtilities().checkIsNull(entity.getDmStProc())
                    .ifPresent(values -> predicates.add(cb.equal(from.get(NfServ_.dmStProc), values)));

            this.commonsRepository.getUtilities().checkIsNull(entity.getDmStProcValues()).ifPresent(values -> predicates.add(from.get(NfServ_.dmStProc).in((Object[]) values)));

            Optional.ofNullable(entityDto).map(NfServDto::getEmpresa).ifPresent(emp -> {

                Join<NfServ, Empresa> joinEmpresa = from.join(NfServ_.empresa, JoinType.INNER);

                this.commonsRepository.getUtilities().checkIsNull(emp.getId()).ifPresent(values -> predicates.add(cb.equal(joinEmpresa.get(Empresa_.uf), values)));
                this.commonsRepository.getUtilities().checkIsNull(emp.getIbgeCidadeValues()).ifPresent(values -> predicates.add(joinEmpresa.get(Empresa_.ibgeCidade).in((Object[]) values)));

            });
        });

        if (entityDto != null && entityDto.getStatusProc() != null && entityDto.getDmStProc() == null) {

            In<BigDecimal> inDmStProcValues = cb.in(from.get(NfServ_.dmStProc));

            entityDto
                    .getStatusProc()
                    .stream()
                    .filter(status ->

                            EnumSet.of(

                                            StatusProcessamento.VALIDADA,
                                            StatusProcessamento.PROCESSADA,
                                            StatusProcessamento.ENVIADA,
                                            StatusProcessamento.AUTORIZADA,
                                            StatusProcessamento.REJEITADA,
                                            StatusProcessamento.ERRONAVALIDACAO,
                                            StatusProcessamento.ERRONAMONTAGEMDOXML,
                                            StatusProcessamento.ERROAOENVIARANOTA,
                                            StatusProcessamento.ERROAOOBTERORETORNODOENVIODANOTA,
                                            StatusProcessamento.RPSNAOCONVERTIDO,
                                            StatusProcessamento.AGUARDANDOLIBERACAO,
                                            StatusProcessamento.SUBSTITUIDA,
                                            StatusProcessamento.ERROGERALDESISTEMA)

                                    .contains(status))
                    .forEach(status -> this.commonsRepository.addToPredicates(cb, predicates, inDmStProcValues, this.commonsRepository.convertEnumToBigDecimal(status)));

        }
        return predicates;
    }

    public Optional<NfServ> findById(Long id) {
        return nfServRepositoryImpl.findById(id);
    }

    public <S extends NfServ> S save(S entity) {
        return nfServRepositoryImpl.save(entity);
    }

    public void delete(NfServ entity) {
        nfServRepositoryImpl.delete(entity);
    }
}
