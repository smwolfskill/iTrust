package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.RequestBiosurveillanceAction;
import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.dao.mysql.OfficeVisitDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class RequestBiosurveillanceActionTest extends TestCase {
    private final String MALARIA = "84.50";
    private final String INFLUENZA = "487.00";

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
        assertEquals("Yes", action.detectEpidemic(MALARIA, "27606",
                presentCases , 5.0));

    }


}
