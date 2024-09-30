package br.com.cadastroit.services.model.commons.api;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.api.domain.MultOrg;
import br.com.cadastroit.services.api.domain.MultOrg_;
import br.com.cadastroit.services.exceptions.CommonsApiException;

@Repository
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class CommonsJpaAPI {
	
	public Empresa validateInfoMultOrg(Credential credential, EntityManager entityManager) throws CommonsApiException{
		try {
			CriteriaBuilder cb 		  = entityManager.getCriteriaBuilder();
			CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);
			Root<Empresa> from		  = cq.from(Empresa.class);
			Join<Empresa, MultOrg> joinMultOrg = from.join(Empresa_.multOrg,JoinType.INNER);
			
			Predicate restriction	  = cb.and(cb.equal(joinMultOrg.get(MultOrg_.cd), credential.getCodigo()),
											   cb.equal(joinMultOrg.get(MultOrg_.hash), credential.getHash()),
											   cb.equal(joinMultOrg.get(MultOrg_.dmSituacao), 1),
											   cb.equal(from.get(Empresa_.id), credential.getEmpresaId()));
			 Empresa empresa		  = entityManager.createQuery(cq.select(from).where(restriction)).getSingleResult();
			 return empresa;
		}catch(NoResultException ex) {
			throw new CommonsApiException("The credentials are not valid to record, check your credentials..., [ERROR] = "+ex.getMessage());
		}catch(Exception ex) {
			throw new CommonsApiException("Validate process error, check your app..., [ERROR] = "+ex.getMessage());
		}
	}

	public void finalizeEntityManager(Logger LOGGER, EntityManager... entityManager) {
		Arrays.stream(entityManager).forEach(e->{
			if(e != null && e.isOpen()) {
				LOGGER.info("Closing connections...");
				e.clear();
				e.close();
			}
		});
	}
}
