package br.com.cadastroit.services.repositories.impl;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import br.com.cadastroit.services.exceptions.BusinessException;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;

public interface ICollectionRelatorioApiRepository {

	public void insertDocument(String collection, Map<String, Object> values) throws BusinessException;
	public ObjectId findMaxIdCallRelatorioApi(MongoTemplate mgTemplate, CollectionRelatorioApi request, Long empresaId, String nroProtocolo) throws BusinessException;
	public void removeCallRelatorioApiData(String collection, Long empresaId, Long abertId) throws BusinessException;
	
}