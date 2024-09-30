package br.com.cadastroit.services.repository.api;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import br.com.cadastroit.services.exceptions.DesifPlanoContaException;
import br.com.cadastroit.services.mongodb.domain.CallRelatorioDesifApi;

public interface ICallRelatorioDesifApiRepository {

	public void insertDocument(String collection, Map<String, Object> values) throws DesifPlanoContaException;
	public ObjectId findMaxIdCallRelatorioDesifApi(MongoTemplate mgTemplate, CallRelatorioDesifApi request, Long empresaId, String nroProtocolo) throws DesifPlanoContaException;
	public List<CallRelatorioDesifApi> findMaxIdCallRelatorioDesifApiPagination(MongoTemplate mgTemplate, CallRelatorioDesifApi request, Long empresaId, String nroProtocolo) throws DesifPlanoContaException;
	public void removeCallRelatorioDesifApiData(String collection, Long empresaId, Long abertId) throws DesifPlanoContaException;
}


