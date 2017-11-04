package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.FilteredEventLoggingAction;
import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class FilteredEventLoggingActionTest extends TestCase{

    private FilteredEventLoggingAction action;
    private DAOFactory factory;
    private long mid = 1L;
    TestDataGenerator gen;

    @Override
    protected void setUp() throws Exception {
        factory = TestDAOFactory.getTestInstance();
        action = new FilteredEventLoggingAction(factory);
        gen = new TestDataGenerator();
    }

    public void testViewTransactionLog() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        List<TransactionBean> list = action.viewTransactionLog("hcp", "patient", new SimpleDateFormat("MM/dd/yyyy").parse("03/03/2003"), new SimpleDateFormat("MM/dd/yyyy").parse("12/31/2012"), "1900");
        assertEquals(9.0, list.get(0).getLoggedInMID()/1e9 );
        assertTrue( list.get(0).getSecondaryMID()/1e9 < 1e3 );
        assertEquals(1900, list.get(0).getTransactionType().getCode() );
    }

    public void testSumTransactionLog1() throws Exception{
        gen.clearAllTables();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date startDate = new Date(df.parse("01-01-2001").getTime());
        Date endDate = new Date(df.parse("01-01-2010").getTime());
        String url = action.sumTransactionLog("hcp", "patient", startDate, endDate, "410");
        assertTrue("No Transaction Log Available for This Filtering.".equals(url));
    }

    public void testSumTransactionLog2() throws Exception{
        gen.clearAllTables();
        gen.standardData();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date startDate = new Date(df.parse("06-25-2007").getTime());
        Date endDate = new Date(df.parse("06-26-2007").getTime());
        String url = action.sumTransactionLog("hcp", "patient", startDate, endDate, "1900");
        String expectedURL = "<div><img name=\"chart1\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|2007-6"
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Month+and+Year\"></div><div><img name=\"chart2\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|1900"
                + "&amp;chbh=25,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Type\"></div><div><img name=\"chart3\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|hcp"
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Logged-in+User\"></div><div><img name=\"chart4\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|patient"
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Secondary+User\"></div>";
        System.out.println(url);
        assertTrue(url.equals(expectedURL));
    }
}
