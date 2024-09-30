package br.com.cadastroit.services.s3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonServiceException;

import br.com.cadastroit.services.aws.AwsTempBucketClient;

public class S3FilesUtil {

	public String checkEnvironment() throws FileNotFoundException, IOException {

		String environment;
		if (System.getenv("ENVIRONMENT") != null) {
			environment = System.getenv("ENVIRONMENT");
		} else {
			Properties props = new Properties();
			props.load(new FileInputStream(new File("/opt/config/version.properties")));
			environment = props.getProperty("apigateway");
		}
		return environment != null && !environment.contains("hml") ? "prd" : "hml";
	}

	public AwsTempBucketClient buildAwsTempBuckClient(String bucketName, Long time, String bucketKey, Logger LOGGER) throws IOException {

		try {
			AwsTempBucketClient awsTempBucketClient = new AwsTempBucketClient();
			awsTempBucketClient.setObjectKey(bucketKey);
			awsTempBucketClient.initializeAmazon(bucketName, time);
			awsTempBucketClient.createTempBucket(bucketName);
			return awsTempBucketClient;
		} catch (AmazonServiceException ex) {
			LOGGER.error(ex.getMessage());
			throw new AmazonServiceException(ex.getMessage());
		}
	}

	public AwsTempBucketClient buildAwsTempBuckClient(String bucketName, Date expire, String bucketKey, Logger LOGGER) throws IOException {

		try {
			AwsTempBucketClient awsTempBucketClient = new AwsTempBucketClient();
			awsTempBucketClient.setObjectKey(bucketKey);
			awsTempBucketClient.initializeAmazon(bucketName, expire);
			awsTempBucketClient.createTempBucket(bucketName);
			return awsTempBucketClient;
		} catch (AmazonServiceException ex) {
			LOGGER.error(ex.getMessage());
			throw new AmazonServiceException(ex.getMessage());
		}
	}

	public void uploadAwsTempBuckClient(AwsTempBucketClient awsTempBucketClient, String bucketName, File file) {

		awsTempBucketClient.uploadFileTos3Bucket(bucketName, file);
	}

	public String zipFiles(Set<File> files, String fileName, String bucketName, AwsTempBucketClient awsTempBucketClient) throws Exception {

		byte[] buffer = new byte[1024];
		try {
			File f = new File(fileName);
			f.createNewFile();

			FileOutputStream fos = new FileOutputStream(f);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (File file : files) {
				ZipEntry ze = new ZipEntry(file.getName());
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(file);
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				in.close();

				Path path = Paths.get(file.getName());
				Files.delete(path);
			}
			zos.closeEntry();
			zos.close();
			if (awsTempBucketClient != null) {
				awsTempBucketClient.uploadFileTos3Bucket(bucketName, f);
				Path path = Paths.get(fileName);
				Files.delete(path);
				return awsTempBucketClient.getTempURL();
			} else {
				return fileName;
			}
		} catch (IOException ex) {
			throw new Exception(ex.getMessage(), ex);
		}
	}
}
