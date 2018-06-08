package br.com.technomori.ordermanager.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.dto.CustomerDTO;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.resources.exceptions.FieldMessage;

public class CustomerUpdateValidator implements ConstraintValidator<CustomerUpdate, CustomerDTO>{

	@Autowired
	HttpServletRequest httpRequest;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Override
	public void initialize( CustomerUpdate ann ) {
	}
	
	@Override
	public boolean isValid(CustomerDTO dto, ConstraintValidatorContext context) {
		
		// This is made to not enforce CustomerDTO to set the id 
		@SuppressWarnings("unchecked")
		Map<String, String> mapVariables = (Map<String, String>) httpRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Integer uriId = Integer.parseInt(mapVariables.get("id"));
		
		List<FieldMessage> list = new ArrayList<FieldMessage>();
		
		Customer customer = customerRepo.findByEmail(dto.getEmail());
		if( customer != null && !customer.getId().equals(uriId)) {
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
