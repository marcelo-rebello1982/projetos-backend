//package br.com.cadastroit.services.mail;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Properties;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.inject.Inject;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.env.Environment;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Component;
//
//import br.com.cadastroit.services.exceptions.AppMailException;
//
//@Component
//public class MailSender {
//
//	private final static Logger LOG = LoggerFactory.getLogger(MailSender.class);
//	protected static final String FROM = "no-reply@mail.varejonline.com.br";
//
//	@Inject
//	private Environment env;
//
//	@Inject
//	private JavaMailSender emailSender;
//
//	@Inject
//	private ParametroService parametroService;
//
//	@Inject
//	private EmpresaService empresaService;
//
//	public void sendWithTemplate(String to, String subject, String body) {
//
//		this.sendWithTemplate(Arrays.asList(to), subject, body, null);
//
//	}
//
//	/**
//	 * Envia um e-mail utilizando o template padrão ERP (definido no arquivo resources/mail/template/template.html).
//	 * 
//	 * @param to Destinatários do e-mail.
//	 * @param subject Assunto do e-mail.
//	 * @param body Conteúdo do e-mail. Será inserido no <code>{body}</code> do template.
//	 */
//	public void sendWithTemplate(List<String> to, String subject, String body) {
//
//		this.sendWithTemplate(to, subject, body, null);
//	}
//
//	/**
//	 * Envia um e-mail utilizando o template padrão ERP (definido no arquivo resources/mail/template/template.html).
//	 * 
//	 * @param to Destinatários do e-mail.
//	 * @param subject Assunto do e-mail.
//	 * @param body Conteúdo do e-mail. Será inserido no <code>{body}</code> do template.
//	 * @param attachment Anexos do e-mail.
//	 */
//	public void sendWithTemplate(List<String> to, String subject, String body, List<MailAttachment> attachment) {
//
//		if (StringUtils.isBlank(body))
//			return;
//
//		FileInputStream stream = null;
//		String template = null;
//
//		FileInputStream stream2 = null;
//		File imageTop = null;
//		File facebook = null;
//		File linkedin = null;
//
//		try {
//			imageTop = new File(this.getClass().getResource("/mail/template/image-top.png").toURI());
//			facebook = new File(this.getClass().getResource("/mail/template/facebook.png").toURI());
//			linkedin = new File(this.getClass().getResource("/mail/template/linkedin.png").toURI());
//			File templateFile = new File(this.getClass().getResource("/mail/template/template.html").toURI());
//
//			byte[] b = new byte[(int) templateFile.length()];
//			stream = new FileInputStream(templateFile);
//			stream.read(b);
//
//			template = new String(b, "UTF-8");
//
//		} catch (Exception e) {
//
//			LOG.error("Ocorreu um erro ao obter o arquivo modelo para envio de e-mail da conferência de produtos de entradas de notas.", e);
//			return;
//
//		} finally {
//
//			if (stream != null)
//				try {
//					stream.close();
//				} catch (IOException e) {
//					LOG.error("Erro ao fechar recurso", e);
//				}
//
//			if (stream2 != null)
//				try {
//					stream2.close();
//				} catch (IOException e) {
//					LOG.error("Erro ao fechar recurso", e);
//				}
//		}
//
//		String content = template.replace("{body}", body);
//		String[] emails = to.stream().toArray(String[]::new);
//		boolean hasAttachments = CollectionUtils.isNotEmpty(attachment);
//
//		// Não envia email se estiver localhost
//		if (this.isDev()) {
//
//			String filesName = hasAttachments ? attachment.stream().map(a -> a.getName()).collect(Collectors.joining(", ")) : "- sem anexo -";
//
//			LOG.info("------ Enviando e-mail ------");
//			LOG.info("Destinatários: {}", to.stream().collect(Collectors.joining(", ")));
//			LOG.info("Assunto: {}", subject);
//			LOG.info("E-mail: {}", content);
//			LOG.info("Anexos: {}", filesName);
//			LOG.info("-----------------------------");
//
//			return;
//
//		}
//
//		try {
//
//			AppMailSender appMailSender = this.mailSender();
//			JavaMailSender mailSender = appMailSender.getMailSender();
//			MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//			MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//			mimeHelper.setTo(emails);
//			mimeHelper.setSubject(subject);
//			mimeHelper.setText(content, true);
//			mimeHelper.setFrom(appMailSender.getUser());
//
//			if (hasAttachments) {
//
//				for (MailAttachment ma : attachment)
//					mimeHelper.addAttachment(ma.getName(), new ByteArrayResource(ma.getContent()));
//
//			}
//
//			if (imageTop != null)
//				mimeHelper.addInline("image-top", imageTop);
//
//			if (facebook != null)
//				mimeHelper.addInline("image-facebook", facebook);
//
//			if (linkedin != null)
//				mimeHelper.addInline("image-linkedin", linkedin);
//
//			mailSender.send(mimeMessage);
//
//		} catch (MessagingException e) {
//			LOG.error("Erro ao enviar email de pendência de aprovação de entrada para usuários.", e);
//		}
//
//	}
//
//	/**
//	 * Envia um e-mail utilizando o template personalizado com o Logo da Empresa, nome e endereço da Empresa Principal (definido
//	 * no arquivo resources/mail/template/template-empresa.html).
//	 * 
//	 * @param to Destinatários do e-mail.
//	 * @param subject Assunto do e-mail.
//	 * @param body Conteúdo do e-mail. Será inserido no <code>{body}</code> do template.
//	 * @param attachment Anexos do e-mail.
//	 */
//	public void sendWithTemplateEmpresa(List<String> to, String subject, String body, List<MailAttachment> attachment) {
//
//		if (StringUtils.isBlank(body))
//			return;
//
//		FileInputStream stream = null;
//		String template = null;
//
//		File logoEmpresa = null;
//
//		try {
//			File templateFile = new File(this.getClass().getResource("/mail/template/template-empresa.html").toURI());
//
//			byte[] b = new byte[(int) templateFile.length()];
//			stream = new FileInputStream(templateFile);
//			stream.read(b);
//
//			template = new String(b, "UTF-8");
//
//		} catch (Exception e) {
//
//			LOG.error("Ocorreu um erro ao obter o arquivo modelo para envio de e-mail.", e);
//			return;
//
//		} finally {
//
//			if (stream != null)
//				try {
//					stream.close();
//				} catch (IOException e) {
//					LOG.error("Erro ao fechar recurso", e);
//				}
//		}
//
//		String content = template.replace("{body}", body);
//		String[] emails = to.stream().toArray(String[]::new);
//		boolean hasAttachments = CollectionUtils.isNotEmpty(attachment);
//
//		ParametroDTO parametroLogoEmpresa = this.parametroService.obterParametro(ParametroChaveType.IMAGEM_LOGO_EMPRESA);
//
//		if (parametroLogoEmpresa != null) {
//			byte[] logoEmpresaByte = (byte[]) parametroLogoEmpresa.getValor();
//			String base = SecurityUtils.getNumeroBase();
//			String suffix = StringUtils.isNotBlank(base) ? base : UUID.randomUUID().toString();
//			logoEmpresa = FileUtils.bytesToFile(logoEmpresaByte, "logoEmpresa" + suffix, TipoArquivo.PNG);
//		} else {
//			LOG.error("Logo da empresa não configurado.");
//		}
//
//		try {
//			Empresa empresaPrincipal = this.empresaService.obterEmpresaPrincipal();
//
//			String nomeEmpresa = empresaPrincipal.getTerceiro().getNome();
//			content = content.replace("{nomeEmpresa}", nomeEmpresa);
//
//			Endereco endereco = empresaPrincipal.getTerceiro().getEnderecoPorPrecedencia();
//			StringBuilder sb = new StringBuilder();
//
//			if (endereco != null) {
//				String end = endereco.getEndereco();
//				String numero = endereco.getNumero();
//				String complemento = StringUtils.isNotBlank(endereco.getComplemento()) ? " " + endereco.getComplemento() + " -" : StringUtils.EMPTY;
//				String bairro = endereco.getBairro();
//				String cep = CEPUtils.formatar(endereco.getCep());
//				String cidade = endereco.getCidade();
//				String uf = endereco.getUf().toUpperCase();
//
//				String enderecoNumeroComplementoBairroCep = String.format("%s, %s -%s %s - CEP %s", end, numero, complemento, bairro, cep).trim();
//				String cidadeUf = String.format("%s - %s", cidade, uf);
//
//				sb.append(enderecoNumeroComplementoBairroCep);
//				sb.append("<br>");
//				sb.append(cidadeUf);
//
//				content = content.replace("{enderecoEmpresa}", sb.toString());
//			}
//
//		} catch (Exception e) {
//			LOG.error("Ocorreu um erro ao obter a empresa principal.");
//			content = content.replace("{nomeEmpresa}", "").replace("{enderecoEmpresa}", "");
//		}
//
//		// Não envia email se estiver localhost
//		if (this.isDev()) {
//
//			String filesName = hasAttachments ? attachment.stream().map(a -> a.getName()).collect(Collectors.joining(", ")) : "- sem anexo -";
//
//			LOG.info("------ Enviando e-mail ------");
//			LOG.info("Destinatários: {}", to.stream().collect(Collectors.joining(", ")));
//			LOG.info("Assunto: {}", subject);
//			LOG.info("E-mail: {}", content);
//			LOG.info("Anexos: {}", filesName);
//			LOG.info("-----------------------------");
//
//			return;
//
//		}
//
//		try {
//
//			AppMailSender voMailSender = this.mailSender();
//			JavaMailSender mailSender = voMailSender.getMailSender();
//			MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//			MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true);
//			mimeHelper.setTo(emails);
//			mimeHelper.setSubject(subject);
//			mimeHelper.setText(content, true);
//			mimeHelper.setFrom(voMailSender.getUser());
//
//			if (hasAttachments) {
//
//				for (MailAttachment ma : attachment)
//					mimeHelper.addAttachment(ma.getName(), new ByteArrayResource(ma.getContent()));
//
//			}
//
//			if (logoEmpresa != null)
//				mimeHelper.addInline("logo-empresa", logoEmpresa);
//
//			mailSender.send(mimeMessage);
//
//		} catch (MessagingException e) {
//			LOG.error("Erro ao enviar email.", e);
//		}
//
//	}
//
//	protected void send(List<String> to, String subject, String text, boolean html) {
//
//		String[] toArr = new String[to.size()];
//		toArr = to.toArray(toArr);
//
//		AppMailSender appMailSender = this.mailSender();
//		JavaMailSender mailSender = appMailSender.getMailSender();
//
//		if (!html) {
//
//			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//			simpleMailMessage.setTo(toArr);
//			simpleMailMessage.setSubject(subject);
//			simpleMailMessage.setText(text);
//			simpleMailMessage.setFrom(appMailSender.getUser());
//
//			mailSender.send(simpleMailMessage);
//
//		} else {
//
//			try {
//
//				MimeMessage mimeMessage = mailSender.createMimeMessage();
//				MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true);
//				mimeHelper.setTo(toArr);
//				mimeHelper.setSubject(subject);
//				mimeHelper.setText(text, true);
//				mimeHelper.setFrom(appMailSender.getUser());
//
//				mailSender.send(mimeMessage);
//
//			} catch (MessagingException e) {
//				throw new AppMailException("Ocorreu um erro ao criar a mensagem");
//			}
//
//		}
//
//	}
//
//	protected AppMailSender mailSender() {
//
//		AppMailSender voSender = null;
//
//		try {
//			voSender = this.customSmtp();
//		} catch (Exception e) {
//			throw new AppMailException("Ocorreu um erro ao obter os dados e criar o recurso para enviar e-mail com smtp customizado.", e);
//		}
//
//		if (voSender != null)
//			return voSender;
//
//		return new AppMailSender(emailSender, FROM);
//
//	}
//
////	private AppMailSender customSmtp() {
////
////		List<ParametroDTO> parametros = this.parametroService.obterParametros(Arrays.asList(ParametroChaveType.SMTP_HOST,
////				ParametroChaveType.SMTP_PORT, ParametroChaveType.SMTP_USER, ParametroChaveType.SMTP_PASS));
////
////		if (CollectionUtils.isEmpty(parametros) || parametros.size() != 4)
////			return null;
////
////		String host = null;
////		Integer port = null;
////		String user = null;
////		String pass = null;
////
////		for (ParametroDTO p : parametros) {
////
////			ParametroChaveType chave = p.getChave();
////
////			switch (chave) {
////				case SMTP_HOST:
////					host = p.getValor().toString();
////					continue;
////
////				case SMTP_PORT:
////					port = ParametroService.valorAsInt(p.getValor());
////					continue;
////
////				case SMTP_USER:
////					user = p.getValor().toString();
////					continue;
////
////				case SMTP_PASS:
////					pass = p.getValor().toString();
////					continue;
////
////				default:
////					break;
////			}
////
////		}
////
////		if (StringUtils.isBlank(host) || port == null || StringUtils.isBlank(user) || StringUtils.isBlank(pass))
////			return null;
////
////		JavaMailSenderImpl sender = new JavaMailSenderImpl();
////		sender.setHost(host);
////		sender.setPort(port);
////		sender.setUsername(user);
////		sender.setPassword(pass);
////
////		Properties props = sender.getJavaMailProperties();
////		props.put("mail.smtp.auth", "true");
////
////		if (port == 25 || port == 587)
////			props.put("mail.smtp.starttls.enable", "true");
////
////		if (port == 465)
////			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
////
////		if (this.isDev())
////			props.put("mail.debug", "true");
////
////		return new AppMailSender(sender, user);
////
////	}
//
////	private boolean isDev() {
////
////		String profile = env.getRequiredProperty(Constants.SERVER_SPRING_PROFILE_ENV);
////		boolean isProduction = profile.equals(Profiles.PRODUCTION.getName());
////
////		return !isProduction;
////
////	}
//
//	class AppMailSender {
//
//		private JavaMailSender mailSender;
//		private String user;
//
//		AppMailSender(JavaMailSender mailSender, String user) {
//
//			this.mailSender = mailSender;
//			this.user = user;
//		}
//
//		public JavaMailSender getMailSender() {
//
//			return mailSender;
//		}
//
//		public String getUser() {
//
//			return user;
//		}
//
//	}
//
//}
