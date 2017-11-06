package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SendReminderTest extends iTrustSeleniumTest{

    protected WebDriver driver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testSendReminders() throws Exception {
        //1. Test admin send reminders
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());
        driver.findElement(By.linkText("Send Reminders")).click();
        driver.findElement(By.name("numberOfDays")).sendKeys("10");
        driver.findElement(By.name("numberOfDays")).submit();
        assertEquals("Reminders sent succesfully !",
                driver.findElement(By.className("iTrustMessage")).getText());

        //2. Test admin view reminders
        driver.findElement(By.linkText("Reminders Outbox")).click();
        assertNotNull(driver.findElement(By.className("fancyTable")));
    }
}
