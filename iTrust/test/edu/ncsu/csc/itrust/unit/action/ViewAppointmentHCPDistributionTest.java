package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.ViewAppointmentHCPDistributionAction;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewAppointmentHCPDistributionTest extends TestCase{
    private ViewAppointmentHCPDistributionAction action;
    private DAOFactory factory;
    private TestDataGenerator gen;

    protected void setUp() throws Exception {
        factory = TestDAOFactory.getTestInstance();
        action = new ViewAppointmentHCPDistributionAction(factory);
        gen = new TestDataGenerator();
    }

    /**
     * Tests whether the method is getting a collection of specialties with the correct capacity.
     * @throws Exception
     */
    public void testGetSpecialties() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        List<String> specialties = action.getSpecialties();
        assertEquals(14, specialties.size());
    }

    /**
     * Tests date error handling on null.
     * @throws Exception
     */
    public void testGetDistributionNull() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        String result = action.getDistribution(null,  new Date(), "anything");
        assertEquals("Invalid date.", result);
    }

    /**
     * Tests date error handling on start and end date being the same date.
     * @throws Exception
     */
    public void testGetDistributionInvalidDate1() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        String result = action.getDistribution(new Date(),  new Date(), "anything");
        assertEquals("Invalid date.", result);
    }

    /**
     * Tests date error handling on start date being after end date.
     * @throws Exception
     */
    public void testGetDistributionInvalidDate2() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        Date today = new Date();
        String result = action.getDistribution(new Date(today.getTime() + (1000 * 60 * 60 * 24)),  today, "anything");
        assertEquals("Invalid date.", result);
    }

    public void testGetDistribution() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        //Create a new date on 2012-08-22 and 2012-08-23
        Date startDate = new Date(112, 7, 22);
        Date endDate = new Date(112, 7, 23);

        String result = action.getDistribution(startDate, endDate, "all");

        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String startDateString = simpleDateFormat.format(startDate);
        String endDateString = simpleDateFormat.format(endDate);

        String trueResult = "<img id=\"chart1\" width=720 src=\"https://chart.googleapis.com/chart?" +
                "chtt=" + Integer.toString(1) + "+Appointments+For+" + "all" + "+from+" +
                startDateString + "+to+" +
                endDateString + "&amp;" +
                "cht=p3&amp;chs=500x200&amp;" +
                1 + "&amp;" +
                "John Zoidberg" + "&amp;" +
                "chco=E8D0A9|B7AFA3|C1DAD6|F5FAFA|ACD1E9|6D929B\">";
    }
}
