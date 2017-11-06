package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.SendReminderAction;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.MessageDAO;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


public class SendReminderActionTest extends TestCase {

	private DAOFactory factory;
	private MessageDAO messageDAO;
	private SendReminderAction srAction;
	private TestDataGenerator gen;
	private long patientId;
	private long hcpId;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		
		this.patientId = 2L;
		this.hcpId = 9000000000L;
		this.factory = TestDAOFactory.getTestInstance();
		this.messageDAO = new MessageDAO(this.factory);
		this.srAction = new SendReminderAction(this.factory, this.hcpId);
	}

	public void testSendReminderAction() throws ITrustException
	{
		int numberOfAppts = srAction.sendReminderForAppointments(10);
		assertTrue(numberOfAppts >= 5);
		/* Cannot use 8 appts. b/c flaky: relies on current day of month.
		 * In DB only 5 are guaranteed to be created ALWAYS within 10 days from now. */
	}

	public void testSendReminder() throws ITrustException, SQLException, FormValidationException {
		ApptBean aBean = new ApptBean();

        aBean.setApptType("TEST");
        aBean.setPatient(patientId);
        aBean.setHcp(hcpId);
        aBean.setDate(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));

        List<MessageBean> mbListBefore = messageDAO.getMessagesFor(patientId);

		srAction.sendReminder(aBean);
		
		List<MessageBean> mbList = messageDAO.getMessagesFor(patientId);

		assertEquals(mbList.size(), mbListBefore.size() + 1);
		MessageBean mBeanDB = mbList.get(0);
		assertEquals("Reminder: upcoming appointment in 3 day(s)", mBeanDB.getSubject());
	}

}
