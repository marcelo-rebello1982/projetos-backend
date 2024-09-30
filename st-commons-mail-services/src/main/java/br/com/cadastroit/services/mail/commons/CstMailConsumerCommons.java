package br.com.cadastroit.services.mail.commons;

import br.com.cadastroit.services.aws.nosql.*;
import br.com.cadastroit.services.aws.nosql.base.S3MailBase;
import br.com.cadastroit.services.aws.nosql.dto.S3MailDto;
import br.com.cadastroit.services.enums.OrigemEmail;
import br.com.cadastroit.services.mail.commons.impl.CstMailCommonsImpl;
import br.com.complianceit.services.common.util.UtilDate;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.sql.Timestamp;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CstMailConsumerCommons implements CstMailCommonsImpl {

	private Logger log;
	private RabbitTemplate rabbitTemplate;
	private MongoTemplate cstMailDb;
	private MongoTemplate awsMailDb;

	public void createObjPublishQueueReSend(Long dmIndEmit, Long dmLegado, Long idNotaFiscal, Long idOracle, String tbOrigem, String email, byte[] xml, byte[] pdf) {
		try {
			S3MailBase s3MailBase = null;
			OrigemEmail origemEmail = null;
			if(dmIndEmit.intValue() == 0 && dmLegado.intValue() == 0) {//Emissao propria
				S3Mail s3 = findS3MailMongo(cstMailDb, (tbOrigem.equals("NOTA_FISCAL_DEST") ? "AUTOMATICO" : "AGENDADO"), idNotaFiscal);
				s3MailBase = createS3MailBase(s3, idNotaFiscal, xml, pdf);
				origemEmail = OrigemEmail.EMISSAO_PROPRIA;
			}else if(dmIndEmit.intValue() == 0 && dmLegado.intValue() != 0) {//Legado
				S3MailLeg s3Leg = findS3MailLegMongo(cstMailDb, (tbOrigem.equals("NOTA_FISCAL_DEST") ? "AUTOMATICO" : "AGENDADO"), idNotaFiscal);
				s3MailBase = createS3MailBase(s3Leg, idNotaFiscal, xml, pdf);
				origemEmail = OrigemEmail.LEGADO;
			} else { //Terceiro
				S3MailTerc s3Terc = findS3MailTercMongo(cstMailDb, (tbOrigem.equals("NOTA_FISCAL_DEST") ? "AUTOMATICO" : "AGENDADO"), idNotaFiscal);
				s3MailBase = createS3MailBase(s3Terc, idNotaFiscal, xml, pdf);
				origemEmail = OrigemEmail.TERCEIRO;
			}
			S3MailDto s3MailDto = createS3MailDto(s3MailBase, tbOrigem, email, idNotaFiscal, idOracle, origemEmail);
			this.rabbitTemplate.convertAndSend("DIRECT-DOCUMENT", "re-envia-email", s3MailDto);
		}catch(Exception ex) {
			log.error("Falha ao tentar publicar mensagem nas filas de reenvio, NOTAFISCAL[ID] => "+idNotaFiscal+", error => "+ex.getMessage());
		}
	}

    public void updateS3MailLog(S3MailLog s3MailLog) {
    	try {
    		Integer retry = s3MailLog.getRetry().intValue();
    		retry++;
			this.cstMailDb.getCollection("s3_mail_log").updateOne(Filters.and(Filters.eq("uuid", s3MailLog.getUuid()),
					Filters.eq("notafiscalId", s3MailLog.getNotafiscalId()),Filters.eq("idOrigem", s3MailLog.getIdOrigem())),
			Updates.combine(Updates.set("retry", retry.longValue())));
			log.info("Atualizado tentavias de reenvio de email mongo para o registro NOTAFISCAL[ID] " + s3MailLog.getNotafiscalId() + " ORIGEM[ID] " + s3MailLog.getIdOrigem() + " tentativas = " + retry + " no NoSQL...");
		}catch(Exception ex) {
			log.error("Falha na atualizacao dos registros de reenvio de email mongo. Erro no acesso aos dados NOSQL, NOTAFISCAL[ID] => " + 
						s3MailLog.getNotafiscalId() + " ORIGEM[ID] " + s3MailLog.getIdOrigem() + ", error => "+ex.getMessage());
		}
    }
	
	public S3Mail findS3MailMongo(MongoTemplate mongoTemplate, String sistema, Long id) {
		S3Mail s3Mail = null;
		try {
			Query query = new Query(Criteria.where("notafiscalId").is(id));
			Optional<S3Mail> obj = Optional.of(mongoTemplate.findOne(query, S3Mail.class));
			if(obj.isPresent()){
				s3Mail = obj.get();
			}
		} catch (Exception e) {
			log.error("Falha na busca dos registros emissao propria. Erro no acesso aos dados NOSQL, "
					+ (sistema.equals("AGENDADO") ? "NFDEST_EMAIL" : "NOTA_FISCAL") + "[ID] => " + id + ", error => "
					+ e.getMessage());
		}
		return s3Mail;
	}
	
	public S3MailBase createS3MailBase(S3Mail s3Mail, Long id, byte[] xml, byte[] pdf) {
		S3MailBase s3MailBase = null;
		if(s3Mail != null) {
			s3MailBase = S3MailBase.builder()
						.notafiscalId(s3Mail.getNotafiscalId())
						.status(s3Mail.getStatus())
						.mailSchedule(s3Mail.getMailSchedule())
						.uuid(s3Mail.getUuid())
						.baseFileName(s3Mail.getBaseFileName())
						.baosPDF(pdf)
						.baosXML(xml)
						.build();			
		}else {
			UUID uuid = UUID.randomUUID();
			s3MailBase = S3MailBase.builder()
					.notafiscalId(id)
					.status(1)
					.mailSchedule(UtilDate.toDateString(new Timestamp(new Date().getTime()), "dd/MM/yyyy HH:mm:ss"))
					.uuid(uuid)
					.baseFileName(uuid.toString()+ "_" + id)
					.baosPDF(pdf)
					.baosXML(xml)
					.build();			
		}
		return s3MailBase;
	}
	
	public S3MailLeg findS3MailLegMongo(MongoTemplate mongoTemplate, String sistema, Long id) {
		S3MailLeg s3MailLeg = null;
		try {
			Query query = new Query(Criteria.where("notafiscalId").is(id));
			Optional<S3MailLeg> obj = Optional.of(mongoTemplate.findOne(query, S3MailLeg.class));
			if(obj.isPresent()){
				s3MailLeg = obj.get();
			}
		} catch (Exception e) {
			log.error("Falha na busca dos registros legado. Erro no acesso aos dados NOSQL, "
					+ (sistema.equals("AGENDADO") ? "NFDEST_EMAIL" : "NOTA_FISCAL") + "[ID] => " + id + ", error => "
					+ e.getMessage());
		}
		return s3MailLeg;
	}
	
	public S3MailBase createS3MailBase(S3MailLeg s3MailLeg, Long id, byte[] xml, byte[] pdf) {
		S3MailBase s3MailBase = null;
		if(s3MailLeg != null) {
			s3MailBase = S3MailBase.builder()
						.notafiscalId(s3MailLeg.getNotafiscalId())
						.status(s3MailLeg.getStatus())
						.mailSchedule(s3MailLeg.getMailSchedule())
						.uuid(s3MailLeg.getUuid())
						.baseFileName(s3MailLeg.getBaseFileName())
						.baosPDF(pdf)
						.baosXML(xml)
						.build();			
		}else {
			UUID uuid = UUID.randomUUID();
			s3MailBase = S3MailBase.builder()
					.notafiscalId(id)
					.status(1)
					.mailSchedule(UtilDate.toDateString(new Timestamp(new Date().getTime()), "dd/MM/yyyy HH:mm:ss"))
					.uuid(uuid)
					.baseFileName(uuid.toString()+ "_" + id)
					.baosPDF(pdf)
					.baosXML(xml)
					.build();			
		}
		return s3MailBase;
	}
	
	public S3MailTerc findS3MailTercMongo(MongoTemplate mongoTemplate, String sistema, Long id) {
		S3MailTerc s3MailTerc = null;
		try {
			Query query = new Query(Criteria.where("notafiscalId").is(id));
			Optional<S3MailTerc> obj = Optional.of(mongoTemplate.findOne(query, S3MailTerc.class));
			if(obj.isPresent()){
				s3MailTerc = obj.get();
			}
		} catch (Exception e) {
			log.error("Falha na busca dos registros terceiro. Erro no acesso aos dados NOSQL, "
					+ (sistema.equals("AGENDADO") ? "NFDEST_EMAIL" : "NOTA_FISCAL") + "[ID] => " + id + ", error => "
					+ e.getMessage());
		}
		return s3MailTerc;
	}
	
	public S3MailBase createS3MailBase(S3MailTerc s3MailTerc, Long id, byte[] xml, byte[] pdf) {
		S3MailBase s3MailBase = null;
		if(s3MailTerc != null) {
			s3MailBase = S3MailBase.builder()
						.notafiscalId(s3MailTerc.getNotafiscalId())
						.status(s3MailTerc.getStatus())
						.mailSchedule(s3MailTerc.getMailSchedule())
						.uuid(s3MailTerc.getUuid())
						.baseFileName(s3MailTerc.getBaseFileName())
						.baosPDF(pdf)
						.baosXML(xml)
						.build();			
		}else {
			UUID uuid = UUID.randomUUID();
			s3MailBase = S3MailBase.builder()
					.notafiscalId(id)
					.status(1)
					.mailSchedule(UtilDate.toDateString(new Timestamp(new Date().getTime()), "dd/MM/yyyy HH:mm:ss"))
					.uuid(uuid)
					.baseFileName(uuid+ "_" + id)
					.baosPDF(pdf)
					.baosXML(xml)
					.build();			
		}
		return s3MailBase;
	}

	public S3MailDto createS3MailDto(S3MailBase base, String tbOracle, String email, Long idNotaFiscal, Long idOracle, OrigemEmail origemEmail) {
		S3MailRetry s3MailRetry = S3MailRetry.builder()
				.uuid(UUID.randomUUID())
				.email(email)
				.idOracle(idOracle)
				.notafiscalId(idNotaFiscal)
				.tbOracle(tbOracle)
				.build();
		S3MailDto s3MailDto = S3MailDto.builder()
				 .s3MailRetry(s3MailRetry)
				 .s3MailBase(base)
				 .sistema(tbOracle.equals("NOTA_FISCAL_DEST") ? "AUTOMATICO" : "AGENDADO")
				 .origemEmail(origemEmail)
				 .build();
		return s3MailDto;
	}
	
	public List<String> tbOrigemDados() {
		List<String> tbOrigemDados = new ArrayList<String>();
		tbOrigemDados.add("NFDEST_EMAIL");
		tbOrigemDados.add("NOTA_FISCAL_DEST");
		return tbOrigemDados;
	}
}
