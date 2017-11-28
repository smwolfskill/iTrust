package edu.ncsu.csc.itrust.unit.dao.patient;

import edu.ncsu.csc.itrust.dao.mysql.PreRegisterDAO;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class AddPreRegisteredPatientTest extends TestCase
{
    private TestDataGenerator gen = new TestDataGenerator();
    private PatientDAO patientDAO = TestDAOFactory.getTestInstance().getPatientDAO();
    private PreRegisterDAO preRegisterDAO = TestDAOFactory.getTestInstance().getPreRegisterDAO();

    protected void setUp() throws Exception {
        gen.clearAllTables();
        gen.standardData();
    }

    public void testPosPreregisterPatient() throws Exception
    {
        //Test 1: all fields filled in
        PatientBean p1 = new PatientBean();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("abc@xyz.com");
        long pid1 = patientDAO.addEmptyPatient();
        patientDAO.editPatient(p1, pid1);

        preRegisterDAO.addPreregisterPatient(pid1,"10","10","0");

        assertTrue(preRegisterDAO.checkPreregisteredPatient(pid1));

        //Test 2: null fields
        PatientBean p2 = new PatientBean();
        p2.setFirstName("Jay");
        p2.setLastName("Denver");
        p2.setEmail("xyz@abc.com");
        long pid2 = patientDAO.addEmptyPatient();
        patientDAO.editPatient(p2, pid2);

        preRegisterDAO.addPreregisterPatient(pid2, null, null, null);

        assertTrue(preRegisterDAO.checkPreregisteredPatient(pid2));
    }

    public void testNegPreregisterPatient() throws Exception
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