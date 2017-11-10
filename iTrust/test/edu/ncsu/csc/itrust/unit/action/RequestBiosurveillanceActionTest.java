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

import java.util.Date;

public class RequestBiosurveillanceActionTest extends TestCase {
    private static int visitIdCount = 0;
    private final String MALARIA = "84.50";
    private final String INFLUENZA = "487.00";

    private TestDataGenerator gen;
    private RequestBiosurveillanceAction action;
    private DAOFactory factory = TestDAOFactory.getTestInstance();
    private DiagnosesDAO diagnosesDAO;
    private OfficeVisitDAO officeVisitDAO;

    private class Sickness {
        public OfficeVisitBean visit;
        public DiagnosisBean diagnosis;
        public Sickness(String icdCode, String date, long patientId) {
            diagnosis = new DiagnosisBean();
            diagnosis.setICDCode(icdCode);
            diagnosis.setVisitID(visitIdCount);
            visit = new OfficeVisitBean(visitIdCount);
            visit.setVisitDateStr(date);
            visit.setHcpID(9000000000L);
            visit.setNotes("Diagnose Influenza");
            visit.setPatientID(patientId);
            visit.setHospitalID("1");
            visitIdCount++;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
        action = new RequestBiosurveillanceAction(factory);
        diagnosesDAO = factory.getDiagnosesDAO();
        officeVisitDAO = factory.getOfficeVisitDAO();
    }

    public void testDetectEpidemic_ZipCode() throws Exception{

        assertEquals("invalid zip code", action.detectEpidemic("anything", "ABCDE", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "1234", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "01234", new Date(),.0));
        assertEquals("invalid zip code", action.detectEpidemic("anything", "1A234", new Date(),.0));
        //assertEquals(null, action.detectEpidemic("84.50", "13234", new Date(),.0));
    }

    public void testDetectEpidemic_IcdCode() throws Exception {

        assertEquals("invalid diagnosis code", action.detectEpidemic("anything", "12334", new Date(),.0));
        assertEquals("No analysis can occur", action.detectEpidemic("12.3", "12334", new Date(),.0));
        assertEquals("No analysis can occur", action.detectEpidemic("90", "12334", new Date(),.0));

    }

    public void testWeekNumber() {
       // assertEquals(45, action.weekNumber(new Date(2017,11,9)));
    }

    public void testDetectEpidemic() throws Exception {
        isInfluenzaEpidemic_test();
        isMalariaEpidemic_test();
    }

    private void isInfluenzaEpidemic_test() throws Exception {


    }

    private void isMalariaEpidemic_test() throws  Exception {
        Sickness[] sicknesses = new Sickness[3];
        sicknesses[0] = new Sickness(MALARIA, "01/03/2010",1);
        sicknesses[1] = new Sickness(MALARIA, "01/06/2011", 1);
        sicknesses[2] = new Sickness(MALARIA, "01/05/2012", 1);
        gen.clearAllTables();
        gen.patient1();

        //Test 1: Empty DB, not epidemic
        assertEquals("No", action.detectEpidemic(MALARIA, "27606", sicknesses[2].visit.getVisitDate(), 1.0));

        //Add 3 cases to DB
        for(int i = 0; i < sicknesses.length; i++) {
            diagnosesDAO.add(sicknesses[i].diagnosis);
            officeVisitDAO.add(sicknesses[i].visit);
        }

        //Test 2: Populated, not epidemic
        assertEquals("No", action.detectEpidemic(MALARIA, "27606", sicknesses[2].visit.getVisitDate(), 1.0));

        //Test 3: Epidemic
        Sickness epidemic = new Sickness(MALARIA, "01/04/2012",1);
        diagnosesDAO.add(epidemic.diagnosis);
        officeVisitDAO.add(epidemic.visit);
        assertEquals("Yes", action.detectEpidemic(MALARIA, "27606", sicknesses[2].visit.getVisitDate(), 0.9));
    }
}
