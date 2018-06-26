package br.com.technomori.ordermanager.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.technomori.ordermanager.domain.Address;
import br.com.technomori.ordermanager.domain.City;
import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.enums.UserProfile;
import br.com.technomori.ordermanager.dto.CustomerDTO;
import br.com.technomori.ordermanager.dto.InsertAddressDTO;
import br.com.technomori.ordermanager.dto.InsertCustomerDTO;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.security.UserSpringSecurity;
import br.com.technomori.ordermanager.services.exceptions.AuthorizationException;
import br.com.technomori.ordermanager.services.exceptions.DataIntegrityException;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imageService;
	
	@Value("${img.customer.profile.prefix}")
	private String imgProfilePrefix;

	@Value("${img.customer.profile.dimension}")
	private Integer imgProfileDimension;

	
	public Customer fetch(Integer id) throws ObjectNotFoundException, AuthorizationException {
		UserSpringSecurity authenticatedUser = UserService.authenticated();
		if( (authenticatedUser == null) || 
			(!authenticatedUser.getId().equals(id) && !authenticatedUser.hasRole(UserProfile.ADMIN)) ) {
			throw new AuthorizationException("Access Denied");
		}
		
		Optional<Customer> ret = repository.findById(id);

		return ret.orElseThrow(
			() -> new ObjectNotFoundException("Object not found: TYPE: "+Customer.class.getName()+", ID: "+id)
		);

	}
	
	public Customer fetchByEmail(String email) {
		UserSpringSecurity authenticatedUser = UserService.authenticated();
		if( (authenticatedUser == null) || 
			(!authenticatedUser.getUsername().equals(email) && !authenticatedUser.hasRole(UserProfile.ADMIN)) ) {
			throw new AuthorizationException("Access Denied");
		}
		
		Customer customer = repository.findByEmail(email);
		if( customer == null ) {
			throw new ObjectNotFoundException("Object not found: TYPE: "+Customer.class.getName()+", email: "+email);
		}
		
		return customer;
	}

	public List<CustomerDTO> fetchAll() {
		List<Customer> customerList = repository.findAll();
		List<CustomerDTO> customerDTOList = customerList.stream()
				.map(customer -> new CustomerDTO(customer))
				.collect(Collectors.toList());
		return customerDTOList;
	}

	/*
	 * Direction can be: "ASC" or "DESC"
	 */
	public Page<CustomerDTO> pagingAll(Integer pageNumber, Integer linesPerPage, String direction, String ... orderBy) {
		PageRequest pageRequest = PageRequest.of(pageNumber, linesPerPage, Direction.valueOf(direction), orderBy);
		Page<Customer> customerPage = repository.findAll(pageRequest);
		Page<CustomerDTO> customerDTOPage = customerPage
				.map(customer -> new CustomerDTO(customer));
		return customerDTOPage;
	}

	@Transactional
	public Customer insert(Customer customer) {
		//Forcing to insert a new Customer instead of updating
		customer.setId(null);
		return repository.save(customer);
	}

	public void update(Customer customer) {
		Customer customerToBeUpdated = fetch(customer.getId()); // Throws an exception if customer is not found
		customerUpdateData(customerToBeUpdated,customer);
		repository.save(customerToBeUpdated);
	}

	private void customerUpdateData(Customer customerToBeUpdated, Customer customerWithNewData) {
		// Only those information is allowed to be updated
		customerToBeUpdated.setName(customerWithNewData.getName());
		customerToBeUpdated.setEmail(customerWithNewData.getEmail());
	}

	public void delete(Integer id) {
		Customer customerToBeDeleted = fetch(id); // Throws an exception if customer is not found
		try {
			repository.delete(customerToBeDeleted);
		} catch( DataIntegrityViolationException ex ) {
			throw new DataIntegrityException("It is not allowed to delete customer who has put orders.");
		}
	}


	public Customer getCustomerFromDTO(CustomerDTO dto) {
		return Customer.builder()
				.id(dto.getId())
				.name(dto.getName())
				.email(dto.getEmail())
				.build();
	}


	public Customer getCustomerFromInsertDTO(InsertCustomerDTO dto) {
		Customer customer = Customer.builder()
				.name(dto.getName())
				.email(dto.getEmail())
				.documentNumber(dto.getDocumentNumber())
				.customerType(dto.getCustomerType())
				.phones(dto.getPhoneNumbers())
				.password(passwordEncoder.encode(dto.getPassword()))
				.userProfiles(dto.getUserProfiles())
				//Every customer has, at least, a "Customer Profile"
				.userProfile(UserProfile.CUSTOMER)
				.build();


		for( InsertAddressDTO addressDTO : dto.getAddresses() ) {
			Address address = Address.builder()
					.customer(customer)
					.street(addressDTO.getStreet())
					.number(addressDTO.getNumber())
					.complement(addressDTO.getComplement())
					.district(addressDTO.getDistrict())
					.zipCode(addressDTO.getZipCode())
					.city(City.builder().id(addressDTO.getCityId()).build())
					.build();
			customer.getAddresses().add(address);
		}
		
		return customer;
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSpringSecurity authenticatedUser = UserService.authenticated();
		if( (authenticatedUser == null) ) { 
			throw new AuthorizationException("Access Denied");
		}

		BufferedImage jpgBufferedImage = imageService.getJpgImageFromFile(multipartFile);
		
		jpgBufferedImage = imageService.cropSquare(jpgBufferedImage);
		
		jpgBufferedImage = imageService.resize(jpgBufferedImage, imgProfileDimension);

		String fileName = imgProfilePrefix + authenticatedUser.getId() +".jpg";
		
		return s3Service.uploadFile(
				imageService.getInputStrem( jpgBufferedImage, "jpg" ),
				fileName,
				"image");
	}
	
	

}
