package edu.ncsu.csc.itrust.unit.dao.patient;

import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.util.List;

public class GetDeathReportTest extends TestCase {
	private PatientDAO patientDAO = TestDAOFactory.getTestInstance().getPatientDAO();
	private TestDataGenerator gen;

	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.icd9cmCodes();
		gen.patient2();
		gen.hcp0();
	}

	public void testGetDeathReport() throws Exception {
		assertEquals(1, patientDAO.getCommonDeaths(-1, "All", 0, 2019).size());
	}
}
