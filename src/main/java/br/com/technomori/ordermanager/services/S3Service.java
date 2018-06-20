package br.com.technomori.ordermanager.services;

import java.io.File;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service

public class S3Service {

	private static final Logger log = Logger.getLogger(S3Service.class.getName());

	@Autowired
	private AmazonS3 s3Client;
	
	@Value("${s3.bucket}")
	private String bucketName;
	public void uploadFile(String localFilePath) {
		
		try {
			log.info("Starting upload ...");
			File file = new File(localFilePath);
			s3Client.putObject(
					new PutObjectRequest(
							bucketName,
							"test.jpg",
							file));
			log.info("... upload completed!");
		} catch( AmazonServiceException e ) {
			log.info("AmazonServiceException: "+e.getErrorMessage());
			log.info("Status code: "+e.getErrorCode());
		} catch( AmazonClientException e ) {
			log.info("AmazonClientException: "+e.getMessage());
		}
	}
}
