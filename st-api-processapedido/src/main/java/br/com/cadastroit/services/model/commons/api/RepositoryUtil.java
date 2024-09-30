package br.com.cadastroit.services.model.commons.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import br.com.cadastroit.services.api.domain.MultOrg;
import br.com.cadastroit.services.api.domain.MultOrg_;
import br.com.cadastroit.services.api.domain.Usuario;
import br.com.cadastroit.services.api.domain.UsuarioEmpresa;
import br.com.cadastroit.services.api.domain.UsuarioEmpresa_;
import br.com.cadastroit.services.api.domain.Usuario_;

public class RepositoryUtil {

	protected javax.persistence.criteria.Predicate[] restrictions(List<javax.persistence.criteria.Predicate> restrictions) {

		javax.persistence.criteria.Predicate[] restrict = new javax.persistence.criteria.Predicate[restrictions.size()];
		for (int index = 0; index < restrict.length; index++) {
			restrict[index] = restrictions.get(index);
		}
		return restrict;
	}

	protected List<Long> retrieveConsolida(Long idEmpresa, Credential credential, EntityManager entityManager) {

		List<Long> empresas = new ArrayList<>();
		CriteriaBuilder cbConsolida = entityManager.getCriteriaBuilder();
		CriteriaQuery<UsuarioEmpresa> cqConsolida = cbConsolida.createQuery(UsuarioEmpresa.class);
		Root<UsuarioEmpresa> fromConsolida = cqConsolida.from(UsuarioEmpresa.class);
		fromConsolida.join(UsuarioEmpresa_.empresa, JoinType.INNER);
		Join<UsuarioEmpresa, Usuario> joinUEU = fromConsolida.join(UsuarioEmpresa_.usuario, JoinType.INNER);
		Join<Usuario, MultOrg> joinUMO = joinUEU.join(Usuario_.multOrg, JoinType.INNER);

		TypedQuery<UsuarioEmpresa> tQuery = entityManager.createQuery(cqConsolida.select(fromConsolida)
				.where(cbConsolida.and(cbConsolida.equal(joinUEU.get(Usuario_.id), credential.getUsuarioId()),
						cbConsolida.equal(joinUMO.get(MultOrg_.cd), credential.getCodigo()))));
		if (!tQuery.getResultList().isEmpty()) {
			List<UsuarioEmpresa> ueCollection = tQuery.getResultList();
			ueCollection.forEach(ue -> {
				empresas.add(ue.getEmpresa().getId());
			});
		}
		entityManager.clear();
		return empresas;
	}

	protected Multimap<String, List<Long>> retrieveConsolida(Long idEmpresa, String CnpjEmpTomadora, Credential credential, EntityManager entityManager) {

		Multimap<String, List<Long>> mapData = ArrayListMultimap.create();
		List<Long> empresas = new ArrayList<Long>();
		List<Long> codPart = new ArrayList<Long>();
		CriteriaBuilder cbConsolida = entityManager.getCriteriaBuilder();
		CriteriaQuery<UsuarioEmpresa> cqConsolida = cbConsolida.createQuery(UsuarioEmpresa.class);
		Root<UsuarioEmpresa> fromConsolida = cqConsolida.from(UsuarioEmpresa.class);
		fromConsolida.join(UsuarioEmpresa_.empresa, JoinType.INNER);
		Join<UsuarioEmpresa, Usuario> joinUEU = fromConsolida.join(UsuarioEmpresa_.usuario, JoinType.INNER);
		Join<Usuario, MultOrg> joinUMO = joinUEU.join(Usuario_.multOrg, JoinType.INNER);

		TypedQuery<UsuarioEmpresa> tQuery = entityManager.createQuery(cqConsolida.select(fromConsolida)
				.where(cbConsolida.and(cbConsolida.equal(joinUEU.get(Usuario_.id), credential.getUsuarioId()),
						cbConsolida.equal(joinUMO.get(MultOrg_.cd), credential.getCodigo()))));
		if (!tQuery.getResultList().isEmpty()) {
			List<UsuarioEmpresa> ueCollection = tQuery.getResultList();
			ueCollection.forEach(ue -> {
				empresas.add(ue.getEmpresa().getId());
				codPart.add(ue.getEmpresa().getPessoa() != null ? Long.valueOf(ue.getEmpresa().getPessoa().getCodPart()) : null);
			});
			mapData.put("idEmp", empresas);
			mapData.put("codPart", codPart);
		}
		entityManager.clear();
		return mapData;
	}

	// Método desabilitado e substituido pelo método acima, acompanhar e futuramente excluir este trecho de código MP 10/05
	protected List<String> retrieveConsolidaPessoa(Long idEmpresa, Credential credential, EntityManager entityManager) {

		List<String> codPart = new ArrayList<>();
		CriteriaBuilder cbConsolida = entityManager.getCriteriaBuilder();
		CriteriaQuery<UsuarioEmpresa> cqConsolida = cbConsolida.createQuery(UsuarioEmpresa.class);
		Root<UsuarioEmpresa> fromConsolida = cqConsolida.from(UsuarioEmpresa.class);
		fromConsolida.join(UsuarioEmpresa_.empresa, JoinType.INNER);
		Join<UsuarioEmpresa, Usuario> joinUEU = fromConsolida.join(UsuarioEmpresa_.usuario, JoinType.INNER);
		Join<Usuario, MultOrg> joinUMO = joinUEU.join(Usuario_.multOrg, JoinType.INNER);
		TypedQuery<UsuarioEmpresa> tQuery = entityManager.createQuery(cqConsolida.select(fromConsolida)
				.where(cbConsolida.and(cbConsolida.equal(joinUEU.get(Usuario_.id), credential.getUsuarioId()),
						cbConsolida.equal(joinUMO.get(MultOrg_.cd), credential.getCodigo()))));
		if (!tQuery.getResultList().isEmpty()) {
			List<UsuarioEmpresa> ueCollection = tQuery.getResultList();
			ueCollection.forEach(ue -> {
				codPart.add(ue.getEmpresa().getPessoa().getCodPart());
			});
		}
		entityManager.clear();
		return codPart;
	}

	protected boolean isConsolidaEmpresa(String allCompanies) {

		if (allCompanies != null && allCompanies.equals("1")) {
			return true;
		} else {
			return false;
		}
	}
}
