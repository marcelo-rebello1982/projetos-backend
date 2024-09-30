package br.com.cadastroit.services.aws;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.S3Object;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsBucketObjectModel {
	
	private S3Object s3objects;
	private ByteArrayOutputStream baos;
	private Integer countObjects;
	private String sizeFileKB;
	private String nameObject;
	private Date lastModified;
	private String storageClass;
	private String owner;
	
}
