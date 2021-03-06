package br.com.technomori.ordermanager.resources.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@Builder
public class StandardError {

	private Long timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;

}
