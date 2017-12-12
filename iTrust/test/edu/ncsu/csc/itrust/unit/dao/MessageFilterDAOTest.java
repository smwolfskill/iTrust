package edu.ncsu.csc.itrust.unit.dao;

import edu.ncsu.csc.itrust.dao.mysql.MessageFilterDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

public class MessageFilterDAOTest extends TestCase {
    MessageFilterDAO messageFilterDAO = TestDAOFactory.getTestInstance().getMessageFilterDAO();

    @Override
    protected void setUp() throws Exception {
        TestDataGenerator gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.uap1();
        gen.hcp0();
    }

    public void testEditMessageFilter() throws DBException {
        messageFilterDAO.editMessageFilter(90000000000L, "ASD");
        assertEquals("ASD", messageFilterDAO.getMessageFilter(90000000000L));
    }

    public void testGetMessageFilter_notExistingMid() throws DBException {
        assertEquals("", messageFilterDAO.getMessageFilter(911111));
    }

}
