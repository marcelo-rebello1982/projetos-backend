//package br.com.cadastroit.services.api.services;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.NoResultException;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import br.com.cadastroit.services.api.domain.Empresa;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(propagation = Propagation.REQUIRED)
//public class EmpresaService {
//
//	private static final String OBJECT = "EMPRESA";
//
//	public Empresa findById(Long id, EntityManagerFactory entityManagerFactory) {
//		EntityManager em = entityManagerFactory.createEntityManager();
//		try {
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);
//			Root<Empresa> from = cq.from(Empresa.class);
//			TypedQuery<Empresa> tQuery = em
//					.createQuery(cq.select(from).where(cb.and(cb.equal(from.get(Empresa_.id), id))));
//			return tQuery.getSingleResult();
//		} catch (NoResultException ex) {
//			throw new NoResultException(OBJECT + " : '" + id + "' " + ex.getMessage());
//		} finally {
//			em.clear();
//			em.close();
//		}
//	}
//}
package br;


