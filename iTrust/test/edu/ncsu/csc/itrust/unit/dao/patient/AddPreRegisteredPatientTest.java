package edu.ncsu.csc.itrust.unit.dao.patient;

import edu.ncsu.csc.itrust.beans.PreRegisterBean;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.PreRegisterDAO;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import java.util.*;

public class AddPreRegisteredPatientTest extends TestCase
{
    private TestDataGenerator gen = new TestDataGenerator();
    private PatientDAO patientDAO = TestDAOFactory.getTestInstance().getPatientDAO();
    private PreRegisterDAO preRegisterDAO = TestDAOFactory.getTestInstance().getPreRegisterDAO();
    private AuthDAO authDAO = TestDAOFactory.getTestInstance().getAuthDAO();

    protected void setUp() throws Exception {
        gen.clearAllTables();
        gen.standardData();
        gen.patient31();
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

        assertTrue(authDAO.getPreregistered(pid1));

        //Test 2: null fields
        PatientBean p2 = new PatientBean();
        p2.setFirstName("Jay");
        p2.setLastName("Denver");
        p2.setEmail("xyz@abc.com");
        long pid2 = patientDAO.addEmptyPatient();
        patientDAO.editPatient(p2, pid2);

        preRegisterDAO.addPreregisterPatient(pid2, null, null, null);

        assertTrue(authDAO.getPreregistered(pid2));
    }

    public void testNegPreregisterPatient() throws Exception
    {
        PatientBean p = new PatientBean();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setEmail("abc@xyz.com");
        long pid = patientDAO.addEmptyPatient();
        patientDAO.editPatient(p,pid);

        assertFalse(authDAO.getPreregistered(pid));
    }

    public void testGetPreregisterPatients() throws Exception
    {
        PatientBean p1 = new PatientBean();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("abc@xyz.com");
        long pid1 = patientDAO.addEmptyPatient();
        p1.setMID(pid1);
        patientDAO.editPatient(p1, pid1);

        preRegisterDAO.addPreregisterPatient(pid1,"10","10","0");

        assertTrue(authDAO.getPreregistered(pid1));

        List<PreRegisterBean> preRegPat = preRegisterDAO.getPreregisteredPatients();
        assertEquals(2,preRegPat.size());
        assertEquals("Prereg",preRegPat.get(0).getPatient().getFirstName());
    }

    public void testGetPreregisterPatient() throws Exception
    {
        PatientBean p1 = new PatientBean();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("abc@xyz.com");
        long pid1 = patientDAO.addEmptyPatient();
        p1.setMID(pid1);
        patientDAO.editPatient(p1, pid1);

        preRegisterDAO.addPreregisterPatient(pid1,"10","10","0");

        assertTrue(authDAO.getPreregistered(pid1));

        PreRegisterBean preRegPat = preRegisterDAO.getPreregisteredPatient(pid1);

        assertEquals("John",preRegPat.getPatient().getFirstName());

    }

    public void testActivatePreregisteredPatient() throws Exception
    {
        PatientBean p1 = new PatientBean();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("abc@xyz.com");
        long pid1 = patientDAO.addEmptyPatient();
        p1.setMID(pid1);
        patientDAO.editPatient(p1, pid1);

        preRegisterDAO.addPreregisterPatient(pid1,"10","10","0");

        preRegisterDAO.activatePreregisteredPatient(pid1,90000000L);

        assertFalse(authDAO.getPreregistered(pid1));
    }

    public void testDeactivatePreregisteredPatient() throws Exception
    {

        PatientBean p1 = new PatientBean();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("abc@xyz.com");
        long pid1 = patientDAO.addEmptyPatient();
        p1.setMID(pid1);
        patientDAO.editPatient(p1, pid1);

        preRegisterDAO.addPreregisterPatient(pid1,"10","10","0");

        preRegisterDAO.deactivatePreregisteredPatient(pid1);

        List<PreRegisterBean> preRegPat = preRegisterDAO.getPreregisteredPatients();
        assertEquals(1,preRegPat.size());

    }

    public void testEditPreregisteredPatient() throws Exception
    {
        PatientBean p1 = new PatientBean();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("abc@xyz.com");
        long pid1 = patientDAO.addEmptyPatient();
        p1.setMID(pid1);
        patientDAO.editPatient(p1, pid1);

        PreRegisterBean preReg = new PreRegisterBean();
        preReg.setPatient(p1);
        preReg.setHeight("10");
        preReg.setWeight("10");
        preReg.setSmoker("0");

        preRegisterDAO.addPreregisterPatient(pid1,"10","10","0");

        preReg.getPatient().setFirstName("Joe");
        preRegisterDAO.editPreregisteredPatient(preReg,pid1);
        PreRegisterBean preRegPat = preRegisterDAO.getPreregisteredPatient(pid1);

        assertEquals("Joe",preRegPat.getPatient().getFirstName());



    }

}