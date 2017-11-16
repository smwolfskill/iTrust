package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.RequestBiosurveillanceAction;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RequestBiosurveillanceActionTest extends TestCase {
    private RequestBiosurveillanceAction requestBiosurveillanceAction;
    private TestDataGenerator gen;
    private DAOFactory factory;

    protected void setUp() throws Exception {
        factory = TestDAOFactory.getTestInstance();
        this.requestBiosurveillanceAction = new RequestBiosurveillanceAction(factory);
        super.setUp();
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
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
