package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class MessageFilterTest extends iTrustSeleniumTest {
    private WebDriver driver = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testTestFilter() throws Exception {
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());

        driver.findElement(By.linkText("Message Inbox")).click();

        driver.findElement(By.linkText("Edit Filter")).click();

        driver.findElement(By.name("sender")).clear();
        driver.findElement(By.name("sender")).sendKeys("Andy Programmer");

        driver.findElement(By.name("test")).click();

        assertFalse(driver.findElement(By.className("fancyTable")) == null);
        assertTrue(driver.getPageSource().contains("Scratchy Throat"));
        assertFalse(driver.getPageSource().contains("Random Person"));
    }

    public void testSaveFilter() throws Exception {
        driver = login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());

        driver.findElement(By.linkText("Message Inbox")).click();

        driver.findElement(By.linkText("Edit Filter")).click();

        driver.findElement(By.name("sender")).clear();
        driver.findElement(By.name("sender")).sendKeys("Andy Programmer");

        driver.findElement(By.name("save")).click();

        assertFalse(driver.findElement(By.className("fancyTable")) == null);
        assertTrue(driver.getPageSource().contains("Scratchy Throat"));
        assertFalse(driver.getPageSource().contains("Random Person"));

        driver.findElement(By.linkText("Message Inbox")).click();
        driver.findElement(By.linkText("Apply Filter")).click();

        assertFalse(driver.findElement(By.className("fancyTable")) == null);
        assertTrue(driver.getPageSource().contains("Scratchy Throat"));
        assertFalse(driver.getPageSource().contains("Random Person"));
    }
}
