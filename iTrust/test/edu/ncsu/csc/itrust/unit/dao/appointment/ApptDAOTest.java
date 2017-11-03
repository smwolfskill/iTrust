package edu.ncsu.csc.itrust.unit.dao.appointment;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
 * @last_edit   11/03/17, Scott Wolfskill
 */
public class ApptDAOTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private ApptDAO apptDAO = factory.getApptDAO();

	/*private ApptBean a1;
	private ApptBean a2;
	private ApptBean a3;*/
	private ApptBean[] appts; //[0..2] are original a1..a3
	
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
 		//Original redundant code:
		/*a1 = new ApptBean();
		a1.setDate(new Timestamp(new Date().getTime()));
		a1.setApptType("Ultrasound");
		a1.setHcp(doctorMID);
		a1.setPatient(patientMID);
		
		a2 = new ApptBean();
		a2.setDate(new Timestamp(new Date().getTime()+1000*60*15));	//15 minutes later
		a2.setApptType("Ultrasound");
		a2.setHcp(doctorMID);
		a2.setPatient(patientMID);
		
		a3 = new ApptBean();
		a3.setDate(new Timestamp(new Date().getTime()+1000*60*45));	//45 minutes later
		a3.setApptType("Ultrasound");
		a3.setHcp(doctorMID);
		a3.setPatient(patientMID);*/
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
	
}
