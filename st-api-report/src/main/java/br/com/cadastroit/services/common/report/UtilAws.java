package br.com.cadastroit.services.common.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.amazonaws.AmazonServiceException;

import br.com.cadastroit.services.aws.AwsTempBucketClient;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UtilAws {

	protected AwsTempBucketClient buildAwsTempBuckClient(String bucketName, String bucketKey) throws IOException {

		try {
			AwsTempBucketClient awsTempBucketClient = new AwsTempBucketClient();
			awsTempBucketClient.setObjectKey(bucketKey);
			awsTempBucketClient.initializeAmazon(bucketName);
			awsTempBucketClient.createTempBucket(bucketName);
			return awsTempBucketClient;
		} catch (AmazonServiceException ex) {
			throw new AmazonServiceException(ex.getMessage());
		}
	}

	protected void uploadAwsTempBuckClient(AwsTempBucketClient awsTempBucketClient, String bucketName, File file) {

		awsTempBucketClient.uploadFileTos3Bucket(bucketName, file);
	}

	protected String zipFiles(List<File> files, String fileName, String bucketName, AwsTempBucketClient awsTempBucketClient) throws IOException {

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
			throw new IOException(ex.getMessage(), ex);
		}
	}

}
