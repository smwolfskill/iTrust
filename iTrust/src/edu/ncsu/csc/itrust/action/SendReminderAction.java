package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

public class SendReminderAction {
    private long loggedInMID;
    private PatientDAO patientDAO;
    private PersonnelDAO personnelDAO;

    public SendReminderAction(DAOFactory factory, long loggedInMID) {
        this.loggedInMID = loggedInMID;
        this.patientDAO = factory.getPatientDAO();
        this.personnelDAO = factory.getPersonnelDAO();
    }

    public void sendReminder(ApptBean aBean) throws ITrustException, SQLException, FormValidationException {
        
    }
}
