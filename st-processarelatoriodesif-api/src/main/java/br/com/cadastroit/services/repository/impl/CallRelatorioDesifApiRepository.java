package br.com.cadastroit.services.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import br.com.cadastroit.services.api.domain.Filters;
import br.com.cadastroit.services.common.util.UtilDate;
import br.com.cadastroit.services.exceptions.DesifPlanoContaException;
import br.com.cadastroit.services.mongodb.client.MongoDbClient;
import br.com.cadastroit.services.mongodb.domain.CallRelatorioDesifApi;
import br.com.cadastroit.services.repository.api.ICallRelatorioDesifApiRepository;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CallRelatorioDesifApiRepository implements ICallRelatorioDesifApiRepository {

	@Autowired
	private MongoDbClient mongoDbClient;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void insertDocument(String collection, Map<String, Object> values) throws DesifPlanoContaException {

		try {
			mongoTemplate = this.mongoDbClient.mongoTemplate();
			MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(collection);
			Document doc = new Document(values);
			mongoCollection.insertOne(doc);
		} catch (Exception e) {
			throw new DesifPlanoContaException(e);
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	// for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
	// String chave = entry.getKey();
	// List<Object> valores = entry.getValue();
	// // aqui a chave e os valores correspondentes
	// }

	public long updateCallRelatorioDesifApi(CallRelatorioDesifApi request, Long empresaId, String nroProtocolo)
			throws DesifPlanoContaException {

		MongoTemplate mongoTemplate = null;

		try {

			mongoTemplate = this.mongoDbClient.mongoTemplate();
			ObjectId obj = this.findMaxIdCallRelatorioDesifApi(mongoTemplate, request, empresaId, nroProtocolo);
			Criteria criteria = new Criteria();
			criteria.andOperator(Criteria.where("_id").is(obj));

			Update update = new Update();
			Query query = new Query(criteria);

			//validar se nenhum campo esta vindo nulo
			//antes de atualizar o Mongo, por hora message esta
			//vindo vazio,neste caso o status ficaria sempre como 0.
			for (Map.Entry<String, Object> entry : toMap(request).entrySet()) {
				if (entry.getValue() != null || entry.getKey().equals("MESSAGES")) { 
					update.set(entry.getKey(), entry.getValue());                 
				} else {
					update.set(entry.getKey(), entry.getValue());
					update.set("STATUS", 0);
				}
			}
			UpdateResult result = mongoTemplate.updateFirst(query, update, CallRelatorioDesifApi.class);
			return result != null ? result.getModifiedCount() : 0l;
		} catch (Exception e) {
			throw new DesifPlanoContaException(e);
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	public ObjectId findMaxIdCallRelatorioDesifApi(MongoTemplate mgTemplate, CallRelatorioDesifApi request,
			Long empresaId, String nroProtocolo) throws DesifPlanoContaException {

		try {
			mgTemplate = this.mongoDbClient.mongoTemplate();
			BasicDBObject query = new BasicDBObject();
			query.put("NROPROTOCOLO", nroProtocolo);
			query.put("EMPRESA_ID", empresaId);

			if (request != null && request.getMESSAGES() != null && !request.getMESSAGES().isEmpty()) {
				query.put("MESSAGES", request.getMESSAGES());
			}

			FindIterable<Document> finder = mgTemplate.getCollection("relatoriodesifdata").find(query)
					.sort(new BasicDBObject("_id", -1)).limit(1);

			Document document = finder.iterator().next();
			final AtomicReference<String> atomic = new AtomicReference<String>();

			document.entrySet().forEach(v -> {
				if (v.getKey().equals("_id")) {
					try {
						atomic.set(v.getValue().toString());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			});
			return document == null ? null : new ObjectId(atomic.get());
		} catch (DesifPlanoContaException e) {
			return null;
		}
	}

	public ObjectId findCallRelatorioDesifApiById(MongoTemplate mgTemplate, Long empresaId)
			throws DesifPlanoContaException {
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("EMPRESA_ID", empresaId);

			FindIterable<Document> finder = mgTemplate.getCollection("relatoriodesifdata").find(query)
					.sort(new BasicDBObject("_id", -1)).limit(1);
			Document document = finder.iterator().next();
			final AtomicReference<String> atomic = new AtomicReference<String>();
			document.entrySet().forEach(v -> {
				if (v.getKey().equals("_id")) {
					try {
						atomic.set(v.getValue().toString());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			});
			return document == null ? null : new ObjectId(atomic.get());
		} catch (DesifPlanoContaException e) {
			return null;
		}
	}

	public List<CallRelatorioDesifApi> findCallRelatorioDesifApiByFilters(Long empresaId,
			Map<String, Object> requestParams, Filters filters, String filterOr, String filterAnd, int page, int length)
			throws DesifPlanoContaException {

		List<CallRelatorioDesifApi> listRelatorioDesifApis = new ArrayList<CallRelatorioDesifApi>();
		
		try {
			if (filters != null) {

				Query query = new Query();
				String dataIni = UtilDate.toDateString(filters.getDataInicial());
				String dataFim = UtilDate.toDateString(filters.getDataFinal());

				if (filters != null && filters.getDataInicial() != null && filters.getDataFinal() != null) {
					query.addCriteria(Criteria.where("CREATIONDATE").gte(dataIni).lte(dataFim));
				}

				if (filters.getProtocolo() != null) {
					query.addCriteria(Criteria.where("NROPROTOCOLO").regex(filters.getProtocolo(), "i"));
				}

				if (filters.getStatus() > 0) {
					query.addCriteria(Criteria.where("STATUS").is(filters.getStatus()));
				}

				query.addCriteria(Criteria.where("EMPRESA_ID").is(empresaId))
						.with(Sort.by(Sort.Direction.DESC, "CREATIONDATE")).limit(length);

				listRelatorioDesifApis = this.mongoTemplate.find(query, CallRelatorioDesifApi.class);
			}
		} catch (DesifPlanoContaException e) {
			return null;
		} finally {
			if (mongoTemplate != null) {
				// this.mongoDbClient.closeMongoConnection();
			}
		}
		return listRelatorioDesifApis;
	}

	public void removeCallRelatorioDesifApiData(String collection, Long empresaId, Long abertId)
			throws DesifPlanoContaException {
		MongoTemplate mgTemplate = null;
		try {
			mgTemplate = this.mongoDbClient.mongoTemplate();

			Criteria criteria = Criteria.where("EMPRESA_ID").is(empresaId).and("ABERT_ID").is(abertId);

			Query query = new Query(criteria);
			mgTemplate.remove(query, collection);
		} catch (Exception e) {
			throw new DesifPlanoContaException(e);
		} finally {
			if (mgTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	private Map<String, Object> toMap(CallRelatorioDesifApi request) {
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("STATUS", 1);
		map.put("EMPRESA_ID", request.getEMPRESA_ID());
		map.put("DESCRIPTION", request.getDESCRIPTION());
		map.put("MESSAGES", request.getMESSAGES());
		map.put("URLS3", request.getURLS3());
		map.put("TYPEARCHIVE", request.getTYPEARCHIVE());
		map.put("NAMEARCHIVE", request.getNAMEARCHIVE());
		map.put("CREATIONDATE", request.getCREATIONDATE());
		map.put("NROPROTOCOLO", request.getNROPROTOCOLO());
		map.put("REFERENCE", request.getREFERENCE());
		
		return map;
	}
	
	 public Map<Integer, Object> converterListaParaMap(List<String> lista) {
	        Map<Integer, Object> map = IntStream.range(0, lista.size())
	        					.boxed()
	        						.collect(Collectors.toMap(i -> i, lista::get));
	        return map;
	 }
	 
	public HashMap<List<Long>, In<Long>> buildInCondition(Message msg, Long idEmpresa, EntityManagerFactory em) {
			final HashMap<List<Long>, In<Long>> values = new HashMap<>();
			List<Long> empresas = new ArrayList<>();
			return values;
	}

	@Override
	public List<CallRelatorioDesifApi> findMaxIdCallRelatorioDesifApiPagination(MongoTemplate mgTemplate,
			CallRelatorioDesifApi request, Long empresaId, String nroProtocolo) throws DesifPlanoContaException {
		return null;
	}
}