package br.com.cadastroit.storage.services.aws;

import org.springframework.stereotype.Component;

@Component
public abstract class AmazonStorage {

	private static final Long EXPIRATION_LIMIT = 300000l;

	private static final String AWS_URL = "s3-sa-east-1.amazonaws.com";

	private String accessKey;

	private String secretKey;
	
}
