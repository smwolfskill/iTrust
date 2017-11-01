package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.FilteredEventLoggingAction;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

    public void testSumTransactionLog1() throws Exception{
        gen.clearAllTables();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date startDate = new Date(df.parse("01-01-2001").getTime());
        Date endDate = new Date(df.parse("01-01-2010").getTime());
        String url = action.sumTransactionLog("hcp", "patient", startDate, endDate, 410);
        assertTrue("No Transaction Log Available for This Filtering.".equals(url));
    }

    public void testSumTransactionLog2() throws Exception{
        gen.clearAllTables();
        gen.standardData();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date startDate = new Date(df.parse("06-25-2007").getTime());
        Date endDate = new Date(df.parse("06-25-2007").getTime());
        String url = action.sumTransactionLog("hcp", "patient", startDate, endDate, 1900);
        String expectedURL = "<div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|2008-06"
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Month+and+Year\"></div><div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|1900"
                + "&amp;chbh=25,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Type\"></div><div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|hcp"
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Logged-in+User\"></div><div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + "100"
                + "&amp;chxr=1,0,"
                + "3"
                + "&amp;chxl=0:"
                + "|patient"
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Secondary+User\"></div>";
    }
}
