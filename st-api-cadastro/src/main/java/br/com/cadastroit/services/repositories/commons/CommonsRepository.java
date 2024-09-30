package br.com.cadastroit.services.repositories.commons;

import java.util.Arrays;
import java.util.logging.Logger;

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
import br.com.cadastroit.services.exceptions.PessoaException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class CommonsRepository {

	public <T> T findById(Long id, Class<T> clazz, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(clazz);
			Root<T> from = cq.from(clazz);
			TypedQuery<T> tQuery = em.createQuery(cq.select(from).where(cb.and(cb.equal(from.get("id"), id))));

			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), getTblName(clazz), "", id));
		} finally {
			em.clear();
			em.close();
		}
	}

	public static String getTblName(Class<?> clazz) {

		return clazz.getAnnotation(Table.class).name();
	}

	public void finalizeEntityManager(Logger LOGGER, EntityManager... entityManager) {

		Arrays.stream(entityManager).forEach(e -> {
			if (e != null && e.isOpen()) {
				LOGGER.info("Closing connections...");
				e.clear();
				e.close();
			}
		});
	}
}
