package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.RequestBiosurveillanceAction;
import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.dao.mysql.OfficeVisitDAO;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RequestBiosurveillanceActionTest extends TestCase {
    private final String MALARIA = "84.50";
    private final String INFLUENZA = "487.00";

public class RequestBiosurveillanceActionTest extends TestCase {
    private RequestBiosurveillanceAction requestBiosurveillanceAction;
    private TestDataGenerator gen;
    private RequestBiosurveillanceAction action;
    private DAOFactory factory = TestDAOFactory.getTestInstance();

    protected void setUp() throws Exception {
        factory = TestDAOFactory.getTestInstance();
        this.requestBiosurveillanceAction = new RequestBiosurveillanceAction(factory);
        super.setUp();
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
        action = new RequestBiosurveillanceAction(factory);
    }

    public void testDetectEpidemic_ZipCode() throws Exception{
        assertEquals("invalid zip code", action.detectEpidemic("anything", "ABCDE", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "1234", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "1A234", new Date(),.0));
    }

    public void testDetectEpidemic_IcdCode() throws Exception {
        assertEquals("invalid diagnosis code", action.detectEpidemic("anything", "12334", new Date(),.0));
        assertEquals("No analysis can occur", action.detectEpidemic("12.3", "12334", new Date(),.0));
        assertEquals("No analysis can occur", action.detectEpidemic("90", "12334", new Date(),.0));
    }

    public void testWeekNumber() {
        assertEquals(3, action.weekNumber(new Date(2017, 0, 16)));
        assertEquals(45, action.weekNumber(new Date(2017,10,9)));
    }

    public void testDetectEpidemic() throws Exception {
        Date presentCases = new Date(new Date().getYear(),10, 9);
        isInfluenzaEpidemic_test(presentCases);
        isMalariaEpidemic_test(presentCases);
    }

    private void isInfluenzaEpidemic_test(Date presentCases) throws Exception {
        //Test 1: Empty DB, not epidemic
        gen.clearAllTables();
        assertEquals("No", action.detectEpidemic(INFLUENZA, "27606",
                presentCases , 0.));

        //Test 2: No influenza data, not epidemic
        gen.standardData();
        assertEquals("No", action.detectEpidemic(INFLUENZA, "27606",
                presentCases , 0.));

        //Test 3: Populated, epidemic
        gen.influenza_epidemic();
        assertEquals("Yes", action.detectEpidemic(INFLUENZA, "27606",
                presentCases , 0.));
    }

    private void isMalariaEpidemic_test(Date presentCases) throws  Exception {
        //Test 1: Empty DB, not epidemic
        gen.clearAllTables();
        assertEquals("No", action.detectEpidemic(MALARIA, "27606",
               presentCases , 0.1));

        //Test 2: No malaria data, not epidemic
        gen.standardData();
        assertEquals("No", action.detectEpidemic(MALARIA, "27606",
                presentCases , 0.1));

        //Test 3: Populated, epidemic
        gen.malaria_epidemic();
        assertEquals("Yes", action.detectEpidemic(MALARIA, "27606",
                presentCases , 1.0));

        //Test 4: Show threshold yields correct results: still epidemic for < 5
        assertEquals("Yes", action.detectEpidemic(MALARIA, "27606",
                presentCases , 4.9));
        assertEquals("No", action.detectEpidemic(MALARIA, "27606",
                presentCases , 6.0));

    }

    public void testSeeTrends1() throws Exception {
        String result = requestBiosurveillanceAction.seeTrends("84", "61801", new SimpleDateFormat("MM/dd/yyyy").parse("11/07/2017"));
        assertTrue("Invalid diagnosis code. Please try again!".equals(result));
    }

    public void testSeeTrends2() throws Exception {
        String result = requestBiosurveillanceAction.seeTrends("84.50", "6180", new SimpleDateFormat("MM/dd/yyyy").parse("11/07/2017"));
        assertTrue("Invalid zip code. Please try again!".equals(result));
    }

    public void testSeeTrends3() throws Exception {
        String result = requestBiosurveillanceAction.seeTrends("84.50", "61801", null);
        assertTrue("Invalid date. Please try again!".equals(result));
    }

    //test exact zip code and zip code within the region
    public void testSeeTrends4() throws Exception {
        String exactResult = requestBiosurveillanceAction.seeTrends("84.50", "27607", new SimpleDateFormat("MM/dd/yyyy").parse("07/19/2011"));
        String regionResult = requestBiosurveillanceAction.seeTrends("84.50", "27611", new SimpleDateFormat("MM/dd/yyyy").parse("07/19/2011"));
        String expected = "<img id=\"diagchart\" src=\"https://chart.googleapis.com/chart?cht=bvg" +
                "&amp;chs=480x320" +
                "&amp;chd=t:" +
                "0,0,0,0,0,0,0,100" +
                "|" +
                "0,0,0,0,0,0,0,100" +
                "|" +
                "0,0,0,0,0,0,0,100" +
                "&amp;chxr=1,0," +
                "1" +
                "&amp;chco=4D89F9,37FF92,F98602" +
                "&amp;chdl=Region|State|All" +
                "&amp;chbh=10,2,10" +
                "&amp;chxt=x,y" +
                "&amp;chxl=0:|Week+1|Week+2|Week+3|Week+4|Week+5|Week+6|Week+7|Week+8" +
                "&amp;chtt=Diagnoses+by+Week\">";
        assertTrue(exactResult.equals(expected));
        assertTrue(regionResult.equals(expected));
    }

    //test for zip code within the state
    public void testSeeTrends5() throws Exception {
        String stateResult = requestBiosurveillanceAction.seeTrends("84.50", "27111", new SimpleDateFormat("MM/dd/yyyy").parse("07/19/2011"));
        String expected = "<img id=\"diagchart\" src=\"https://chart.googleapis.com/chart?cht=bvg" +
                "&amp;chs=480x320" +
                "&amp;chd=t:" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "0,0,0,0,0,0,0,100" +
                "|" +
                "0,0,0,0,0,0,0,100" +
                "&amp;chxr=1,0," +
                "1" +
                "&amp;chco=4D89F9,37FF92,F98602" +
                "&amp;chdl=Region|State|All" +
                "&amp;chbh=10,2,10" +
                "&amp;chxt=x,y" +
                "&amp;chxl=0:|Week+1|Week+2|Week+3|Week+4|Week+5|Week+6|Week+7|Week+8" +
                "&amp;chtt=Diagnoses+by+Week\">";
        assertTrue(stateResult.equals(expected));
    }

    //test for all zip codes
    public void testSeeTrends6() throws Exception {
        String stateResult = requestBiosurveillanceAction.seeTrends("84.50", "11111", new SimpleDateFormat("MM/dd/yyyy").parse("07/19/2011"));
        String expected = "<img id=\"diagchart\" src=\"https://chart.googleapis.com/chart?cht=bvg" +
                "&amp;chs=480x320" +
                "&amp;chd=t:" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "0,0,0,0,0,0,0,100" +
                "&amp;chxr=1,0," +
                "1" +
                "&amp;chco=4D89F9,37FF92,F98602" +
                "&amp;chdl=Region|State|All" +
                "&amp;chbh=10,2,10" +
                "&amp;chxt=x,y" +
                "&amp;chxl=0:|Week+1|Week+2|Week+3|Week+4|Week+5|Week+6|Week+7|Week+8" +
                "&amp;chtt=Diagnoses+by+Week\">";
        assertTrue(stateResult.equals(expected));
    }

    public void testSeeTrends7() throws Exception {
        String stateResult = requestBiosurveillanceAction.seeTrends("84.50", "11111", new SimpleDateFormat("MM/dd/yyyy").parse("09/05/2011"));
        String expected = "<img id=\"diagchart\" src=\"https://chart.googleapis.com/chart?cht=bvg" +
                "&amp;chs=480x320" +
                "&amp;chd=t:" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "100,0,0,0,0,0,0,0" +
                "&amp;chxr=1,0," +
                "1" +
                "&amp;chco=4D89F9,37FF92,F98602" +
                "&amp;chdl=Region|State|All" +
                "&amp;chbh=10,2,10" +
                "&amp;chxt=x,y" +
                "&amp;chxl=0:|Week+1|Week+2|Week+3|Week+4|Week+5|Week+6|Week+7|Week+8" +
                "&amp;chtt=Diagnoses+by+Week\">";
        assertTrue(stateResult.equals(expected));
    }

    public void testSeeTrends8() throws Exception {
        String stateResult = requestBiosurveillanceAction.seeTrends("84.50", "11111", new SimpleDateFormat("MM/dd/yyyy").parse("09/05/2000"));
        String expected = "<img id=\"diagchart\" src=\"https://chart.googleapis.com/chart?cht=bvg" +
                "&amp;chs=480x320" +
                "&amp;chd=t:" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "0,0,0,0,0,0,0,0" +
                "|" +
                "0,0,0,0,0,0,0,0" +
                "&amp;chxr=1,0," +
                "1" +
                "&amp;chco=4D89F9,37FF92,F98602" +
                "&amp;chdl=Region|State|All" +
                "&amp;chbh=10,2,10" +
                "&amp;chxt=x,y" +
                "&amp;chxl=0:|Week+1|Week+2|Week+3|Week+4|Week+5|Week+6|Week+7|Week+8" +
                "&amp;chtt=Diagnoses+by+Week\">";
        assertTrue(stateResult.equals(expected));
    }
}
