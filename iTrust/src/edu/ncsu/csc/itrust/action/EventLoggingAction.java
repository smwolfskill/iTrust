package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;

import java.sql.Date;

/**
 * Handles retrieving the log of record accesses for a given user Used by viewAccessLog.jsp
 * 
 * 
 */
public class EventLoggingAction {
	private TransactionDAO transDAO;

	/**
	 * Set up
	 * 
	 * @param factory The DAOFactory used to create the DAOs used in this action.
	 * @param loggedInMID The MID of the person retrieving the logs.
	 */
	public EventLoggingAction(DAOFactory factory) {
		this.transDAO = factory.getTransactionDAO();
	}

	/**
	 * Log a transaction, with all of the info. The meaning of secondaryMID and addedInfo changes depending on
	 * the transaction type.
	 * 
	 * @param type The {@link TransactionType} enum representing the type this transaction is.
	 * @param loggedInMID The MID of the user who is logged in.
	 * @param secondaryMID Typically, the MID of the user who is being acted upon.
	 * @param addedInfo A note about a subtransaction, or specifics of this transaction (for posterity).
	 * @throws DBException
	 */
	public void logEvent(TransactionType type, long loggedInMID, long secondaryMID, String addedInfo)
			throws DBException {
		this.transDAO.logTransaction(type, loggedInMID, secondaryMID, addedInfo);
	}

	/*
	userRole: "all" for all, role otherwise
	secondaryRole: "all" for all, role otherwise
	startDate: Must always be defined, default Jan 1 1970
	endDate: Must always be defined, default current date
	transType: -1 for all, type number otherwise
	 */
	public void viewTransactionLog(String userRole, String secondaryRole, Date startDate, Date endDate, int transType){

	}

	/*
	userRole: "all" for all, role otherwise
	secondaryRole: "all" for all, role otherwise
	startDate: Must always be defined, default Jan 1 1970
	endDate: Must always be defined, default current date
	transType: -1 for all, type number otherwise
	 */
	public void sumTransactionLog(String userRole, String secondaryRole, Date startDate, Date endDate, int transType){

	}
}
