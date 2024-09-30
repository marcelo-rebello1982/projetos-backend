package br.com.cadastroit.services.repositories;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Pessoa_;
import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.impl.PessoaRepositoryImpl;
import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PessoaRepository implements Serializable {

	private static final long serialVersionUID = -6226504130558319561L;

	private static final String MODE = "Error on %s mode to %s, [error] = %s";
	private static final String OBJECT = "PESSOA";
	private static final String ORDER = "order";
	
	@Autowired
	private PessoaRepositoryImpl pessoaRepositoryImpl;
	
	
	public Pessoa findById(Long id, EntityManagerFactory entityManagerFactory) {

		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
			Root<Pessoa> from = cq.from(Pessoa.class);
			TypedQuery<Pessoa> tQuery = em.createQuery(cq.select(from).where(cb.equal(from.get(Pessoa_.id), id)));
			return tQuery.getSingleResult();
		} catch (NoResultException ex) {
			throw new PessoaException(String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), OBJECT, "", id));
		} finally {
			em.clear();
			em.close();
		}
	}
	

	
	public Optional<Pessoa> findById(Long id) {

		return pessoaRepositoryImpl.findById(id);
	}

	public <S extends Pessoa> S save(S entity) {

		return pessoaRepositoryImpl.save(entity);
	}

	public void delete(Pessoa entity) {

		pessoaRepositoryImpl.delete(entity);
	}

	public <T> Optional<T> checkIsNull(T field) {

		return Optional.ofNullable(field);
	}

}
