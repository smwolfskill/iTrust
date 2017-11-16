package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.ViewDeathReportAction;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ViewDeathReportActionTest {
    private ViewDeathReportAction dAction = new ViewDeathReportAction(TestDAOFactory.getTestInstance());
    private TestDataGenerator gen;

    @Before
    public void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.icd9cmCodes();
        gen.patient2();
        gen.hcp0();
    }

    @Test
    public void getDeathsTest() throws Exception {
        assertEquals(1, dAction.getDeaths("All", 0, 2019).size());
    }

    @Test
    public void getDeathsForHCPTest() throws Exception {
        assertEquals(1, dAction.getDeathsForHCP(9000000000L, "All", 0, 2019).size());
    }

}