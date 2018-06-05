package br.com.technomori.ordermanager.resources.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.technomori.ordermanager.services.exceptions.DataIntegrityException;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@ControllerAdvice
public class ResourceHandlerException {

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> objectNotFoundException(ObjectNotFoundException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.httpStatus(HttpStatus.NOT_FOUND.value())
				.message(e.getMessage())
				.timestamp(System.currentTimeMillis())
				.build();
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}


	@ExceptionHandler(DataIntegrityException.class)
	public ResponseEntity<StandardError> dataIntegrity(DataIntegrityException e, HttpServletRequest request) {
		
		StandardError error = StandardError.builder()
				.httpStatus(HttpStatus.BAD_REQUEST.value())
				.message(e.getMessage())
				.timestamp(System.currentTimeMillis())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> MethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {

		ValidationError error = ValidationError.VEBuilder()
				.httpStatus(HttpStatus.BAD_REQUEST.value())
				.message("Validation Error")
				.timestamp(System.currentTimeMillis())
				.build();
		
		for( FieldError fe : e.getBindingResult().getFieldErrors()) {
			error.setError( 
					FieldMessage.builder()
						.fieldName(fe.getField())
						.message(fe.getDefaultMessage())
						.build()
			);
		}
			
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

}
