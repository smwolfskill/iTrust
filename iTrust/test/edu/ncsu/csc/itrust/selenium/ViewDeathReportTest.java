package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class ViewDeathReportTest extends iTrustSeleniumTest {
    private WebDriver driver = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testViewDeathReportTest() throws Exception {
        //Login
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());

        //Click on "Transaction Log"
        driver.findElement(By.linkText("View Death Report")).click();

        //Select "hcp" for first role
        new Select(driver.findElement(By.name("gender"))).selectByVisibleText("All");

        //Set dates
        driver.findElement(By.name("startDate")).clear();
        driver.findElement(By.name("startDate")).sendKeys("0");
        driver.findElement(By.name("endDate")).clear();
        driver.findElement(By.name("endDate")).sendKeys("9999");

        //Click on "Summary button"
        driver.findElement(By.name("submitView")).click();

        //Find charts
        assertNotNull(driver.findElement(By.className("fTable")));
        assertTrue(driver.getPageSource().contains("250.10"));
    }
}
