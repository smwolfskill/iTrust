package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.RequestBiosurveillanceAction;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.util.Date;

public class RequestBiosurveillanceActionTest extends TestCase {

    private TestDataGenerator gen;
    private RequestBiosurveillanceAction action;
    private DAOFactory factory = TestDAOFactory.getTestInstance();


    protected void setUp() throws Exception {
        super.setUp();
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
        action = new RequestBiosurveillanceAction(factory);
    }

    public void testDetectEpidemic_ZipCode() throws Exception{

        assertEquals("invalid zip code", action.detectEpidemic("anything", "ABCDE", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "1234", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "01234", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "1A234", new Date(),.0));
        assertEquals(null, action.detectEpidemic("84.50", "13234", new Date(),.0));
    }

    public void testWeekNumber() {
       // assertEquals(45, action.weekNumber(new Date(2017,11,9)));
    }
}
