package br.com.technomori.ordermanager.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service

public class S3Service {

	private static final Logger log = Logger.getLogger(S3Service.class.getName());

	@Autowired
	private AmazonS3 s3Client;
	
	@Value("${s3.bucket}")
	private String bucketName;
	
	
	public URI uploadFile(MultipartFile multipartFile) {
		try {
			String fileName = multipartFile.getOriginalFilename();
			InputStream is = multipartFile.getInputStream();
			String contentType = multipartFile.getContentType();

			return uploadFile(is, fileName, contentType);
		} catch (IOException e) {
			throw new RuntimeException("Error converting URL to URI: " + e.getStackTrace());
		}
	}

	public URI uploadFile(InputStream is, String fileName, String contentType) {
		try {
			log.info("Starting upload ...");
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			
			s3Client.putObject(
					new PutObjectRequest(
							bucketName,
							fileName,
							is,
							metadata
							));
			log.info("... upload completed!");
			
			return s3Client.getUrl(bucketName, fileName).toURI();
			
		} catch( URISyntaxException e ) {
			throw new RuntimeException("Error converting URL to URI: " + e.getStackTrace());
		}
	}

}
