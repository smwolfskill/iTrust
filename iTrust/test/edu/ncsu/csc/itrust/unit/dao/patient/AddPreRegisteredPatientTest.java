package edu.ncsu.csc.itrust.unit.dao.patient;

import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class AddPreRegisteredPatientTest extends TestCase
{
    private TestDataGenerator gen = new TestDataGenerator();
    private PatientDAO patientDAO = TestDAOFactory.getTestInstance().getPatientDAO();
    private PersonnelDAO personnelDAO = TestDAOFactory.getTestInstance().getPersonnelDAO();

    protected void setUp() throws Exception {
        gen.clearAllTables();
        gen.standardData();
    }

    public void testPosPreregisterPatient() throws Exception
    {
        long pid = patientDAO.addEmptyPatient();
        PatientBean p = patientDAO.getPatient(pid);
        p.setDateOfDeactivationStr("01/01/2017");
        patientDAO.editPatient(p,personnelDAO.searchForPersonnelWithName("Shape","Shifter").get(0).getMID());

        assertTrue(patientDAO.isPreRegisteredPatient(pid));
    }
    public void testNegPreRegisterPatient() throws Exception
    {
        long pid = patientDAO.addEmptyPatient();
        PatientBean p = patientDAO.getPatient(pid);

        patientDAO.editPatient(p,9000000003L);

        assertFalse(patientDAO.isPreRegisteredPatient(pid));
        p.setDateOfDeactivationStr("01/01/2017");
        assertFalse(patientDAO.isPreRegisteredPatient(pid));

    }

}