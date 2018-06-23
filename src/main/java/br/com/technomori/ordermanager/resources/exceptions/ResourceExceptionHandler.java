package br.com.technomori.ordermanager.resources.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import br.com.technomori.ordermanager.services.exceptions.AuthorizationException;
import br.com.technomori.ordermanager.services.exceptions.DataIntegrityException;
import br.com.technomori.ordermanager.services.exceptions.FileException;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> objectNotFoundException(ObjectNotFoundException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}


	@ExceptionHandler(DataIntegrityException.class)
	public ResponseEntity<StandardError> dataIntegrity(DataIntegrityException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("Data Integrity")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> MethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {

		ValidationError error = ValidationError.VEBuilder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
				.error("Validation Error")
				.message(e.getMessage())
				.build();
		
		for( FieldError fe : e.getBindingResult().getFieldErrors()) {
			error.addFieldMessage( 
					FieldMessage.builder()
						.fieldName(fe.getField())
						.message(fe.getDefaultMessage())
						.build()
			);
		}
			
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
	}
	
	@ExceptionHandler(AuthorizationException.class)
	public ResponseEntity<StandardError> authorizationException(AuthorizationException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.FORBIDDEN.value())
				.error("Access Denied")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(FileException.class)
	public ResponseEntity<StandardError> fileException(FileException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("File Error")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(AmazonServiceException.class)
	public ResponseEntity<StandardError> amazonServiceException(AmazonServiceException e, HttpServletRequest request) {
		
		HttpStatus httpErrorCode = HttpStatus.valueOf(e.getErrorCode());
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(httpErrorCode.value())
				.error("Amazon Service Error")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		
		return ResponseEntity.status(httpErrorCode).body(error);
	}

	@ExceptionHandler(AmazonClientException.class)
	public ResponseEntity<StandardError> amazonClientException(AmazonClientException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("Amazon Client Error")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(AmazonS3Exception.class)
	public ResponseEntity<StandardError> amazonS3Exception(AmazonS3Exception e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("Amazon S3 Error")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

}
