package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import edu.ncsu.csc.itrust.enums.TransactionType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class AddPatientTest extends iTrustSeleniumTest{
	
	protected WebDriver driver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}
	

	
	public void testBlankPatientName() throws Exception{
		//Login
		driver = login("9000000000", "pw");
		assertEquals("iTrust - HCP Home", driver.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		
		//Click the add patients link
		driver.findElement(By.linkText("Patient")).click();
		assertEquals("iTrust - Add Patient", driver.getTitle());

		//Enter in information but blank first name
		driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("");
		driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys("Doe");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("john.doe@example.com");
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//body")).getText().contains("This form has not been validated correctly."));
		
		//Enter in information but blank last name
		driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("John");
		driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys("");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("john.doe@example.com");
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//body")).getText().contains("This form has not been validated correctly."));
	}
	
	public void testInvalidPatientName() throws Exception{
		//Login
		driver = login("9000000000", "pw");
		assertEquals("iTrust - HCP Home", driver.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		
		//Click the add patients link
		driver.findElement(By.linkText("Patient")).click();
		assertEquals("iTrust - Add Patient", driver.getTitle());

		//Enter in information but invalid first name
		driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("----");
		driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys("Doe");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("john.doe@example.com");
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//body")).getText().contains("This form has not been validated correctly."));
		
		//Enter in information but invalid last name
		driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("John");
		driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys("----");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("john.doe@example.com");
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//body")).getText().contains("This form has not been validated correctly."));
	}
	
	//Should pass with email changes
	public void testInvalidPatientEmail() throws Exception{
		//Login
		driver = login("9000000000", "pw");
		assertEquals("iTrust - HCP Home", driver.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		
		//Click the add patients link
		driver.findElement(By.linkText("Patient")).click();
		assertEquals("iTrust - Add Patient", driver.getTitle());
	
		//Enter in information but invalid email
		driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("John");
		driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys("Doe");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("---@---.com");
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		assertFalse(driver.findElement(By.xpath("//body")).getText().contains("This form has not been validated correctly."));
	}

	public void testPreRegisteredPatient() throws Exception {
		WebDriver htmlDriver = new Driver();
		htmlDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		htmlDriver.get("http://localhost:8080/iTrust/");

		//Click the pre-register link
		htmlDriver.findElement(By.linkText("Pre-Register")).click();
		assertEquals("iTrust - Pre-Register", htmlDriver.getTitle());

		//Enter in information
		htmlDriver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("John");
		htmlDriver.findElement(By.xpath("//input[@name='lastName']")).sendKeys("Doe");
		htmlDriver.findElement(By.xpath("//input[@name='email']")).sendKeys("---@---.com");
		htmlDriver.findElement(By.xpath("//input[@name='password']")).sendKeys("abc123");
		htmlDriver.findElement(By.xpath("//input[@name='confirmPassword']")).sendKeys("abc123");
		htmlDriver.findElement(By.xpath("//input[@value='Submit']")).click();
		assertTrue(htmlDriver.findElement(By.xpath("//body")).getText().contains("Account pre-registered"));

	}
}