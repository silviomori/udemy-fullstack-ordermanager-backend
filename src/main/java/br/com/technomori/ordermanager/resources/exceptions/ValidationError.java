package br.com.technomori.ordermanager.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidationError extends StandardError {

	private List<FieldMessage> errors = new ArrayList<FieldMessage>();

	private ValidationError(Integer httpStatus, String message, Long timestamp, List<FieldMessage> errors) {
		super(httpStatus,message,timestamp);
		this.errors = errors;
	}
	
	public void setError(FieldMessage fieldMessage) {
		errors.add(fieldMessage);
	}
	
	public static Builder VEBuilder() {
		return new Builder();
	}

	public static class Builder extends ValidationError {
		Builder httpStatus(Integer httpStatus) {
			setHttpStatus(httpStatus);
			return this;
		}

		Builder message(String message) {
			setMessage(message);
			return this;
		}

		Builder timestamp(Long timestamp) {
			setTimestamp(timestamp);
			return this;
		}

		Builder fieldMessage(FieldMessage fieldMessage) {
			setError(fieldMessage);
			return this;
		}

		ValidationError build() {
			return new ValidationError(getHttpStatus(), getMessage(), getTimestamp(), getErrors());
		}
	}
}
