package br.com.cadastroit.services.mail.commons;

import br.com.cadastroit.services.aws.nosql.S3MailLog;
import br.com.cadastroit.services.aws.nosql.S3MailRetry;
import br.com.cadastroit.services.aws.nosql.base.S3MailBase;
import br.com.cadastroit.services.aws.nosql.dto.S3MailDto;
import br.com.cadastroit.services.enums.OrigemEmail;
import br.com.cadastroit.services.mail.commons.impl.CstMailCommonsImpl;
import br.com.cadastroit.services.mail.domain.nosql.CSFBlacklist;
import br.com.cadastroit.services.mail.domain.nosql.CSFMailBlacklist;
import br.com.cadastroit.services.mail.domain.nosql.CstMailBlock;
import br.com.cadastroit.services.mail.enums.NotaFiscalEnum;
import br.com.cadastroit.services.mail.model.Email;
import br.com.cadastroit.services.mail.sender.CstMailSender;
import br.com.cadastroit.services.commons.domain.CsfTipoLog;
import br.com.cadastroit.services.commons.domain.EmpresaCommons;
import br.com.cadastroit.services.commons.domain.LogGenerico;
import br.com.cadastroit.services.commons.services.CsfTipoLogService;
import br.com.cadastroit.services.commons.services.LogGenericoService;
import br.com.cadastroit.services.jdbc.JdbcImpl;
import br.com.cadastroit.services.nfe.domain.NfdestEmail;
import br.com.cadastroit.services.nfe.domain.NotaFiscal;
import br.com.cadastroit.services.nfe.domain.NotaFiscalDest;
import br.com.cadastroit.services.nfe.services.DominioService;
import br.com.cadastroit.services.nfe.services.NotaFiscalDestService;
import br.com.cadastroit.services.nfe.services.NotaFiscalService;
import br.com.cadastroit.services.persistence.EntityManagerAPI;
import br.com.cadastroit.services.profile.services.ParamGeralEmailService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CstMailCommons implements CstMailCommonsImpl {

    private NotaFiscalService notafiscalService;
    private NotaFiscalDestService notafiscalDestService;
    private ParamGeralEmailService paramGeralEmailService;
    private DominioService dominioService;
    private CsfTipoLogService csfTipoLogService;
    private LogGenericoService logGenericoService;
    private Logger log;
    private org.apache.logging.log4j.Logger logService;
    private S3MailBase s3Mail;
    private EntityManagerFactory entityManagerFactory;
    private NotaFiscal notafiscal;
    private MongoTemplate cstMailDb;
    private MongoTemplate awsMailDb;
    private RabbitTemplate rabbitTemplate;

    private CstMailSender cstMailSender(String emailPlain) {
        return CstMailSender.builder()
                .email(emailPlain)
                .empresa(notafiscal.getEmpresa())
                .entityManagerFactory(this.entityManagerFactory)
                .juridica(notafiscal.getEmpresa().getPessoa().getJuridica())
                .logger(log)
                .notafiscal(notafiscal)
                .paramGeralEmailService(this.paramGeralEmailService)
                .pessoa(notafiscal.getEmpresa().getPessoa())
                .s3Mail(s3Mail)
                .build();
    }

    private void loggingInfoMail(String email) {
        Optional<CstMailBlock> cstMailBlock = Optional.ofNullable(this.awsMailDb.findOne(new Query(Criteria.where("email").is(email)), CstMailBlock.class));
        CstMailBlock mailBlock = null;
        if (!cstMailBlock.isPresent()) {
            mailBlock = CstMailBlock.builder().date(new Date(System.currentTimeMillis()).getTime())
                    .empresaId(this.notafiscal.getEmpresa() != null ? this.notafiscal.getEmpresa().getId() : 0l)
                    .times(1l)
                    .uuid(UUID.randomUUID())
                    .email(email)
                    .build();
            this.awsMailDb.save(mailBlock);
            log.info(String.format("Mail block registrado, uuid = %s, times = %s", mailBlock.getUuid(), mailBlock.getTimes()));
        } else {
            mailBlock = cstMailBlock.get();
            long times = mailBlock.getTimes() + 1;
            UpdateResult ur = this.awsMailDb.getCollection("cst_mail_block").updateOne(Filters.eq("email", email), Updates.combine(Updates.set("times", times)));
            log.info(String.format("Mail block atualizado, uuid = %s, times = %s. Total de linhas afetadas = %s", mailBlock.getUuid(), mailBlock.getTimes(), ur.getModifiedCount()));
        }
    }

    private boolean blacklistMember(String email) {
        log.info(String.format("Verificando email. Blacklist => %s. Acessando blacklist => %s", email, this.awsMailDb.getDb().getName()));
        Optional<CSFMailBlacklist> valueMailBlackList = Optional.ofNullable(this.awsMailDb.findOne(new Query(Criteria.where("email").is(email.toLowerCase())), CSFMailBlacklist.class));
        boolean r = valueMailBlackList.isPresent();
        if (!r) {
            log.info(String.format("Email %s da csfmailblacklist, email = %s. Procurando na csfblacklist....", (r ? "faz parte" : "nao faz parte"), email));
            Optional<CSFBlacklist> valueBlackList = Optional.ofNullable(this.awsMailDb.findOne(new Query(Criteria.where("email").is(email)), CSFBlacklist.class));
            r = valueBlackList.isPresent();
        }
        log.info(String.format("Email %s da blacklist => %s", (r ? "faz parte" : "nao faz parte"), email));
        if (r) {//Gerando log
            this.loggingInfoMail(email);
        }
        return r;
    }

    public LinkedList<Email> emailsAutomaticos() {
        final LinkedList<Email> emails = new LinkedList<>();
        try {
            Set<NotaFiscalDest> destinatarios = this.notafiscal.getNotaFiscalDestCollection();
            destinatarios.stream().forEach(dest -> {
                if (dest.getEmail() != null) {
                    String[] emailsDest = dest.getEmail().replace(";", ",").split(",");
                    Stream.of(emailsDest).forEach(email -> {
                        String emailValida = (email != null ? email.trim() : "");
                        if (emailValida != null && !emailValida.equals("")) {
                            log.info("Envio de email automatico [NOTA_FISCAL_DEST.EMAIL] = " + emailValida + ".");
                            Email emailObj = Email.builder().id(dest.getId()).email(emailValida).build();
                            emails.add(emailObj);
                        } else {
                            log.info("E-mail vazio para [NOTA_FISCAL.ID] = " + notafiscal.getId());
                        }
                    });
                }
            });
        } catch (Exception ex) {
            log.error("Falha ao recuperar emails para envio automatico, NOTA_FISCAL[ID] => " + this.s3Mail.getNotafiscalId() + ". Envio de email de NOTA EMISSAO PROPRIA, error => " + ex.getMessage());
        }
        return emails;
    }

    public LinkedList<Email> emailsAgendados() {
        final LinkedList<Email> emails = new LinkedList<Email>();
        try {
            Set<NotaFiscalDest> destinatarios = this.notafiscal.getNotaFiscalDestCollection();
            destinatarios.stream().forEach(dest -> {
                List<NfdestEmail> destMailCollection = new ArrayList<>();
                try {
                    NotaFiscalDest notafiscalDest = this.notafiscalDestService.recuperarColecaoDeEmailsPorDestinatario(dest.getId(), entityManagerFactory);
                    if (notafiscalDest.getNfdestEmailCollection() != null && !notafiscalDest.getNfdestEmailCollection().isEmpty()) {
                        destMailCollection.addAll(notafiscalDest.getNfdestEmailCollection());
                    }
                } catch (Exception ex) {
                    log.error("Falha ao recuperar lista de emails para o destinatario[ID] = " + dest.getId());
                }

                if (!destMailCollection.isEmpty()) {
                    destMailCollection.forEach(nfdestEmail -> {
                        String[] emailsDest = nfdestEmail.getEmail().replace(";", ",").split(",");
                        if (emailsDest.length > 1) {
                            Stream.of(emailsDest).forEach(email -> {
                                String emailValida = (email != null ? email.trim() : "");
                                if (emailValida != null && !emailValida.equals("")) {
                                    log.info("Envio de email agendado [NFDEST_EMAIL.EMAIL] = " + emailValida + ".");
                                    Email emailObj = Email.builder().id(nfdestEmail.getId()).email(emailValida).build();
                                    emails.add(emailObj);
                                } else {
                                    log.info("E-mail vazio para [NFDEST_EMAI.ID] = " + nfdestEmail.getId());
                                }
                            });
                        } else {
                            log.info("Envio de email agendado [NFDEST_EMAIL.EMAIL] = " + emailsDest[0] + ".");
                            Email emailObj = Email.builder().id(nfdestEmail.getId()).email(emailsDest[0]).build();
                            emails.add(emailObj);
                        }
                    });
                }
            });
        } catch (Exception ex) {
            log.error("Falha ao recuperar emails para envio agendado, NOTA_FISCAL[ID] => " + this.s3Mail.getNotafiscalId() + ". Envio de email de NOTA EMISSAO PROPRIA, error => " + ex.getMessage());
        }
        return emails;
    }

    public void atualizarDmStEmailNotaFiscal(Long idNota, BigDecimal dmStEmail, boolean block, Connection connection) {
        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE NOTA_FISCAL SET DM_ST_EMAIL = ? WHERE ID = ?")) {
            pstmt.setBigDecimal(1, dmStEmail);
            pstmt.setLong(2, idNota);
            int rows = pstmt.executeUpdate();
            if (rows >= 0) {
                log.info(String.format("%s NOTAFISCAL_ID[" + idNota + "]%s. Campo DM_ST_EMAIL atualizado para %s", (!block ? "Email enviado com sucesso para" : "Existem emails bloqueados para a"), (!block ? "" : "EMAILS INVALIDOS"), dmStEmail.toString()));
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar CAMPO DM_ST_EMAIL da nota NOTAFISCAL_ID[" + idNota + "], erro => " + e.getMessage());
        }
    }

    public void atualizarDmStEmailNfDestEmail(Long idNfDestEmail, BigDecimal dmStEmail, Connection connection) {
        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE NFDEST_EMAIL SET DM_ST_EMAIL = ? WHERE ID = ?")) {
            pstmt.setBigDecimal(1, dmStEmail);
            pstmt.setLong(2, idNfDestEmail);
            int rows = pstmt.executeUpdate();
            if (rows >= 0) {
                log.info("Registro atualizado com sucesso para NFDESTEMAIL_ID[" + idNfDestEmail + "]. Campo DM_ST_EMAIL atualizado para " + dmStEmail.toString());
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar CAMPO DM_ST_EMAIL da nota NFDESTEMAIL_ID[" + idNfDestEmail + "], erro => " + e.getMessage());
        }
    }

    public void finalizarEnvioEmail(boolean result, OrigemEmail origemEmail) {
        try {
            AtomicReference<String> baosPDF = new AtomicReference<>("");
            AtomicReference<String> baosXML = new AtomicReference<>("");

            String collection = "";
            String collectionUpload = "";

            switch (origemEmail) {
                case EMISSAO_PROPRIA:
                    collection = "s3_mail";
                    collectionUpload = "s3Upload";
                    break;
                case TERCEIRO:
                    collection = "s3_mail_terc";
                    collectionUpload = "s3UploadTerc";
                    break;
                case LEGADO:
                    collection = "s3_mail_leg";
                    collectionUpload = "s3UploadLeg";
                    break;
                default:
                    break;
            }

            FindIterable<Document> values = this.cstMailDb.getCollection(collectionUpload).find(Filters.eq("notafiscalId", this.s3Mail.getNotafiscalId())).limit(1);
            values.forEach(c -> {
                AtomicBoolean updated = new AtomicBoolean(false);
                c.entrySet().stream().forEach(entry -> {
                    String key = entry.getKey();
                    if (key.equals("bucketLocation")) {
                        String value = entry.getValue().toString();
                        baosPDF.set(value.replace("xml", "pdf"));
                        baosXML.set(value.replace("pdf", "xml"));
                        updated.set(true);
                    }
                    if (updated.get()) return;
                });
            });

            if (!collection.equals("")) {
                this.cstMailDb.getCollection(collection).updateOne(Filters.and(Filters.eq("uuid", this.s3Mail.getUuid()),
                                Filters.eq("notafiscalId", this.s3Mail.getNotafiscalId())),
                        Updates.combine(Updates.set("baosPDF", baosPDF.get().getBytes()),
                                Updates.set("baosXML", baosXML.get().getBytes()),
                                Updates.set("mailSchedule", UtilString.toDateString(new Timestamp(new Date(System.currentTimeMillis()).getTime()), "dd/MM/yyyy HH:mm:ss")),
                                Updates.set("status", (result ? 2 : 3))));
                log.info("Enderecos atualizados para o registro NOTAFISCAL[ID] " + this.s3Mail.getNotafiscalId() + " no NoSQL...");
            } else {
                log.error(String.format("Erro na atualizacao da collection para o envio de emails de %s", origemEmail.toString()));
            }
        } catch (Exception ex) {
            log.error("Falha na atualizacao dos registros. Erro no acesso aos dados NOSQL, NOTAFISCAL[ID] => " + this.s3Mail.getNotafiscalId() + ", error => " + ex.getMessage());
        }
    }


    public String gerarInformacoesEnvioEmailNotaFiscal(boolean tabular, boolean showHeader, boolean lastRecord) {
        StringBuffer sbInfo = new StringBuffer();
        StringBuffer sbHeader = new StringBuffer();
        NotaFiscalEnum nfEnum = UtilString.intToEnum(this.notafiscal.getDmStProc().intValue());
        if (!tabular) {
            sbInfo.append("Empresa.: ").append(this.notafiscal.getEmpresa().getPessoa().getNome()).append("\n");
            sbInfo.append("Numero..: ").append(this.notafiscal.getNroNf().toString()).append("\n");
            sbInfo.append("Serie...: ").append(this.notafiscal.getSerie()).append("\n");
            sbInfo.append("Modelo..: ").append(this.notafiscal.getModfiscal().getCodMod()).append(" - ").append(this.notafiscal.getModfiscal().getDescr()).append("\n");
            sbInfo.append("Dt.Emiss: ").append(UtilString.toDateTimeString(this.notafiscal.getDtEmiss())).append("\n");
            try {
                sbInfo.append("Tp.Emiss: ").append(this.dominioService.recuperarDominio("NOTA_FISCAL.DM_IND_EMIT", this.notafiscal.getDmIndEmit().toString(), this.entityManagerFactory).getDescr()).append("\n");
                sbInfo.append("Operacao: ").append(this.dominioService.recuperarDominio("NOTA_FISCAL.DM_IND_OPER", this.notafiscal.getDmIndOper().toString(), this.entityManagerFactory).getDescr()).append("\n");
            } catch (Exception ex) {

            }
            if (this.notafiscal.getNroChaveNfe() != null && this.notafiscal.getNroChaveNfe().length() > 0) {
                sbInfo.append("Chave...: ").append(this.notafiscal.getNroChaveNfe()).append("\n");
            }
            sbInfo.append("Situacao: ").append(UtilString.dmStProcToString(nfEnum)).append("\n");
            if (this.notafiscal.getCodMsg() != null && this.notafiscal.getMotivoResp() != null) {
                sbInfo.append("Retorno.: ").append(this.notafiscal.getCodMsg()).append(" - ").append(this.notafiscal.getMotivoResp()).append("\n");
            }
        } else {
            sbInfo.append("| ").append(UtilString.substRPad(this.notafiscal.getEmpresa().getPessoa().getNome(), 30)).append(" ");
            sbInfo.append("| ").append(UtilString.substRPad(this.notafiscal.getNroNf().toString(), 12)).append(" ");
            sbInfo.append("| ").append(UtilString.substRPad(this.notafiscal.getSerie(), 11)).append(" ");
            sbInfo.append("| ").append(UtilString.substRPad(this.notafiscal.getModfiscal().getCodMod(), 2)).append(" - ").append(UtilString.substRPad(this.notafiscal.getModfiscal().getDescr(), 20)).append(" ");
            sbInfo.append("| ").append(UtilString.toDateTimeString(this.notafiscal.getDtEmiss())).append(" ");
            try {
                sbInfo.append("| ").append(UtilString.substRPad(this.dominioService.recuperarDominio("NOTA_FISCAL.DM_IND_EMIT", this.notafiscal.getDmIndEmit().toString(), this.entityManagerFactory).getDescr(), 15)).append(" ");
                sbInfo.append("| ").append(UtilString.substRPad(this.dominioService.recuperarDominio("NOTA_FISCAL.DM_IND_OPER", this.notafiscal.getDmIndOper().toString(), this.entityManagerFactory).getDescr(), 14));
            } catch (Exception ex) {

            }
            sbInfo.append(" |");
            sbInfo.append("\n");
            if (showHeader) {
                sbHeader.append(StringUtils.rightPad("+", 147, '-')).append("+").append("\n");// Linha 1
                sbHeader.append("| -- EMPRESA ------------------- ");// Cabecalho
                sbHeader.append("| -- NR.NF. -- ");
                sbHeader.append("| -- S�RIE -- ");
                sbHeader.append("| -- MODELO. -------------- ");
                sbHeader.append("| ---- DT.EMISS. ---- ");
                sbHeader.append("| -- TP.EMISS. -- ");
                sbHeader.append("| -- TP.OPER. -- ").append("|\n");
                sbHeader.append(StringUtils.rightPad("+", 147, '-')).append("+").append("\n");// Linha 2
                sbHeader.append(sbInfo);
                if (lastRecord) {
                    sbHeader.append(StringUtils.rightPad("+", 147, '-')).append("+").append("\n");
                }
                return sbHeader.toString();
            }
            if (lastRecord) {
                sbInfo.append(StringUtils.rightPad("+", 147, '-')).append("+").append("\n");
            }
        }
        return sbInfo.toString();
    }

    public LogGenerico logGenerico(CsfTipoLog cstTipoLog, EmpresaCommons empresa) {
        return LogGenerico.builder().csfTipoLog(cstTipoLog)
                .dmEnvEmail(false)
                .dmImpressa(empresa != null ? new BigDecimal(0) : new BigDecimal(2))
                .dtHrLog(new Timestamp(new Date(System.currentTimeMillis()).getTime()))
                .empresaCommons(empresa)
                .mensagem("")
                .objReferencia("NOTA_FISCAL")
                .processoId(new BigDecimal("0"))
                .referenciaId(new BigDecimal(this.notafiscal.getId()))
                .resumo("")
                .build();
    }

    private S3MailLog validarEnvioEmail(Email email, NotaFiscal nf, String origem, Long status) {
        S3MailLog s3MailLog = this.cstMailDb.findOne(new Query(Criteria.where("idOrigem").is(email.getId())), S3MailLog.class);
        if (s3MailLog != null) {
            return s3MailLog;
        } else {
            S3MailLog s3MailLogNew = S3MailLog.builder().notafiscalId(s3Mail.getNotafiscalId())
                    .notafiscalId(nf.getId())
                    .idOrigem(email.getId())
                    .table(origem.equals("AUTOMATICO") ? "NOTA_FISCAL_DEST" : "NFDEST_EMAIL")
                    .status(status)
                    .uuid(UUID.randomUUID())
                    .build();
            this.cstMailDb.save(s3MailLogNew);
            return s3MailLogNew;
        }
    }

    public void enviarDocumentoPorEmail(CstMailCommons cstMailCommons, S3MailBase s3Mail, LinkedList<Email> emails, NotaFiscal nf, String sistema, OrigemEmail origemEmail) {
        final EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityManagerAPI entityManagerAPI = EntityManagerAPI.builder().logger(this.log).build();
        try {
            Class.forName(JdbcImpl.driver);
            try (Connection connection = DriverManager.getConnection(JdbcImpl.url, JdbcImpl.user, JdbcImpl.pass)) {
                AtomicBoolean statusFromRecord = new AtomicBoolean(updateOracle.equals(1) ? false : true);
                AtomicBoolean blacklist = new AtomicBoolean(false);
                AtomicBoolean violacao  = new AtomicBoolean();
                emails.stream().forEach(data -> {
                    violacao.set(false);//Variável de controle => Verifica se houve violação no envio do email

                    String logInfo = "";
                    String email = data.getEmail();
                    String emailPlain = email.replace(",", "").replace(";", "");
                    boolean result = updateOracle.equals(1) ? false : true;
                    log.info("Enviando email para " + emailPlain + ", NOTAFISCAL[ID] = " + s3Mail.getNotafiscalId());
                    CstMailSender cstMailSender = cstMailCommons.cstMailSender(emailPlain);
                    if (sendEmail.equals(1)) {
                        S3MailLog s3MailLog = this.validarEnvioEmail(data, nf, sistema, (sistema.equals("AUTOMATICO") ? 6l : 5l));
                        if (sistema.equals("AGENDADO")) {
                            this.atualizarDmStEmailNfDestEmail(data.getId(), new BigDecimal(5), connection);
                        }

                        try {
                            if (!this.blacklistMember(emailPlain.toLowerCase())) {
                                result = cstMailSender.enviarNotaPorEmail(nf.getDmStEmail().intValue());
                            } else {
                                blacklist.set(true);
                                logInfo = String.format("Erro no envio do email %s. Email bloqueado. Verifique se o email e valido.", emailPlain);
                            }
                            if (!statusFromRecord.get()) {
                                statusFromRecord.set(result);
                            }
                        } catch (Exception ex) {
                            if (this.validateException(ex)) {
                                violacao.set(true);
//                                S3MailRetry s3MailRetry = this.findMongoMailRetry(nf, emailPlain, data.getId(), sistema);
//                                if (s3MailRetry != null) {
//                                    S3MailDto s3MailDto = S3MailDto.builder()
//                                            .s3MailRetry(s3MailRetry)
//                                            .s3MailBase(s3Mail)
//                                            .sistema(sistema)
//                                            .build();
//                                    this.publishContigenQueue(nf, s3MailDto);
//                                }
                                log.warn(String.format("Email com codigo de violacao no envio, [Email] = %s, NotaFiscal[Id] = %s. Efetuando nova tentativa de envio.", email,nf.getId()));
                                if (sistema.equals("AGENDADO")) {
                                    this.atualizarDmStEmailNfDestEmail(data.getId(), new BigDecimal(4), connection);
                                }
                            }
                        }
                        if (!violacao.get() && updateOracle.equals(1)) {
                            try {
                                logInfo += result ? "E-mail da NF-e enviado ao(s) destinatario(s) " + email + " pelo SISTEMA " + sistema + "."
                                        : "Nao foi possivel enviar o e-mail da NF-e ao(s) destinatario(s) " + email + " pelo SISTEMA " + sistema + ".";
                                String nfeStr = cstMailCommons.gerarInformacoesEnvioEmailNotaFiscal(false, false, true);
                                String cd = !result ? CsfTipoLog.Cd.AVISO_ENV_EMAIL_DEST_NFE.toString() : CsfTipoLog.Cd.INFO_ENV_EMAIL_DEST_NFE.toString();
                                CsfTipoLog csfTipoLog = this.csfTipoLogService.findByCd(cd, entityManager);

                                EmpresaCommons empresaCommons = EmpresaCommons.builder()
                                        .id(nf.getEmpresa().getId()).build();

                                LogGenerico logGenerico = cstMailCommons.logGenerico(csfTipoLog, empresaCommons);
                                logGenerico.setResumo(logInfo);
                                logGenerico.setMensagem(logInfo + "\n\n" + nfeStr);
                                this.logGenericoService.log(logGenerico, logService, entityManager);
                                if (sistema.equals("AGENDADO")) {
                                    cstMailCommons.atualizarDmStEmailNfDestEmail(data.getId(), (statusFromRecord.get() ? new BigDecimal(2) : new BigDecimal(3)), connection);
                                }
                            } catch (Exception ex) {
                                log.error("Erro ao gerar logs para NOTAFISCAL_ID " + nf.getId() + ", erro => " + ex.getMessage());
                            }
                        } else {
                            log.info("Os registros nao foram atualizados no Oracle!!! Para que sejam atualizados, defina o valor da variavel \"UPDATE_ORACLE\" para 1");
                        }
                        if (s3MailLog != null) {
                            this.cstMailDb.getCollection("s3_mail_log").updateOne(Filters.eq("idOrigem", data.getId()),
                                    Updates.set("status", 2));
                        }
                    }
                });
                if (updateOracle.equals(1)) {
                    cstMailCommons.atualizarDmStEmailNotaFiscal(nf.getId(), (statusFromRecord.get() ? new BigDecimal(2) : new BigDecimal(3)), blacklist.get(), connection);
                }
                cstMailCommons.finalizarEnvioEmail(statusFromRecord.get(), origemEmail);
            } catch (SQLException ex) {
                log.error("Falha ao recuperar dados NOTA_FISCAL[ID] => " + s3Mail.getNotafiscalId() + ". Envio de email de NOTA EMISSAO PROPRIA, error => " + ex.getMessage());
            } finally {
                entityManagerAPI.closeConnection(entityManager);
            }
        } catch (ClassNotFoundException ex) {
            log.error("Oracle JDBC Driver nao identificado, [error] = " + ex.getMessage());
        }
    }

    private void publishContigenQueue(NotaFiscal nf, S3MailDto s3MailDto) {
        try {
            if (nf.getDmIndEmit().intValue() == 0 && nf.getDmLegado().intValue() == 0) {
                this.rabbitTemplate.convertAndSend("DIRECT-DOCUMENT", "envia-email-contingencia", s3MailDto);
            } else if (nf.getDmIndEmit().intValue() == 0 && nf.getDmLegado().intValue() != 0) {
                this.rabbitTemplate.convertAndSend("DIRECT-DOCUMENT", "envia-email-legado-contingencia", s3MailDto);
            } else if (nf.getDmIndEmit().intValue() == 1 && nf.getDmLegado().intValue() == 0) {
                this.rabbitTemplate.convertAndSend("DIRECT-DOCUMENT", "envia-email-terceiro-contingencia", s3MailDto);
            }
        } catch (Exception e) {
            log.error("Falha ao tentar publicar mensagem nas filas de contingecia, NOTAFISCAL[ID] => " + nf.getId() + ", error => " + e.getMessage());
        }
    }

    private Message buildMessageSchedule(String value) throws Exception {
        try {
            int seconds = 120;
            int time = seconds * 1000;
            if (System.getenv("RETRY-MAIL-MONGO-TIME") != null && !System.getenv("RETRY-MAIL-MONGO-TIME").equals("") && Integer.parseInt(System.getenv("RETRY-MAIL-MONGO-TIME")) > 120) {
                String printTime = System.getenv("RETRY-MAIL-MONGO-TIME");
                seconds = Integer.parseInt(printTime);
                time = seconds * 1000;
            }
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setHeader(MessageProperties.X_DELAY, time);
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            log.info("Next check in " + seconds + " seconds");
            return new Message(value.getBytes(), messageProperties);
        } catch (Exception e) {
            throw new Exception("Fail build to message schedule mongo error => " + e.getMessage());
        }
    }

    private S3MailRetry findMongoMailRetry(NotaFiscal nf, String email, Long idOracle, String sistema) {
        S3MailRetry s3MailRetry = null;
        try {
            s3MailRetry = this.cstMailDb.findOne(new Query(Criteria.where("notafiscalId").is(nf.getId()).and("email").is(email.trim())),S3MailRetry.class);
            if(s3MailRetry == null) {
                s3MailRetry = S3MailRetry.builder()
                        .uuid(UUID.randomUUID())
                        .email(email)
                        .notafiscalId(nf.getId())
                        .idOracle(idOracle)
                        .tbOracle(sistema.equals("AUTOMATICO") ? "NOTA_FISCAL_DEST" : "NFDEST_EMAIL")
                        .build();
            }
        } catch (Exception ex) {
            log.error("Falha na busca dos registros. Erro no acesso aos dados NOSQL, NOTAFISCAL[ID] => " + nf.getId() + ", error => " + ex.getMessage());
        }
        return s3MailRetry;
    }

    private boolean validateException(Exception ex) {
        AtomicBoolean validate = new AtomicBoolean(true);
        for (String m : ignoredErrors) {
            if (ex != null && ex.getMessage() != null && ex.getMessage().contains(m)) {
                validate.set(false);
            }
        }
        return validate.get();
    }
}
