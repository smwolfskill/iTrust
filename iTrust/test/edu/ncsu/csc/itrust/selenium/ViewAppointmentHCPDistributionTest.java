package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class ViewAppointmentHCPDistributionTest extends iTrustSeleniumTest {
    private WebDriver driver = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    /**
     * Tests the nonexistence of the returned chart when no date is selected.
     * @throws Exception
     */
    public void testSeeDistributionNullDate() throws Exception {
        //Login as admin
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click on "Appointment Distribution Among HCPs"
        driver.findElement(By.linkText("Appointment Distribution Among HCPs")).click();

        //Select "all" for specialty
        new Select(driver.findElement(By.name("specialty"))).selectByVisibleText("All");

        //Set dates
        driver.findElement(By.name("startDate")).clear();
        driver.findElement(By.name("endDate")).clear();
        driver.findElement(By.name("endDate")).sendKeys("06/26/2007");

        //Click on "See Distribution button"
        driver.findElement(By.name("seeDistribution")).click();

        //Find charts
        assertTrue(driver.getPageSource().contains("Invalid date."));
        assertTrue(driver.findElements(By.id("chart1")).size() == 0);
    }

    /**
     * Tests the nonexistence of the returned chart when invalid date is selected. (start date is after end date)
     * @throws Exception
     */
    public void testSeeDistributionInvalidDate() throws Exception {
        //Login as admin
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click on "Appointment Distribution Among HCPs"
        driver.findElement(By.linkText("Appointment Distribution Among HCPs")).click();

        //Select "all" for specialty
        new Select(driver.findElement(By.name("specialty"))).selectByVisibleText("All");

        //Set dates
        driver.findElement(By.name("startDate")).clear();
        driver.findElement(By.name("startDate")).sendKeys("06/27/2007");
        driver.findElement(By.name("endDate")).clear();
        driver.findElement(By.name("endDate")).sendKeys("06/26/2007");

        //Click on "See Distribution button"
        driver.findElement(By.name("seeDistribution")).click();

        //Find charts
        assertTrue(driver.getPageSource().contains("Invalid date."));
        assertTrue(driver.findElements(By.id("chart1")).size() == 0);
    }

    /**
     * Tests the existence of the returned chart when inputs are valid.
     * @throws Exception
     */
    public void testSeeDistribution() throws Exception {
        //Login as admin
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click on "Appointment Distribution Among HCPs"
        driver.findElement(By.linkText("Appointment Distribution Among HCPs")).click();

        //Select "all" for specialty
        new Select(driver.findElement(By.name("specialty"))).selectByVisibleText("All");

        //Set dates
        driver.findElement(By.name("startDate")).clear();
        driver.findElement(By.name("startDate")).sendKeys("06/25/2007");
        driver.findElement(By.name("endDate")).clear();
        driver.findElement(By.name("endDate")).sendKeys("06/26/2007");

        //Click on "See Distribution button"
        driver.findElement(By.name("seeDistribution")).click();

        //Find charts
        assertFalse(driver.getPageSource().contains("Invalid date."));
        assertFalse(driver.findElements(By.id("chart1")).size() == 0);
    }
}
