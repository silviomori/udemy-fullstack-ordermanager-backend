package br.com.technomori.ordermanager.services.validation.util;

public class BR {

	// TODO: improve this validation
	public static boolean isValidCPF(String cpf) {
		if( cpf == null ) return false;
		if( cpf.length() != 11 ) return false;
		
		return true;
	}
	
	// TODO: improve this validation
	public static boolean isValidCNPJ(String cnpj) {
		if( cnpj == null ) return false;
		if( cnpj.length() != 14 ) return false;
		
		return true;
	}
}
