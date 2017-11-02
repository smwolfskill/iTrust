package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import java.util.*;

public class SendReminderAction {
    private long loggedInMID;
    private PatientDAO patientDAO;
    private PersonnelDAO personnelDAO;
    private ApptDAO apptDAO;

    public SendReminderAction(DAOFactory factory, long loggedInMID) {
        this.loggedInMID = loggedInMID;
        this.patientDAO = factory.getPatientDAO();
        this.personnelDAO = factory.getPersonnelDAO();
        this.apptDAO = factory.getApptDAO();
    }

    public void sendReminderForAppointments(int numDays) throws ITrustException
    {
        List<ApptBean> appointments = null;
       try
       {
           appointments = apptDAO.getUpcomingAppts(numDays);
           for(ApptBean appt : appointments) {
               sendReminder(appt);
           }
       }
       catch (DBException e)
       {
           throw new ITrustException("DB Error in sending reminders.");
       }
       catch (SQLException e)
       {
           throw new ITrustException("SQL Error in sending reminders.");
       }
       catch (FormValidationException e) {
           throw new ITrustException("FormValidation Error in sending reminders.");
       }

    }

    public void sendReminder(ApptBean aBean) throws ITrustException, SQLException, FormValidationException {
        
    }
}
