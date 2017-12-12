package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.WeeklyScheduleAction;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyScheduleActionTest extends TestCase {
    private DAOFactory factory = TestDAOFactory.getTestInstance();
    private ApptDAO apptDAO = factory.getApptDAO();
    private TestDataGenerator gen;
    private WeeklyScheduleAction action;

    private Date twoWeeks; //two weeks after current date
    private Date threeWeeks;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.appointment();
        action = new WeeklyScheduleAction(factory);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24*7*2); //2 weeks later
        twoWeeks = cal.getTime();
        cal.add(Calendar.HOUR, 24*7); //3 weeks from now
        threeWeeks = cal.getTime();
    }

    public void testHourOfDay_toString() throws Exception {
        //Test 1
        assertEquals("15:00", action.hourOfDay_toString(15));

        //Test 2. Insert "0" before 1-digit hour
        assertEquals("02:00", action.hourOfDay_toString(2));
    }

    public void testGetApptsForWeekOf() throws Exception {
        List<ApptBean> weekAppts = action.getApptsForWeekOf(twoWeeks);
        assertTrue(weekAppts.size() >= 3 && weekAppts.size() <= 4); //could vary depending on weekday
    }

    public void testGetHeatmapForWeekOf() throws Exception {
        gen.clearAllTables();
        long patientMID = 42L;
        long doctorMID = 9000000000L;
        ApptBean appt = new ApptBean();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        appt.setDate(new Timestamp(cal.getTimeInMillis())); //Sunday at 10am
        appt.setApptType("Ultrasound");
        appt.setHcp(doctorMID);
        appt.setPatient(patientMID);
        apptDAO.scheduleAppt(appt); //add to DB

        WeeklyScheduleAction.HeatmapData data = action.getHeatmapForWeekOf(cal.getTime());
        assertEquals(7, data.colorMap.length); //7 days
        assertEquals(1, data.colorMap[0].length); //1 hour
        assertTrue(10 == data.earliestAndLatest.key); //earliest is 10am
        assertTrue(10 == data.earliestAndLatest.value); //latest is 10am

        //Check values of array:
        assertFalse(data.colorMap[0][0].equals(action.BASE_COLOR_STR)); //has non-empty color data
        for(int day = 1; day < 7; day++) {
            assertEquals(action.BASE_COLOR_STR, data.colorMap[day][0]);
        }
    }
}
