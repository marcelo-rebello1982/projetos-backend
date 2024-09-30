package br.com.cadastroit.services.mongodb.repository.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.sqs.model.Message;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import br.com.cadastroit.services.exceptions.MongoDBException;
import br.com.cadastroit.services.mongodb.client.MongoDBClient;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;
import br.com.cadastroit.services.repositories.impl.ICollectionRelatorioApiRepository;
import br.com.cadastroit.services.utils.UtilDate;
import br.com.cadastroit.services.web.dto.FiltersDTO;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CollectionRelatorioApiRepository implements ICollectionRelatorioApiRepository {

	@Autowired
	private MongoDBClient mongoDbClient;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void insertDocument(String collection, Map<String, Object> values) throws MongoDBException {

		try {
			mongoTemplate = this.mongoDbClient.mongoTemplate();
			MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(collection);
			Document doc = new Document(values);
			mongoCollection.insertOne(doc);
		} catch (Exception e) {
			throw new MongoDBException(e.getMessage());
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	public long updateCallRelatorioApi(CollectionRelatorioApi request, Long empresaId, String nroProtocolo) throws MongoDBException {

		try {

			mongoTemplate = this.mongoDbClient.mongoTemplate();
			ObjectId obj = this.findMaxIdCallRelatorioApi(mongoTemplate, request, empresaId, nroProtocolo);
			Criteria criteria = new Criteria();
			criteria.andOperator(Criteria.where("_id").is(obj));

			Update update = new Update();
			Query query = new Query(criteria);

			// validar se nenhum campo esta vindo nulo
			// antes de atualizar o Mongo, por hora message esta
			// vindo vazio,neste caso o status ficaria sempre como 0.
			for (Map.Entry<String, Object> entry : toMap(request).entrySet()) {
				if (entry.getValue() != null || entry.getKey().equals("MESSAGES")) {
					update.set(entry.getKey(), entry.getValue());
				} else {
					update.set(entry.getKey(), entry.getValue());
					update.set("STATUS", 0);
				}
			}

			// for (Map.Entry<String, List<Object>> entry : toMap(request).entrySet()) {
			// String chave = entry.getKey();
			// List<Object> valores = entry.getValue();
			// }

			UpdateResult result = mongoTemplate.updateFirst(query, update, CollectionRelatorioApi.class);
			return result != null ? result.getModifiedCount() : 0l;
		} catch (Exception e) {
			throw new MongoDBException(e.getMessage());
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	public ObjectId findMaxIdCallRelatorioApi(MongoTemplate mgTemplate, CollectionRelatorioApi request, Long empresaId, String nroProtocolo)
			throws MongoDBException {

		try {
			mongoTemplate = this.mongoDbClient.mongoTemplate();
			BasicDBObject query = new BasicDBObject();
			query.put("NROPROTOCOLO", nroProtocolo);
			query.put("EMPRESA_ID", empresaId);

			if (request != null && request.getMESSAGES() != null && !request.getMESSAGES().isEmpty()) {
				query.put("MESSAGES", request.getMESSAGES());
			}

			FindIterable<Document> finder = mongoTemplate.getCollection("relatoriocontasdata")
					.find(query)
					.sort(new BasicDBObject("_id", -1))
					.limit(1);

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
		} catch (Exception e) {
			throw new MongoDBException(e.getMessage());
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	public ObjectId findCallRelatorioApiById(MongoTemplate mgTemplate, Long empresaId) throws MongoDBException {

		try {

			mongoTemplate = this.mongoDbClient.mongoTemplate();
			BasicDBObject query = new BasicDBObject();
			query.put("EMPRESA_ID", empresaId);

			FindIterable<Document> finder = mongoTemplate.getCollection("relatoriocontasdata")
					.find(query)
					.sort(new BasicDBObject("_id", -1))
					.limit(1);
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
		} catch (Exception e) {
			throw new MongoDBException(e.getMessage());
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	public List<CollectionRelatorioApi> findCollRelatorioApiByFilters(Long pessoaId, Map<String, Object> requestParams, FiltersDTO filters, String filterOr, String filterAnd, int page, int length)
			throws MongoDBException {

		List<CollectionRelatorioApi> listRelatorioApis = new ArrayList<CollectionRelatorioApi>();

		try {

			if (filters != null) {

				mongoTemplate = this.mongoDbClient.mongoTemplate();

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

				query.addCriteria(Criteria.where("PESSOA_ID").is(pessoaId)).with(Sort.by(Sort.Direction.DESC, "CREATIONDATE")).limit(length);

				listRelatorioApis = mongoTemplate.find(query, CollectionRelatorioApi.class);
			}
		} catch (Exception e) {
			throw new MongoDBException(e.getMessage());
		} finally {
			if (mongoTemplate != null) {
				this.mongoDbClient.closeMongoConnection();
			}
		}
		return listRelatorioApis;
	}

	public void removeCallRelatorioApiData(String collection, Long empresaId, Long abertId) throws MongoDBException {

		try {

			mongoTemplate = this.mongoDbClient.mongoTemplate();
			Criteria criteria = Criteria.where("EMPRESA_ID").is(empresaId).and("ABERT_ID").is(abertId);

			Query query = new Query(criteria);
			mongoTemplate.remove(query, collection);
		} catch (Exception e) {
			throw new MongoDBException(e.getMessage());
		} finally {
			if (mongoTemplate != null)
				this.mongoDbClient.closeMongoConnection();
		}
	}

	private Map<String, Object> toMap(CollectionRelatorioApi request) {

		Map<String, Object> map = new HashMap<>();

		map.put("STATUS", 1);
		map.put("PESSOA_ID", request.getPESSOA_ID());
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

		Map<Integer, Object> map = IntStream.range(0, lista.size()).boxed().collect(Collectors.toMap(i -> i, lista::get));
		return map;
	}

	public HashMap<List<Long>, In<Long>> buildInCondition(Message msg, Long idEmpresa, EntityManagerFactory em) {

		final HashMap<List<Long>, In<Long>> values = new HashMap<>();
		List<Long> empresas = new ArrayList<>();
		return values;
	}
}