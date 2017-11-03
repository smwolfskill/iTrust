package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import java.util.*;

public class SendReminderAction {
    public final long systemReminderMID;
    private long loggedInMID;
    private ApptDAO apptDAO;
    private SendMessageAction smAction;

    public SendReminderAction(DAOFactory factory, long loggedInMID) throws DBException {
        this.systemReminderMID = factory.getPersonnelDAO().searchForPersonnelWithName("System", "Reminder").get(0).getMID();
        this.loggedInMID = loggedInMID;
        this.apptDAO = factory.getApptDAO();
        this.smAction = new SendMessageAction(factory, systemReminderMID);
    }

    public void sendReminder(ApptBean aBean) throws ITrustException, SQLException, FormValidationException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = aBean.getDate().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, MMM d");

        MessageBean message = new MessageBean();
        message.setTo(aBean.getPatient());
        message.setFrom(systemReminderMID);
        message.setSubject(String.format("Reminder: upcoming appointment in %d day(s)", now.truncatedTo(ChronoUnit.DAYS).until(date.truncatedTo(ChronoUnit.DAYS), ChronoUnit.DAYS)));
        message.setBody(String.format("You have an appointment on %s with Dr. %s", date.format(formatter), smAction.getPersonnelName(aBean.getHcp())));
        message.setSentDate(Timestamp.valueOf(now));

        smAction.sendMessage(message);
    }

    public void sendReminderForAppointments(int numDays) throws ITrustException {
        List<ApptBean> appointments = null;
        try {
            appointments = apptDAO.getUpcomingAppts(numDays);
            for (ApptBean appt : appointments) {
                sendReminder(appt);
            }
        } catch (DBException e) {
            throw new ITrustException("DB Error in sending reminders.");
        } catch (SQLException e) {
            throw new ITrustException("SQL Error in sending reminders.");
        } catch (FormValidationException e) {
            throw new ITrustException("Form Validation Error in sending reminders.");
        }

    }
}
