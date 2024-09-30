package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Empresa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.DepartamentoException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.impl.EmpresaRepositoryImpl;
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
