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

	private List<FieldMessage> fieldMessages = new ArrayList<FieldMessage>();

	private ValidationError(Long timestamp, Integer status, String error, String message, String path, List<FieldMessage> fieldMessages) {
		super(timestamp, status, error, message, path);
		this.fieldMessages = fieldMessages;
	}
	
	public void addFieldMessage(FieldMessage fieldMessage) {
		fieldMessages.add(fieldMessage);
	}
	
	public static Builder VEBuilder() {
		return new Builder();
	}

	public static class Builder extends ValidationError {
		Builder timestamp(Long timestamp) {
			setTimestamp(timestamp);
			return this;
		}

		Builder status(Integer httpStatus) {
			setStatus(httpStatus);
			return this;
		}

		Builder error(String error) {
			setError(error);
			return this;
		}

		Builder message(String message) {
			setMessage(message);
			return this;
		}

		Builder path(String path) {
			setPath(path);
			return this;
		}

		Builder fieldMessage(FieldMessage fieldMessage) {
			addFieldMessage(fieldMessage);
			return this;
		}

		ValidationError build() {
			return new ValidationError(getTimestamp(), getStatus(), getError(), getMessage(), getPath(), getFieldMessages());
		}
	}
}
