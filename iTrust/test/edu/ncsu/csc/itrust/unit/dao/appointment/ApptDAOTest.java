package edu.ncsu.csc.itrust.unit.dao.appointment;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.ApptTypeBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.ApptTypeDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

/**
 * ApptDAOTest --- Class for testing ApptDAO basic functionality, and UC41.1 request.
 *
 * @last_edit   11/30/17, Scott Wolfskill
 */
public class ApptDAOTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private ApptDAO apptDAO = factory.getApptDAO();

	private ApptBean[] appts = null; //[0..2] are original a1..a3
	
	long patientMID = 42L;
	long doctorMID = 9000000000L;
	
	@Override
	protected void setUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.appointmentType();

		appts = new ApptBean[5];
		Long now = new Date().getTime();
		final Long min = 1000L*60L;
		final Long day = min*60L*24L;
		Long[] dates = new Long[] {now, now+min*15, now+min*45, now+day, now+day*3};
		for(int i = 0; i < appts.length; i++) {
			appts[i] = new ApptBean();
			appts[i].setDate(new Timestamp(dates[i]));
			appts[i].setApptType("Ultrasound");
			appts[i].setHcp(doctorMID);
			appts[i].setPatient(patientMID);
 		}
	}

	/**
	 * Test UC_Own
	 * @throws Exception
	 */
	public void testGetApptsForWeekOf() throws Exception {
		//Test 1. Correct results for positive test
		apptDAO.scheduleAppt(appts[0]); //now
		apptDAO.scheduleAppt(appts[3]); //in 1 day exactly
		apptDAO.scheduleAppt(appts[4]); //in 3 days exactly

		List<ApptBean> weekAppts = apptDAO.getApptsForWeekOf(appts[0].getDate());
		assertTrue(weekAppts.size() >= 1 && weekAppts.size() <= 3); //could vary depending on current day of week

		//Test 2. Correct results for negative test
		Calendar cal = Calendar.getInstance();
		cal.setTime(appts[4].getDate());
		cal.add(Calendar.HOUR, 24*7); //1 week later
		weekAppts = apptDAO.getApptsForWeekOf(cal.getTime());
		assertEquals(0, weekAppts.size());
	}

	/**
	 * Test UC41.1 fxn. getUpcomingAppts(int numDays).
	 * @throws Exception
	 */
	public void testGetUpcomingAppts() throws Exception {
		//1. Test empty DB
		List<ApptBean> upcomingAppts = apptDAO.getUpcomingAppts(30);
		assertEquals(0, upcomingAppts.size());

		//2. Test correct results after add appts
		apptDAO.scheduleAppt(appts[0]); //now
		apptDAO.scheduleAppt(appts[3]); //in 1 day exactly
		apptDAO.scheduleAppt(appts[4]); //in 3 days exactly

		upcomingAppts = apptDAO.getUpcomingAppts(1);
		assertEquals(2, upcomingAppts.size()); //assert only 2 returned
	}

	public void testAppointment() throws Exception {
		
		long doctorMID = 9000000000L;
		
		
		List<ApptBean> conflicts = apptDAO.getAllConflictsForDoctor(doctorMID);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[0]); //a1
		apptDAO.scheduleAppt(appts[2]); //a3
		
		conflicts = apptDAO.getAllConflictsForDoctor(doctorMID);
		assertEquals(0, conflicts.size());

	}
	
	public void testAppointmentConflict() throws Exception {
		
		long doctorMID = 9000000000L;		
		
		List<ApptBean> conflicts = apptDAO.getAllConflictsForDoctor(doctorMID);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[0]); //a1
		apptDAO.scheduleAppt(appts[1]); //a2
		
		conflicts = apptDAO.getAllConflictsForDoctor(doctorMID);
		assertEquals(2, conflicts.size());

	}
	
	public void testAppointmentPatientConflict() throws Exception {
		
				
		List<ApptBean> conflicts = apptDAO.getAllConflictsForPatient(patientMID);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[0]); //a1
		apptDAO.scheduleAppt(appts[1]); //a2
		
		conflicts = apptDAO.getAllConflictsForPatient(patientMID);
		assertEquals(2, conflicts.size());

	}
	
	public void testGetConflictForAppointment() throws Exception {
		
		List<ApptBean> conflicts = apptDAO.getAllHCPConflictsForAppt(doctorMID, appts[0]);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[0]); //a1
		
		conflicts = apptDAO.getAllHCPConflictsForAppt(doctorMID, appts[0]);
		assertEquals(1, conflicts.size());
		
		ApptBean a1new = conflicts.get(0);
		
		conflicts = apptDAO.getAllHCPConflictsForAppt(doctorMID, a1new);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[1]); //a2
		
		conflicts = apptDAO.getAllHCPConflictsForAppt(doctorMID, a1new);
		assertEquals(1, conflicts.size());

	}
	
	public void testGetPatientConflictForAppointment() throws Exception {
		
		List<ApptBean> conflicts = apptDAO.getAllPatientConflictsForAppt(patientMID, appts[0]);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[0]); //a1
		
		conflicts = apptDAO.getAllPatientConflictsForAppt(patientMID, appts[0]);
		assertEquals(1, conflicts.size());
		
		ApptBean a1new = conflicts.get(0);
		
		conflicts = apptDAO.getAllHCPConflictsForAppt(doctorMID, a1new);
		assertEquals(0, conflicts.size());
		
		apptDAO.scheduleAppt(appts[1]); //a2
		
		conflicts = apptDAO.getAllPatientConflictsForAppt(patientMID, a1new);
		assertEquals(1, conflicts.size());

	}
	
	public void testGetApptType() throws Exception{
		ApptTypeDAO apptTypeDAO = factory.getApptTypeDAO();
		
		ApptTypeBean type = apptTypeDAO.getApptType("Ultrasound");
		
		assertEquals(30, type.getDuration());
		assertEquals("Ultrasound", type.getName());
	}

	/**
	 * Test to see if getting filtered number of appointments for each hcp is working correctly.
	 * @throws Exception
	 */
	public void testGetAppointmentCountByHCP() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		ApptDAO apptDAO = factory.getApptDAO();

		//Create a new date on today + 7 and today + 8
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, 7);
		Date startDate = cal.getTime();
		cal.add(Calendar.DATE, 1);
		Date endDate = cal.getTime();
		Map<String, Integer> result = apptDAO.getAppointmentCountByHCP(startDate, endDate, "surgeon");
		assertEquals(1, result.size());
		assertTrue(2 <= (int) result.get("Kelly Doctor"));
	}
}
