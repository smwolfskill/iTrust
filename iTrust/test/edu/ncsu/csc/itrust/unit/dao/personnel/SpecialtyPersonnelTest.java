package edu.ncsu.csc.itrust.unit.dao.personnel;

import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.util.List;

public class SpecialtyPersonnelTest extends TestCase {
    PersonnelDAO personnelDAO = TestDAOFactory.getTestInstance().getPersonnelDAO();
    TestDataGenerator gen;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
    }

    /**
     * Tests that the getAllSpecialties method returns a list of all of the unique specialties.
     * @throws Exception
     */
    public void testgetAllSpecialties() throws Exception {
        List<String> result = personnelDAO.getAllSpecialties();
        assertEquals(14, result.size());
    }
}
