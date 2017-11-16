package edu.ncsu.csc.itrust.unit.dao.patient;

import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.dao.mysql.PreRegisterDAO;
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
    private PreRegisterDAO preRegisterDAO = TestDAOFactory.getTestInstance().getPreRegisterDAO();

    protected void setUp() throws Exception {
        gen.clearAllTables();
        gen.standardData();
    }

    public void testPosPreregisterPatient() throws Exception
    {
        PatientBean p = new PatientBean();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setEmail("abc@xyz.com");
        long pid = patientDAO.addEmptyPatient();
        patientDAO.editPatient(p,pid);

        preRegisterDAO.addPreregisterPatient(pid,"10","10","0");

        assertTrue(preRegisterDAO.checkPreregisteredPatient(pid));
    }

    public void testNEgPreregisterPatient() throws Exception
    {
        PatientBean p = new PatientBean();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setEmail("abc@xyz.com");
        long pid = patientDAO.addEmptyPatient();
        patientDAO.editPatient(p,pid);

        assertFalse(preRegisterDAO.checkPreregisteredPatient(pid));
    }

}