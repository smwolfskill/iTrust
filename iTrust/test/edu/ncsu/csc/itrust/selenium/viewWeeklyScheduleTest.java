package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class viewWeeklyScheduleTest extends iTrustSeleniumTest{
    private WebDriver driver = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testViewWeeklySchedule_TableExist() throws Exception {
        //Login
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click on "Transaction Log"
        driver.findElement(By.linkText("Weekly Scheduling")).click();

        //Set dates
        driver.findElement(By.name("date")).clear();
        driver.findElement(By.name("date")).sendKeys("10/16/2017");

        //Click on "See Trends" button
        driver.findElement(By.name("viewSchedule")).click();

        //An non-existing date, should include hours from 7-19
        for(int i=7;i<=19;i++)
            assertTrue(driver.getPageSource().contains(i+":00"));
    }

    public void testViewWeeklySchedule_InvalidDate() throws Exception {
        //Login
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click on "Transaction Log"
        driver.findElement(By.linkText("Weekly Scheduling")).click();

        //Set dates
        driver.findElement(By.name("date")).clear();
        driver.findElement(By.name("date")).sendKeys("xxx");

        //Click on "See Trends" button
        driver.findElement(By.name("viewSchedule")).click();

        //Date should be today
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(cal.getTime());
        assertTrue(driver.getPageSource().contains(date));

    }
}
