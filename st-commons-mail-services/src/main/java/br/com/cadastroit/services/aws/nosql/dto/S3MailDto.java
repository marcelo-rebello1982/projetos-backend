//package br.com.cadastroit.services.aws.nosql.dto;
//
//import java.io.Serializable;
//
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import br.com.cadastroit.services.aws.nosql.S3MailRetry;
//import br.com.cadastroit.services.aws.nosql.base.S3MailBase;
//import br.com.cadastroit.services.enums.OrigemEmail;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Document(collection = "s3MailDto")
//public class S3MailDto implements Serializable{
//	
//	private static final long serialVersionUID = 7939459442997134511L;
//	
//	private S3MailRetry s3MailRetry;
//	private S3MailBase s3MailBase;
//	private String sistema;
//	private OrigemEmail origemEmail;
//}
