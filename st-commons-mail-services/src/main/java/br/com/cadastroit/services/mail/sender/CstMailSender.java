package br.com.cadastroit.services.mail.sender;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import br.com.cadastroit.services.aws.nosql.base.S3MailBase;
import br.com.cadastroit.services.builders.MailSenderBuilder;
import br.com.cadastroit.services.mail.Attachment;
import br.com.cadastroit.services.mail.MailObject;
import br.com.cadastroit.services.mail.MailObjectImpl;
import br.com.cadastroit.services.mail.commons.UtilString;
import br.com.cadastroit.services.mail.commons.impl.CstMailCommonsImpl;
import br.com.cadastroit.services.mail.exceptions.CstMailException;
import br.com.cadastroit.services.nfe.domain.NotaFiscal;
import br.com.cadastroit.services.nfe.domain.NotaFiscalEmit;
import br.com.cadastroit.services.nfe.domain.NotaFiscalTotal;
import br.com.cadastroit.services.profile.domain.Empresa;
import br.com.cadastroit.services.profile.domain.Juridica;
import br.com.cadastroit.services.profile.domain.Pessoa;
import br.com.cadastroit.services.profile.services.ParamGeralEmailService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CstMailSender implements CstMailCommonsImpl {

	private NotaFiscal notafiscal;
	private Empresa empresa;
	private Pessoa pessoa;
	private Juridica juridica;
	private Logger logger;
	private String email;
	private S3MailBase s3Mail;
	private EntityManagerFactory entityManagerFactory;
	private ParamGeralEmailService paramGeralEmailService;

	public boolean enviarNotaPorEmail(Integer viaPortal) throws Exception {

		try {
			MailObject mailObject = this.configurarTemplate();
			mailObject = anexarXML(mailObject);
			boolean enviarDanfeEmail = this.empresa.getDmEnvDanfeEmail().longValue() == 1l || viaPortal == 1;

			this.logger.info(String.format(ANEXANDO_DOCUMENTO, "XML", this.notafiscal.getNroNf()));
			if (this.notafiscal.getDmStProc().intValue() == 4 && enviarDanfeEmail) {// Enviar danfe por email
				mailObject = anexarPDF(mailObject);
				this.logger.info(String.format(ANEXANDO_DOCUMENTO, "PDF", this.notafiscal.getNroNf() + " - Nota Autorizada"));
			}
			this.logger.info(String.format(ENVIANDO_EMAIL, this.notafiscal.getNroNf(), mailObject.getFromAddress(), mailObject.getSubject()));

			String[] paramGeralEmail = this.paramGeralEmailService.getByMultOrg(this.empresa.getMultOrg().getId(), this.entityManagerFactory);
			validarEnvioEmail(paramGeralEmail, mailObject);
			this.logger.info(String.format(EMAIL_ENVIADO, this.notafiscal.getNroNf()));
			return true;
		} catch (Exception e) {
			logger.error(String.format(ERROR_ENVIAR_EMAIL, this.notafiscal.getId(), e.getMessage()));
			throw new Exception(String.format(ERROR_ENVIAR_EMAIL, this.notafiscal.getId(), e.getMessage()));
		}
	}

	private MailObject configurarTemplate() {

		MailObject mailObject = new MailObjectImpl();
		mailObject.setFromName(this.empresa.getEmailNomeRemetente());
		mailObject.setFromAddress(this.empresa.getEmailEnderecoRemetente());
		mailObject.addTo(this.email.replace(",", "").replace(";", ""));
		mailObject.setSubject(this.empresa.getEmailTemplateSubjectRemetente());

		if (this.notafiscal.getDmStProc().intValue() == 7) {// Nota cancelada

			// Quando o campo email_template_body_canc for diferente de nulo e maior que zero, sera utilizado-o
			if (this.empresa.getEmailTemplateBodyCanc() != null && this.empresa.getEmailTemplateBodyCanc().trim().length() > 0) {
				mailObject.setBody(this.empresa.getEmailTemplateBodyCanc());
			} else {
				mailObject.setBody(this.empresa.getEmailTemplateBodyRemetente());
			}

		} else {
			mailObject.setBody(this.empresa.getEmailTemplateBodyRemetente());
		}

		Map<String, String> replaceWords = new HashMap<>();

		// Documento
		replaceWords.put("#hora_aprovacao_lote#", UtilString.toDateTimeString(this.notafiscal.getDtAutSefaz()));
		replaceWords.put("#nro_documento#", this.notafiscal.getNroNf().toString());
		replaceWords.put("#serie_documento#", this.notafiscal.getSerie());
		replaceWords.put("#nro_chave_acesso#", this.notafiscal.getNroChaveNfe() != null ? this.notafiscal.getNroChaveNfe() : "");
		replaceWords.put("#data_emissao#", UtilString.toDateTimeString(this.notafiscal.getDtEmiss()));

		NotaFiscalTotal notafiscalTotal = this.notafiscal.getNotaFiscalTotalCollection().iterator().next();
		replaceWords.put("#valor_total#", UtilString.toCurrency(notafiscalTotal.getVlTotalNf()));

		String remetente = null;
		String razaoSocial = null;
		String cnpjEmitente = null;
		String telefoneEmitente = null;
		if (this.notafiscal.getDmIndEmit().intValue() == 0) {// Emissao propria - Emitente e a propria empresa
			remetente = this.pessoa.getFantasia() != null ? this.pessoa.getFantasia() : this.pessoa.getNome();
			razaoSocial = this.pessoa.getNome() != null ? this.pessoa.getNome() : "";

			String cnpj = (StringUtils.leftPad(this.juridica.getNumCnpj().toString(), 8, '0'))
					+ (StringUtils.leftPad(this.juridica.getNumFilial().toString(), 4, '0'))
					+ (StringUtils.leftPad(this.juridica.getDigCnpj().toString(), 2, '0'));
			cnpjEmitente = this.juridica != null ? UtilString.formataCNPJ(cnpj) : "";
			telefoneEmitente = this.pessoa.getFone() != null ? this.pessoa.getFone() : "";
		} else if (this.notafiscal.getDmIndEmit().intValue() == 1) {// Terceiro - Emitente vem da tabela emitente
			NotaFiscalEmit emit = (NotaFiscalEmit) this.notafiscal.getNotaFiscalEmitCollection().toArray()[0];
			remetente = emit.getFantasia() != null ? emit.getFantasia() : emit.getNome();
			razaoSocial = emit.getNome() != null ? emit.getNome() : "";
			cnpjEmitente = emit.getCnpj() != null ? emit.getCnpj() : "";
			telefoneEmitente = emit.getFone() != null ? emit.getFone() : "";
		}

		replaceWords.put("#remetente_nota_fantasia#", remetente);
		replaceWords.put("#razao_social_emitente#", razaoSocial);
		replaceWords.put("#cnpj_emitente#", cnpjEmitente);
		replaceWords.put("#telefone_emitente#", telefoneEmitente);

		// Destinatario
		this.notafiscal.getNotaFiscalDestCollection().stream().forEach(dest -> {
			replaceWords.put("#razao_social_destinatario#", dest.getNome());
			if (dest.getCnpj() != null && !dest.getCnpj().isEmpty()) {
				replaceWords.put("#cnpj_destinatario#", UtilString.formataCNPJ(dest.getCnpj()));
			} else if (dest.getCpf() != null && !dest.getCpf().isEmpty()) {
				replaceWords.put("#cnpj_destinatario#", UtilString.formataCPF(dest.getCpf()));
			} else if (dest.getUf() != null && dest.getUf().toUpperCase().equals("EX")) {
				replaceWords.put("#cnpj_destinatario#", "Não Possui");
			}
		});
		mailObject.replaceWithMap(replaceWords);
		return mailObject;
	}

	private MailObject anexarXML(MailObject mailObject) {

		try {
			mailObject.addAttachment(
					new Attachment("NF-e" + (this.notafiscal.getNroChaveNfe() != null ? "-" + this.notafiscal.getNroChaveNfe().trim() : "") + ".xml",
							this.s3Mail.getBaosXML(), "text/xml; charset=utf-8"));
			// NotaFiscalCce cce = this.notaFiscalCceDaoBean.findByNotaFiscal(this.notafiscal.getId());//Verifica existencia da
			// ultima carta de correcao
			// if (cce != null && cce.getXmlProc() != null) {
			// mailObject.addAttachment(new Attachment("CCE-NFe-" + this.notafiscal.getNroChaveNfe().trim() + ".xml",
			// cce.getXmlProc(), "text/xml; charset=utf-8"));
			// }
			// if (this.notafiscal.getDmStProc().intValue() == 7) {//Adiciona no anexo o xml do cancelamento
			// if (this.notafiscal.getNotaFiscalCancCollection() != null && this.notafiscal.getNotaFiscalCancCollection().size() >
			// 0) {
			// NotaFiscalCanc nfc = this.notafiscal.getNotaFiscalCancCollection().iterator().next();
			// byte[] outCancXml = nfc.getCancProcXml();
			// mailObject.addAttachment(new Attachment("CANC-NFe-" + this.notafiscal.getNroChaveNfe().trim() + ".xml", outCancXml,
			// "text/xml; charset=utf-8"));
			// }
			// }
		} catch (Exception e) {
			logger.error("Erro ao anexar XML da NotaFiscal.Id=" + this.notafiscal.getId() + ": " + e.getMessage());
		}
		return mailObject;
	}

	private MailObject anexarPDF(MailObject mailObject) {

		try {// Adiciona anexo quando nota autorizada
			mailObject.addAttachment(new Attachment(
					"DANFE-NFe" + (this.notafiscal.getNroChaveNfe() != null ? "-" + this.notafiscal.getNroChaveNfe().trim() : "") + ".pdf",
					s3Mail.getBaosPDF(), "application/pdf; charset=utf-8"));
		} catch (Exception e) {
			logger.error("Erro ao anexar PDF DANFE para NOTAFISCAL_[ID] = " + this.notafiscal.getId() + ": " + e.getMessage());
		}
		// try {
		// NotaFiscalCce cce = this.notaFiscalCceDaoBean.findByNotaFiscal(this.notafiscal.getId());// Verifica existencia da
		// ultima carta de correcao
		// if (cce != null && cce.getXmlProc() != null) {
		// JasperReport reportPrincipal = SpringBeanFactoryUtil.getBean("NF_CARTA_CORRECAO_PRINCIPAL");// Gera report em PDF
		// Map<String, Object> parametros = ReportUtil.paramNfCartaCorrecao(nf, cce);// Monta Parametros Relatario
		// ReportExec r = new ReportExec();
		// r.setVias(1);
		// r.setParametros(parametros);
		// r.setReportPrincipal(reportPrincipal);
		// r.fill(r.getParametros(), r.getReportPrincipal(), new JREmptyDataSource());
		// r.generatePDFReport();
		// ByteArrayOutputStream baosPdf = r.getPDFReport();
		// mailObject.addAttachment(new Attachment("CCE-NFe-" + this.notafiscal.getNroChaveNfe().trim() + ".pdf",
		// baosPdf.toByteArray(), "application/pdf; charset=utf-8"));
		// }
		// } catch (Exception e) {
		// logger.error("Erro ao anexar PDF CCE da NotaFiscal.Id=" + this.notafiscal.getId() + ": " + e.getMessage());
		// }
		return mailObject;
	}

	private boolean validarEnvioEmail(String[] paramGeralEmail, MailObject mailObject) throws Exception {

		File[] files = new File[mailObject.getAttachments().length];
		try {
			String username = paramGeralEmail[1];
			String password = paramGeralEmail[2];
			String protocol = paramGeralEmail[4];
			String smtpHost = paramGeralEmail[7];
			String port = paramGeralEmail[8];
			String smtpAuth = paramGeralEmail[10];
			boolean sendPartial = true;
			String connectionTimeout = "10000";

			JavaMailSender javaMailSender = MailSenderBuilder.builder()
					.connectionTimeout(connectionTimeout)
					.protocol(protocol)
					.smtpAuth(Boolean.valueOf(smtpAuth))
					.smtpHost(smtpHost)
					.timeout(connectionTimeout)
					.sendPartial(sendPartial)
					.username(username)
					.password(password)
					.port(Integer.parseInt(port))
					.LOGGER(this.logger)
					.build()
					.createJavaMailSenderImpl();

			MimeMessage message = javaMailSender.createMimeMessage();
			final MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(mailObject.getFromAddress().length() == 0 ? "noreply@compliancefiscal.com.br" : mailObject.getFromAddress());
			helper.setTo(mailObject.getTo());
			helper.setSubject(mailObject.getSubject());
			helper.setText(mailObject.getBody());
			helper.setBcc(mailObject.getBcc());
			helper.setCc(mailObject.getCc());
			AtomicInteger index = new AtomicInteger();
			if (mailObject.getAttachments().length > 0) {
				Arrays.stream(mailObject.getAttachments()).forEach(a -> {
					try {
						File file = new File(a.getAttachmentName());
						FileUtils.writeByteArrayToFile(file, a.getContent());
						helper.addAttachment(file.getName(), file);

						files[index.get()] = file;
					} catch (IOException ex) {
						logger.error(String.format(
								"Falha no envio do email (Erros na composicao dos arquivos). NF[ID] = %s, Destinatario = %s. Total de anexos %s. [Erro] = %s",
								this.notafiscal.getId(), mailObject.getTo(), mailObject.getAttachments().length, ex.getMessage()));
					} catch (MessagingException ex) {
						logger.error(String.format(
								"Falha no envio do email (Problemas ao adicionar anexos na mensagem dos arquivo). NF[ID] = %s, Destinatario = %s. Total de anexos %s. [Erro] = %s",
								this.notafiscal.getId(), mailObject.getTo(), mailObject.getAttachments().length, ex.getMessage()));
					}
					index.getAndIncrement();
				});
			}
			try {
				javaMailSender.send(message);
				return true;
			} catch (CstMailException ex) {
				throw new CstMailException(String.format("Falha no envio do email. NF[ID] = %s, Destinatario = %s. Total de anexos %s. [Erro] = %s",
						this.notafiscal.getId(), mailObject.getTo(), mailObject.getAttachments().length, ex.getMessage()));
			}
		} catch (Exception ex) {
			throw new Exception(String.format("Erro geral no envio do email. NF[ID] = %s, Destinatario = %s. Total de anexos %s. [Erro] = %s",
					this.notafiscal.getId(), mailObject.getTo(), mailObject.getAttachments().length, ex.getMessage()));
		} finally {
			if (files.length > 0) {
				Arrays.stream(files).forEach(f -> {
					try {
						FileUtils.forceDelete(f);
					} catch (IOException ex) {
						logger.error(String.format("Erro na exclusao do arquivo %s. [Erro] = %s", f.getName(), ex.getMessage()));
					}
				});
			}
		}
	}

}
