package br.com.complianceit.services;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CstRelatoriodesifApiApplicationTests {

//	@Test
//	public void uploadFile() throws NoSuchAlgorithmException, IOException, URISyntaxException {
//		AwsCryptBucketClient awsCryptBucketClient = new AwsCryptBucketClient();
//		awsCryptBucketClient.createCryptBucket(bucket, this.sufix);
//
//		File file = new File("/home/mateusgobo/Desktop/Mesa/"+bucket+"/35211245281813001379550230000050501341283480.pdf");
//		
//		AwsS3RabbitMessage awsS3RabbitMessage = new AwsS3RabbitMessage();
//		awsS3RabbitMessage.setFile(FileUtils.readFileToByteArray(file));
//		awsS3RabbitMessage.setFileName(file.getName());
//		awsS3RabbitMessage.setId(1L);
//		awsS3RabbitMessage.setPrefix(this.prefix);
//		awsS3RabbitMessage.setUuid(UUID.randomUUID());
//
//		HashMap<String, String> reference = new HashMap<>();
//		reference.put("NOTAFISCAL_ID", "126576804");
//		awsS3RabbitMessage.setReference(reference);
//		
//		AwsBucketUploadModel awsBucketUploadModel = awsCryptBucketClient.uploadBucketObjects(bucket, awsS3RabbitMessage, file);
//		System.out.println(awsBucketUploadModel.getFileName()+"\nStatusCode => "+awsBucketUploadModel.getCode()+"\nTextStatus => "+awsBucketUploadModel.getTextCode());
//	}
//
//	@Test
//	public void uploadFileRabbit() throws NoSuchAlgorithmException, IOException, URISyntaxException, Exception {
//		AwsCryptBucketClient awsCryptBucketClient = new AwsCryptBucketClient();
//		awsCryptBucketClient.createCryptBucket(bucket, this.sufix);
//
//		File file = new File("/home/mateusgobo/Desktop/Mesa/"+bucket+"/35211245281813001379550230000050501341283480.pdf");
//
//		AwsS3RabbitMessage awsS3RabbitMessage = new AwsS3RabbitMessage();
//		awsS3RabbitMessage.setFile(FileUtils.readFileToByteArray(file));
//		awsS3RabbitMessage.setFileName(file.getName());
//		awsS3RabbitMessage.setId(126576804L);
//		awsS3RabbitMessage.setPrefix(this.prefix);
//		awsS3RabbitMessage.setUuid(UUID.randomUUID());
//
//		HashMap<String, String> reference = new HashMap<>();
//		reference.put("NOTAFISCAL_ID", "126576804");
//		awsS3RabbitMessage.setReference(reference);
//
//		this.initialize();
//	}

}
