package br.com.technomori.ordermanager;

import java.util.logging.Logger;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test

public class TestSuite {
	
	private static Logger logger = Logger.getLogger(TestSuite.class.getName());
	
//	final static String SERVER_ADDRESS = "https://silviomori-order-manager.herokuapp.com";
	final static String SERVER_ADDRESS = "http://localhost:8080";

	@BeforeSuite
	public void beforeSuite() {
		logger.info("Initializing tests ...");
	}
	
	@AfterSuite
	public void afterSuite() {
		logger.info("... tests completed!");
	}
}
