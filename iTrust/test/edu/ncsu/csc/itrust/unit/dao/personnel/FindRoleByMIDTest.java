package edu.ncsu.csc.itrust.unit.dao.personnel;

import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.util.List;

public class FindRoleByMIDTest extends TestCase {
    private TransactionDAO tranDAO = TestDAOFactory.getTestInstance().getTransactionDAO();

    private TestDataGenerator gen;
    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testFindRoleByMID1() throws Exception {
        String patient = tranDAO.findRoleByMID(1L);
        assertEquals("patient", patient);
        String hcp = tranDAO.findRoleByMID(9000000000L);
        assertEquals("hcp", hcp);
    }

    public void testFindRoleByMID2() throws Exception {
        String role = tranDAO.findRoleByMID(0L);
        assertEquals("", role);
    }
}
