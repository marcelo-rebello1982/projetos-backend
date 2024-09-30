package br.com.cadastroit.services.aws;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.cadastroit.services.common.aws.AwsCommons;
import br.com.cadastroit.services.common.aws.AwsS3RabbitMessage;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Transition;
import software.amazon.awssdk.services.s3.model.TransitionStorageClass;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

@Slf4j
public class AwsCryptBucketClient implements Serializable{

	private static final long serialVersionUID = 1L;
	private S3Client s3CryptoClient;
	
	public AwsCryptBucketClient() throws NoSuchAlgorithmException, IOException, URISyntaxException {
		AwsCommons awsCommons		= new AwsCommons.Builder().withProperties().build();
		String pass					= awsCommons.getPasswordS3();
		String user					= awsCommons.getUserS3();
		
		AwsBasicCredentials credentials = AwsBasicCredentials.create(user, pass);
		this.s3CryptoClient			= S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials))
														.region(Region.US_EAST_2)
													    .build();
	}
	
	private boolean doesBucketExist(String bucketName) {
		return this.s3CryptoClient.listBuckets(ListBucketsRequest.builder().build()).buckets().stream().anyMatch(b->b.name().equals(bucketName));
	}
	private HeadBucketResponse createBucket(String bucketName) {
		S3Waiter s3Waiter = this.s3CryptoClient.waiter();
		CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
		this.s3CryptoClient.createBucket(bucketRequest);
		
		HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName).build();
		WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
		Optional<HeadBucketResponse> response = waiterResponse.matched().response();
		
		if(response.isPresent()) {
			log.info(bucketName+" is ready...");
			return response.get();
		}
		
		return null;
	}
	public void createCryptBucket(String bucketName, String prefixBucket) throws S3Exception {
		try {
			if(!this.doesBucketExist(bucketName)) {//Creating a new bucket
				HeadBucketResponse headBucketResponse = this.createBucket(bucketName);
				if(headBucketResponse != null) {
					LifecycleRuleFilter ruleFilter = LifecycleRuleFilter.builder()
																		.prefix(prefixBucket)
																		.build();
					Transition transition = Transition.builder()
													  .storageClass(TransitionStorageClass.STANDARD_IA)
													  .days(30)
													  .build();
					
					LifecycleRule rule = LifecycleRule.builder()
													  .id("Archive after 30 days rule to Infrequent Access")
													  .filter(ruleFilter)
													  .transitions(transition)
													  .status(ExpirationStatus.ENABLED)
													  .build();
					BucketLifecycleConfiguration lifecycleConfiguration = BucketLifecycleConfiguration.builder()
																									  .rules(rule)
																									  .build();
					PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder()
																																		  .bucket(bucketName)
																																		  .lifecycleConfiguration(lifecycleConfiguration)
																																		  .expectedBucketOwner("772603031349")
																																		  .build();
					this.s3CryptoClient.putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequest);
				}		
			} 
		}catch (S3Exception ex) {
			log.error(ex.getMessage());
		}
	}
	
	private ListObjectsResponse listObjectsResponse(String bucketName) {
		ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucketName).build();
		ListObjectsResponse response   = this.s3CryptoClient.listObjects(listObjects);
		return response;
	}
	public AwsBucketObjectModel downalodBucketObject(String bucketName, String prefix, String fileName) throws S3Exception {
		try {
			List<S3Object> s3objects = this.listObjectsResponse(bucketName).contents();
			S3Object s3 = s3objects.stream().filter(dataBucket->dataBucket.key().equals(prefix+fileName)).findAny().orElse(null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			this.s3CryptoClient.getObject(GetObjectRequest.builder().bucket(bucketName).key(s3.key()).build(), ResponseTransformer.toOutputStream(baos));
			
			return AwsBucketObjectModel.builder()
									   .nameObject(s3.key())
									   .countObjects(s3objects.size())
									   .s3objects(s3)
									   .sizeFileKB((s3.size()/1024)+" KBs")
									   .owner(s3.owner().id())
									   .lastModified(new Date(s3.lastModified().toEpochMilli()))
									   .storageClass(s3.storageClassAsString())
									   .baos(baos).build();
		}catch(S3Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}
	
	public List<AwsBucketObjectModel> downalodBucketObjects(String bucketName, Integer count) throws S3Exception {
		try {
			List<S3Object> s3objectCollection = this.listObjectsResponse(bucketName).contents();
			List<AwsBucketObjectModel> awsBucketObjectModelCollection = new ArrayList<>();
			List<S3Object> s3objects = s3objectCollection.stream().sorted(Comparator.comparing(S3Object::key)).limit(count).collect(Collectors.toList());
			s3objects.forEach(s3 -> {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				this.s3CryptoClient.getObject(GetObjectRequest.builder().bucket(bucketName).key(s3.key()).build(), ResponseTransformer.toOutputStream(baos));
				
				awsBucketObjectModelCollection.add(
					AwsBucketObjectModel.builder()
										.nameObject(s3.key())
										.countObjects(s3objectCollection.size())
										.s3objects(s3)
										.sizeFileKB((s3.size()/1024)+" KBs")
										.baos(baos)
										.owner(s3.owner().id())
									    .lastModified(new Date(s3.lastModified().toEpochMilli()))
										.storageClass(s3.storageClassAsString())
										.build()
				);
			});
			
			return awsBucketObjectModelCollection;
		}catch(S3Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}
	
	public AwsBucketUploadModel uploadBucketObjects(String bucketName, AwsS3RabbitMessage awsS3RabbitMessage, File file) throws AwsServiceException, SdkClientException  {
		try {
			Map<String,String> tagging = awsS3RabbitMessage.getReference();
			String key 	= tagging.entrySet().iterator().next().getKey();
			String value= tagging.entrySet().iterator().next().getValue();
			
			PutObjectRequest objRequest = PutObjectRequest.builder().bucket(bucketName)
																    .key(awsS3RabbitMessage.getPrefix()+awsS3RabbitMessage.getFileName())
																    .tagging(key+"="+value)
																    .build();
			PutObjectResponse response  = this.s3CryptoClient.putObject(objRequest, RequestBody.fromFile(file));
			
			SdkHttpResponse sdkResponse = response.sdkHttpResponse();
			return AwsBucketUploadModel.builder()
									   .code(sdkResponse.statusCode())
									   .textCode(sdkResponse.statusText().get())
									   .fileName(file.getName())
									   .eTag(response.eTag())
									   .build();
		}catch(AwsServiceException serviceException) {
			log.error(serviceException.getMessage());
			return null;
		}catch(SdkClientException clientException) {
			log.error(clientException.getMessage());
			return null;
		}
	}
	
	public List<AwsBucketObjectModel> bucketObjectsCollection(String bucketName) throws S3Exception {
		try {
			List<S3Object> s3objects = this.listObjectsResponse(bucketName).contents();
			List<AwsBucketObjectModel> awsBucketObjectModelCollection = new ArrayList<>();
			
			s3objects.forEach(s3 -> {
				awsBucketObjectModelCollection.add(
					AwsBucketObjectModel.builder()
										.nameObject(s3.eTag())
										.countObjects(s3objects.size())
										.s3objects(s3)
										.sizeFileKB((s3.size()/1024)+" KBs")
										.baos(null)
										.build()
				);
			});
			return awsBucketObjectModelCollection;
		}catch(S3Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}
}
