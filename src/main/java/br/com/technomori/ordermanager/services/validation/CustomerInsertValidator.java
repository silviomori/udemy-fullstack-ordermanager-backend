package br.com.technomori.ordermanager.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.enums.CustomerType;
import br.com.technomori.ordermanager.dto.InsertCustomerDTO;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.resources.exceptions.FieldMessage;
import br.com.technomori.ordermanager.services.validation.util.BR;

public class CustomerInsertValidator implements ConstraintValidator<CustomerInsert, InsertCustomerDTO>{

	@Autowired
	private CustomerRepository customerRepo;
	
	@Override
	public void initialize( CustomerInsert ann ) {
	}
	
	@Override
	public boolean isValid(InsertCustomerDTO dto, ConstraintValidatorContext context) {
		List<FieldMessage> list = new ArrayList<FieldMessage>();
		
		if( dto.getCustomerType() == null ) {
			list.add(FieldMessage.builder()
					.fieldName("customerType")
					.message("Customer Type must be defined.")
					.build()
					);
		} else if( dto.getCustomerType().equals(CustomerType.INDIVIDUAL) ) {
			if( ! BR.isValidCPF(dto.getDocumentNumber()) ) {
				list.add(FieldMessage.builder()
						.fieldName("documentNumber")
						.message("Invalid document number")
						.build()
						);
			}
		} else if( dto.getCustomerType().equals(CustomerType.CORPORATE) ) {
			if( ! BR.isValidCNPJ(dto.getDocumentNumber()) ) {
				list.add(FieldMessage.builder()
						.fieldName("documentNumber")
						.message("Invalid document number")
						.build()
						);
			}
		}
		
		Customer customer = customerRepo.findByEmail(dto.getEmail());
		if( customer != null ) {
			list.add(FieldMessage.builder()
					.fieldName("email")
					.message("Email has already been registered.")
					.build()
					);
		}
		
		for( FieldMessage fieldMessage : list ) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(fieldMessage.getMessage())
				.addPropertyNode(fieldMessage.getFieldName())
				.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
