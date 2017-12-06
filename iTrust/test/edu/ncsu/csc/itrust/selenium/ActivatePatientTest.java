package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import edu.ncsu.csc.itrust.enums.TransactionType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class ActivatePatientTest extends iTrustSeleniumTest{

    protected WebDriver driver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testViewPreregisteredPatients() throws Exception {
        //Login
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");

        //Click the add patients link
        driver.findElement(By.linkText("Preregistered Patients")).click();
        assertEquals("iTrust - View Preregistered Patients", driver.getTitle());

        //Check for table
        assertNotNull(driver.findElement(By.id("patientList")));
    }

    public void testEditPreregisteredPatient() throws Exception {
        //Login
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");

        //Click the add patients link
        driver.findElement(By.linkText("Preregistered Patients")).click();
        assertEquals("iTrust - View Preregistered Patients", driver.getTitle());

        //Edit Prereg Person
        driver.findElement(By.linkText("Prereg Person")).click();
        assertEquals("iTrust - Edit Preregistered Patient", driver.getTitle());
        driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys("Preregistered");
        driver.findElement(By.xpath("//input[@name='heightStr']")).sendKeys("5");
        driver.findElement(By.xpath("//input[@value='Edit Patient Record']")).click();
        assertTrue(driver.findElement(By.xpath("//body")).getText().contains("Information Successfully Updated"));
    }

    public void testActivatePreregisteredPatient() throws Exception {
        //Login
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");

        //Click the add patients link
        driver.findElement(By.linkText("Preregistered Patients")).click();
        assertEquals("iTrust - View Preregistered Patients", driver.getTitle());

        //Edit Prereg Person
        driver.findElement(By.linkText("Prereg Person")).click();
        assertEquals("iTrust - Edit Preregistered Patient", driver.getTitle());

        //Activate Patient
        driver.findElement(By.xpath("//input[@value='Activate Patient']")).click();
        assertTrue(driver.findElement(By.xpath("//body")).getText().contains("Patient Successfully Activated"));
    }

    public void testDeactivatePreregisteredPatient() throws Exception {
        //Login
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");

        //Click the add patients link
        driver.findElement(By.linkText("Preregistered Patients")).click();
        assertEquals("iTrust - View Preregistered Patients", driver.getTitle());

        //Edit Prereg Person
        driver.findElement(By.linkText("Prereg Person")).click();
        assertEquals("iTrust - Edit Preregistered Patient", driver.getTitle());

        //Deactivate Patient
        driver.findElement(By.xpath("//input[@value='Deactivate Patient']")).click();
        String body = driver.findElement(By.xpath("//body")).getText();
        assertTrue(driver.findElement(By.xpath("//body")).getText().contains("Patient Successfully Deactivated"));
    }
}