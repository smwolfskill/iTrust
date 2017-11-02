package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

public class SendReminderAction {
    private long loggedInMID;
    private SendMessageAction smAction;

    public SendReminderAction(DAOFactory factory, long loggedInMID) {
        this.loggedInMID = loggedInMID;
        this.smAction = new SendMessageAction(factory, loggedInMID);
    }

    public void sendReminder(ApptBean aBean) throws ITrustException, SQLException, FormValidationException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = aBean.getDate().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, MMM d");

        MessageBean message = new MessageBean();
        message.setTo(aBean.getPatient());
        message.setFrom(aBean.getHcp());
        message.setSubject(String.format("Reminder: upcoming appointment in %d day(s)", now.until(date, ChronoUnit.DAYS)));
        message.setBody(String.format("You have an appointment on %s with Dr. %s", date.format(formatter), smAction.getPersonnelName(aBean.getHcp())));
        message.setSentDate(Timestamp.valueOf(now));

        smAction.sendMessage(message);
    }
}
