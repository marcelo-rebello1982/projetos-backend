package br.com.cadastroit.services.web.controller.commons;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.repositories.CommonsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NfServCommonsController {
	
	@Autowired
	public EntityManagerFactory entityManagerFactory;

	protected final ObjectMapper mapperJson;
    protected final CommonsRepository commonsRepository;
	protected final String EMPTY_MSG = "List is empty...";

    public static Long sum(Tuple result) {
        return Long.sum(Long.sum((Long) result.get("processamento"), (Long) result.get("pendencia")),
                Long.sum((Long) result.get("autorizada"), (Long) result.get("cancelada")));
    }

    public ResponseEntity<Object> validarCabecalho(String uuid, String token) {
        boolean valido = (uuid != null && !uuid.equals("")) && (token != null && !token.equals(""));
        return !valido ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("ApiMessage.COLLECTION_IS_EMPTY.message() -1")
                : null;
    }

    public ResponseEntity<Object> validarCollection(List<?> collection) {
        return collection.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("ApiMessage.COLLECTION_IS_EMPTY.message() -2")
                : ResponseEntity.status(HttpStatus.OK).body(collection);
    }

//    public boolean updateDmStProc(List<Long> invoicesToUpdate, EntityManagerFactory entityManagerFactory)
//            throws NfServException {
//        try {
//            EntityManager entityManager = entityManagerFactory.createEntityManager();
//            entityManager.getTransaction().begin();
//            invoicesToUpdate.stream().map(id -> entityManager.find(NotaFiscal.class, id))
//                    .filter(notaFiscal -> !validateStatus(notaFiscal.getDmStProc()))
//                    .forEach(notaFiscal -> {
//                        notaFiscal.setDmStProc(new BigDecimal(0));
//                        notaFiscal.setLote(null);
//                    });
//            entityManager.getTransaction().commit();
//            return true;
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//    public void resendInvoices(List<Long> invoicesToUpdate, EntityManager entityManager)
//            throws NfServException {
//        try {
//            entityManager.getTransaction().begin();
//            invoicesToUpdate.forEach(id -> {
//                NotaFiscal notaFiscal = entityManager.find(NotaFiscal.class, id);
//                if (!this.validateStatus(notaFiscal.getDmStProc())) {
//                    notaFiscal.setDmStProc(new BigDecimal(0));
//                    notaFiscal.setLote(null);
//                }
//                entityManager.merge(notaFiscal);
//            });
//            entityManager.getTransaction().commit();
//        } catch (Exception ex) {
//            throw new NfServException(ex.getMessage(), ex);
//        }
//    }
//
//    public String convertMapValue(Integer ids) {
//        Map<Integer, String> mapData = new HashMap<>();
//        mapData.put(1, "empresaId");
//        mapData.put(2, "natOperId");
//        mapData.put(3, "idEmpresaDestino");
//        return mapData.entrySet().stream().filter(p -> p.getKey().equals(ids)).findFirst().get().getValue();
//    }
//
//    public boolean validateStatus(BigDecimal dmStProc) {
//        return new HashSet<>(Arrays.asList(4L, 6L, 7L, 8L))
//                .contains(dmStProc.longValue());
//    }
//    
//    public boolean validateStatus(List<Long> acceptedStatus, BigDecimal actualStatus) {
//    	return acceptedStatus.stream()
//    			.anyMatch(accepted -> accepted.equals(actualStatus.longValue()));
//    }
//    
//    //.setDmStProc(this.converterStatusProcessamento(xx.getDmStProc().intValue()));
//    public String converterStatusProcessamento(Integer dmSituacao) {
//        Map<Integer, String> status = new HashMap<>();
//        status.put(1, "VALIDADA");
//        status.put(2, "PROCESSADA");
//        status.put(3, "ENVIADA");
//        status.put(4, "AUTORIZADA");
//        status.put(5, "REJEITADA");
//        status.put(10, "ERRONAVALIDACAO");
//        status.put(11, "ERRONAMONTAGEMDOXML");
//        status.put(12, "ERROAOENVIARANOTA");
//        status.put(13, "ERROAOOBTERORETORNODOENVIODANOTA");
//        status.put(20, "RPSNAOCONVERTIDO");
//        status.put(21, "AGUARDANDOLIBERACAO");
//        status.put(23, "SUBSTITUIDA");
//        status.put(99, "ERROGERALDESISTEMA");
//        return status.entrySet().stream().filter(p -> p.getKey().equals(dmSituacao)).findFirst().get().getValue();
//    }
//
//    // aplica o (mapper) a entidade fornecida.
//    public <T, D> D mountDto(Function<T, D> mapper, T entity) {
//        return mapper.apply(entity);
//    }
//    
//    public <T, R> List<R> mountListDto(Function<T, R> mapper, List<T> list) {
//		return list.stream().map(mapper).collect(Collectors.toList());
//	}

    public HttpHeaders httpHeaders(String count) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("summaryCount", count);
        return headers;
    }
}
