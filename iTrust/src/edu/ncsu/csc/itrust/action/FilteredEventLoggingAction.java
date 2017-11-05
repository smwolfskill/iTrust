package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilteredEventLoggingAction {

    private TransactionDAO transDAO;
    private AuthDAO authDAO;

    public FilteredEventLoggingAction(DAOFactory factory) {
        this.transDAO = factory.getTransactionDAO();
        this.authDAO = factory.getAuthDAO();
    }

    /*
	userRole: "all" for all, role otherwise
	secondaryRole: "all" for all, role otherwise
	startDate: Must always be defined, default Jan 1 1970
	endDate: Must always be defined, default current date
	transType: -1 for all, type number otherwise
	 */
    public List<TransactionBean> viewTransactionLog(String userRole, String secondaryRole, java.util.Date startDate, java.util.Date endDate, String transType){

        List<TransactionBean> accesses; //stores the log entries
//        List<PersonnelBean> dlhcps;
        try {
            accesses = transDAO.getFilteredTransactions(userRole, secondaryRole, startDate, endDate, transType);
            return accesses;
        } catch (DBException e) {
            System.out.println(e.getExtendedMessage());
            return null;
        }

        //get the medical dependents for a signed in user. If the selected user is not the
        //signed in user or one of the dependents, then the user doesn't have access to the log
//        List<PatientBean> patientRelatives = getRepresented(loggedInMID);
//
//
//        dlhcps = patientDAO.getDeclaredHCPs(mid);
//
//        boolean midInScope = false;
//        for (PatientBean pb : patientRelatives) {
//            if (pb.getMID() == mid)
//                midInScope = true;
//        }
//        if (mid != loggedInMID && !midInScope) { //the selected user in the form is out of scope and can't be shown to the user
//            throw new FormValidationException("Log to View.");
//        }
//
//        //user has either 0 or 1 DLHCP's. Get one if exists so it can be filtered from results
//        long dlhcpID = -1;
//        if(!dlhcps.isEmpty())
//            dlhcpID = dlhcps.get(0).getMID();
//
//        if (startDate == null || endDate == null)
//            return transDAO.getAllRecordAccesses(mid, dlhcpID, getByRole);
//
//        try {
//			/*the way the Date class works, is if you enter more months, or days than
//			 is allowed, it will simply mod it, and add it all together. To make sure it
//			 matches MM/dd/yyyy, I am going to use a Regular Expression
//			 */
//            //month can have 1 or 2 digits, same with day, and year must have 4
//            Pattern p = Pattern.compile("[0-9]{1,2}?/[0-9]{1,2}?/[0-9]{4}?");
//            Matcher m = p.matcher(endDate);
//            Matcher n = p.matcher(startDate);
//            //if it fails to match either of them, throw the form validation exception
//            if (!m.matches() || !n.matches()) {
//                throw new FormValidationException("Enter dates in MM/dd/yyyy");
//            }
//
//            java.util.Date lower = new SimpleDateFormat("MM/dd/yyyy").parse(lostartDatewerDate);
//            java.util.Date upper = new SimpleDateFormat("MM/dd/yyyy").parse(endDate);
//
//            if (lower.after(upper))
//                throw new FormValidationException("Start date must be before end date!");
//            accesses = transDAO.getRecordAccesses(mid, dlhcpID, lower, upper, getByRole);
//        } catch (ParseException e) {
//            throw new FormValidationException("Enter dates in MM/dd/yyyy");
//        }
    }

    /*
    userRole: "all" for all, role otherwise
    secondaryRole: "all" for all, role otherwise
    startDate: Must always be defined, default Jan 1 1970
    endDate: Must always be defined, default current date
    transType: -1 for all, type number otherwise
     */
    public String sumTransactionLog(String userRole, String secondaryRole, Date startDate, Date endDate, String transType) throws ITrustException {
        List<TransactionBean> beanList = this.transDAO.getFilteredTransactions(userRole, secondaryRole, startDate, endDate, transType);

        if (beanList == null || beanList.size() == 0) {
            return "No Transaction Log Available for This Filtering.";
        }
        HashMap<String, Integer> loggedInData = new HashMap<>();
        HashMap<String, Integer> secondaryData = new HashMap<>();
        HashMap<String, Integer> dateData = new HashMap<>();
        HashMap<String, Integer> transactionTypeData = new HashMap<>();

        for (TransactionBean bean: beanList) {

            //first bar chart: logged in user role v.s. number of transactions
            String loggedInRole = this.authDAO.getUserRole(bean.getLoggedInMID()).getUserRolesString();
            populateDataFromBeanList(loggedInData, loggedInRole);

            //second bar chart: secondary user role v.s. number of transaction
            long secondaryMID = bean.getSecondaryMID();
            String secondaryUserRole = "";
            if (secondaryMID == 0) {
                secondaryUserRole = "N/A";
            } else {
                try {
                    secondaryUserRole = this.authDAO.getUserRole(secondaryMID).getUserRolesString();
                } catch (ITrustException e) {
                    secondaryUserRole = "N/A";
                }
            }
            populateDataFromBeanList(secondaryData, secondaryUserRole);

            //third bar chart: month&year v.s. number of transaction
            Timestamp timestamp = bean.getTimeLogged();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            String year = String.valueOf(localDateTime.getYear());
            String month = String.valueOf(localDateTime.getMonthValue());
            String yearAndMonth = year + "-" + month;
            populateDataFromBeanList(dateData, yearAndMonth);

            //fourth bar chart: transaction type v.s. number of transaction
            String transactionTypeCode = String.valueOf(bean.getTransactionType().getCode());
            populateDataFromBeanList(transactionTypeData, transactionTypeCode);
        }

        String loggedInUsers = "";
        int loggedInMax = Collections.max(loggedInData.values());
        String loggedInMaxString = String.valueOf(loggedInMax);
        String loggedInTransNum = "";
        for (Map.Entry<String, Integer> entry: loggedInData.entrySet()){
            loggedInUsers += "|" + entry.getKey();
            int transNum = entry.getValue()*100/loggedInMax;
            loggedInTransNum += String.valueOf(transNum) + ",";
        }
        loggedInTransNum = loggedInTransNum.substring(0, loggedInTransNum.length() - 1);

        String secondaryUsers = "";
        int secondaryMax = Collections.max(secondaryData.values());
        String secondaryMaxString = String.valueOf(secondaryMax);
        String secondaryTransNum = "";
        for (Map.Entry<String, Integer> entry: secondaryData.entrySet()){
            secondaryUsers += "|" + entry.getKey();
            int transNum = entry.getValue()*100/secondaryMax;
            secondaryTransNum += String.valueOf(transNum) + ",";
        }
        secondaryTransNum = secondaryTransNum.substring(0, secondaryTransNum.length() - 1);

        Map<String, Integer> sortedDateData = new TreeMap<>(dateData);
        String dates = "";
        int dateMax = Collections.max(sortedDateData.values());
        String dateMaxString = String.valueOf(dateMax);
        String dateTransNum = "";
        for (Map.Entry<String, Integer> entry: sortedDateData.entrySet()){
            dates += "|" + entry.getKey();
            int transNum = entry.getValue()*100/dateMax;
            dateTransNum += String.valueOf(transNum) + ",";
        }
        dateTransNum = dateTransNum.substring(0, dateTransNum.length() - 1);

        String types = "";
        int typeMax = Collections.max(transactionTypeData.values());
        String typeMaxString = String.valueOf(typeMax);
        String typeTransNum = "";
        for (Map.Entry<String, Integer> entry: transactionTypeData.entrySet()){
            types += "|" + entry.getKey();
            int transNum = entry.getValue()*100/typeMax;
            typeTransNum += String.valueOf(transNum) + ",";
        }
        typeTransNum = typeTransNum.substring(0, typeTransNum.length() - 1);

        return "<div><img id=\"chart1\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + dateTransNum
                + "&amp;chxr=1,0,"
                + dateMaxString
                + "&amp;chxl=0:"
                + dates
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Month+and+Year\"></div><div><img id=\"chart2\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + typeTransNum
                + "&amp;chxr=1,0,"
                + typeMaxString
                + "&amp;chxl=0:"
                + types
                + "&amp;chbh=25,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Type\"></div><div><img id=\"chart3\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + loggedInTransNum
                + "&amp;chxr=1,0,"
                + loggedInMaxString
                + "&amp;chxl=0:"
                + loggedInUsers
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Logged-in+User\"></div><div><img id=\"chart4\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + secondaryTransNum
                + "&amp;chxr=1,0,"
                + secondaryMaxString
                + "&amp;chxl=0:"
                + secondaryUsers
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Secondary+User\"></div>";
    }

    private void populateDataFromBeanList(HashMap<String, Integer> loggedInData, String loggedInRole) {
        if (loggedInData.containsKey(loggedInRole)) {
            loggedInData.put(loggedInRole, loggedInData.get(loggedInRole)+1);
        } else {
            loggedInData.put(loggedInRole, 1);
        }
    }

    public String getDefaultStart(List<TransactionBean> accesses) {
        String startDate = "";
        if (accesses.size() > 0) {
            startDate = new SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(accesses.get(accesses.size() - 1)
                    .getTimeLogged().getTime()));
        } else {
            startDate = new SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        }
        return startDate;
    }

    /**
     * Returns the date of the last Transaction in the list passed as a param if the list is not empty
     * otherwise, returns today's date
     *
     * @param accesses A java.util.List of TransactionBeans storing the access.
     * @return A String representation of the date of the last transaction.
     */
    public String getDefaultEnd(List<TransactionBean> accesses) {
        String endDate = "";
        if (accesses.size() > 0) {
            endDate = new SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(accesses.get(0)
                    .getTimeLogged().getTime()));
        } else {
            endDate = new SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        }
        return endDate;
    }
}
