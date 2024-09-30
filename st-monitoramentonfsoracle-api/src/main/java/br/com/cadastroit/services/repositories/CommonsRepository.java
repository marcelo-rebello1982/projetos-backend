package br.com.cadastroit.services.repositories;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.NfServException;
import br.com.cadastroit.services.utils.UtilDate;
import br.com.cadastroit.services.utils.Utilities;
import br.com.cadastroit.services.web.dto.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Getter
@Transactional(propagation = Propagation.REQUIRED)
public class CommonsRepository {

    private final Utilities utilities = Utilities.builder().build();

    public <T> T findById(Long id, Class<T> clazz, EntityManagerFactory entityManagerFactory) {

        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clazz);
            Root<T> from = cq.from(clazz);
            TypedQuery<T> tQuery = em
                    .createQuery(cq.select(
                            from).where(
                            cb.and(cb.equal(
                                    from.get("id"),
                                    id))));

            return tQuery.getSingleResult();
        } catch (NoResultException ex) {
            throw new NfServException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), getTblName(clazz), "", id));
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public LinkedHashMap<String, String> createPredicates(boolean objectNotNull, Filters filters) {

    	//https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

    	String fDateBegin = UtilDate.toDateString(UtilDate.addMinHourToDate(UtilDate.toDateString(filters.getDtEmissIni())), "dd/MM/yyyy");
    	String fDateEnd = UtilDate.toDateString(UtilDate.addMaxHourToDate(UtilDate.toDateString(filters.getDtEmissFim())), "dd/MM/yyyy");

    	if (objectNotNull && filters.getIsDestalharItemNfServ().get() == false || filters.getIsDestalharItemNfServ() == null) { 
    		// se não passar o IsDestalharItemNfServ , monta o predicates para
    		// a view  V_DASH_TOT_CIDADE_ESTADO
    		// caso contrário para V_DASH_TOT_ITEM_NFSERV. MP 13-07-2023
    		
    		

    		this.utilities.putValuesInMap(map, filters.getMultOrgCd(), "V.MULTORG_CD = '");

    		Optional.ofNullable(filters).map(Filters::getEmpresa).ifPresent(entity -> {

    			this.utilities.putValuesInMap(map, entity.getUf(), "V.UF = '");
    			this.utilities.putValuesInMap(map, entity.getIbgeCidade(), "V.IBGE_CIDADE = '");
    			this.utilities.putValuesInMap(map, entity.getDescrCidade(), "V.DESCR_CIDADE = '");

    		});

    		this.utilities.putValuesInMap(map, filters.getDtEmissIni(), "V.DT_EMISS >= TO_DATE ('", fDateBegin.concat(" 23:59:59','DD/MM/YYYY HH24:MI:SS')"));
    		this.utilities.putValuesInMap(map, filters.getDtEmissFim(), "V.DT_EMISS <= TO_DATE ('", fDateEnd.concat(" 23:59:59','DD/MM/YYYY HH24:MI:SS')"));

    	} else {

    		Optional.ofNullable(filters).ifPresent(entity -> {

    			this.utilities.putValuesInMap(map, entity.getMultOrgCd(), "V.MULTORG_CD = '");
    			this.utilities.putValuesInMap(map, entity.getCnpjEmit(), "V.CNPJ_EMIT = '");
    			this.utilities.putValuesInMap(map, entity.getImEmit(), "V.IM_EMIT = '");
    			this.utilities.putValuesInMap(map, entity.getSerie(), "V.SERIE = '");

    			// UX Usara "nroNfValues": ["164, 248"] ou "dmStProcValues": ["1, 10"]
    			// para consultar uma ou X nf's MP 08/23 RM 111509/111995
    			
    			if (entity.getNroNfValues() != null && entity.getNroNfIni() == null && entity.getNroNfFim() == null) {
    				this.utilities.putValuesInMap(map, "V.NRO_NF IN (", entity.getNroNfValues());
    			}
    			
    			if (entity.getNroNfIni() != null && entity.getNroNfFim() == null && entity.getNroNfValues() == null ) {
    				this.utilities.putValuesInMap(map, entity.getNroNfIni(), "V.NRO_NF >= '");
    			}
    			
    			if (entity.getNroNfIni() != null && entity.getNroNfFim() == null && entity.getNroNfValues() == null ) {
    				this.utilities.putValuesInMap(map, entity.getNroNfIni(), "V.NRO_NF >= '");
    			}
    			
    			if (entity.getNroNfFim() != null && entity.getNroNfIni() == null && entity.getNroNfValues() == null ) {
    				this.utilities.putValuesInMap(map, entity.getNroNfFim(), "V.NRO_NF <= '");
    			}
    			
    			if (entity.getNroNfIni() != null && entity.getNroNfFim() != null && entity.getNroNfValues() == null ) {
    				this.utilities.putValuesInMap(map,  entity.getNroNfIni() + " AND " + entity.getNroNfFim(), "V.NRO_NF BETWEEN ");
    			}

    			if (entity.getDmStProcValues() != null) {
    				this.utilities.putValuesInMap(map, "V.DM_ST_PROC IN (", entity.getDmStProcValues());
    			}

    			this.utilities.putValuesInMap(map, entity.getNroDocTomador(), "V.NRO_DOC_TOMADOR = '");
    			this.utilities.putValuesInMap(map, entity.getNomeTomador(), "V.NOME_TOMADOR = '");

    			this.utilities.putValuesInMap(map, entity.getDtEmissIni(), "V.DT_EMISS >= TO_DATE ('", fDateBegin.concat(" 23:59:59','DD/MM/YYYY HH24:MI:SS')"));
    			this.utilities.putValuesInMap(map, entity.getDtEmissFim(), "V.DT_EMISS <= TO_DATE ('", fDateEnd.concat(" 23:59:59','DD/MM/YYYY HH24:MI:SS')"));

    		});

    		Optional.ofNullable(filters).map(Filters::getEmpresa).ifPresent(entity -> {

    			// filtro por  UF, cidade e IBGE vira de  empresa.
    			this.utilities.putValuesInMap(map, entity.getUf(), "V.UF = '");
    			this.utilities.putValuesInMap(map, entity.getIbgeCidade(), "V.IBGE_CIDADE = '");
    			this.utilities.putValuesInMap(map, entity.getDescrCidade(), "V.DESCR_CIDADE = '");

    		});
    	}
    	return map;
    }
    
    public static String getTblName(Class<?> clazz) {
        return clazz.getAnnotation(Table.class).name();
    }
}
