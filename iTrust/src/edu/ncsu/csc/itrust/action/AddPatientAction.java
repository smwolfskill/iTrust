package edu.ncsu.csc.itrust.action;


import edu.ncsu.csc.itrust.RandomPassword;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.dao.mysql.PreRegisterDAO;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.validate.AddPatientValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Used for Add Patient page (addPatient.jsp). This just adds an empty patient, creates a random password for
 * that patient.
 * 
 * Very similar to {@link AddOfficeVisitAction}
 * 
 * 
 */
public class AddPatientAction {
	private PatientDAO patientDAO;
	private AuthDAO authDAO;
	private long loggedInMID;
	private PersonnelDAO personnelDAO;
	private PreRegisterDAO preRegisterDAO;

	/**
	 * Just the factory and logged in MID
	 * 
	 * @param factory
	 * @param loggedInMID
	 */
	public AddPatientAction(DAOFactory factory, long loggedInMID) {
		this.patientDAO = factory.getPatientDAO();
		this.loggedInMID = loggedInMID;
		this.authDAO = factory.getAuthDAO();
		this.personnelDAO = factory.getPersonnelDAO();
		this.preRegisterDAO = factory.getPreRegisterDAO();
	}
	
	/**
	 * Creates a new patient, returns the new MID. Adds a new user to the table with a 
	 * specified dependency
	 * 
	 * @param p patient to be created
	 * @param isDependent true if the patient is to be a dependent, false otherwise
	 * @return the new MID of the patient
	 * @throws FormValidationException if the patient is not successfully validated
	 * @throws ITrustException 
	 */
	public long addDependentPatient(PatientBean p, long repId) throws FormValidationException, ITrustException {
		new AddPatientValidator().validate(p);
		long newMID = patientDAO.addEmptyPatient();
		boolean isDependent = true;
		p.setMID(newMID);
		String pwd = authDAO.addUser(newMID, Role.PATIENT, RandomPassword.getRandomPassword());
		
		patientDAO.addRepresentative(repId, newMID);
		authDAO.setDependent(newMID, isDependent);
		p.setPassword(pwd);
		patientDAO.editPatient(p, loggedInMID);
		return newMID;
	}
	
	public long addPatient(PatientBean p) throws FormValidationException, ITrustException {
		new AddPatientValidator().validate(p);
		long newMID = patientDAO.addEmptyPatient();
		p.setMID(newMID);
		String pwd = authDAO.addUser(newMID, Role.PATIENT, RandomPassword.getRandomPassword());
		p.setPassword(pwd);
		patientDAO.editPatient(p, loggedInMID);
		return newMID;
	}

	public long addPreRegisteredPatient(PatientBean p, String height, String weight, String smoker) throws FormValidationException, ITrustException {
		new AddPatientValidator().validate(p);
		long newMID = patientDAO.addEmptyPatient();
		p.setMID(newMID);
		p.setDateOfDeactivationStr(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		patientDAO.editPatient(p,personnelDAO.searchForPersonnelWithName("Shape","Shifter").get(0).getMID());

		preRegisterDAO.addPreregisterPatient(p.getMID(),height,weight,smoker);

		return newMID;
	}
}
