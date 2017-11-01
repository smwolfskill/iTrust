package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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
    public void viewTransactionLog(String userRole, String secondaryRole, Date startDate, Date endDate, int transType){

    }

    /*
    userRole: "all" for all, role otherwise
    secondaryRole: "all" for all, role otherwise
    startDate: Must always be defined, default Jan 1 1970
    endDate: Must always be defined, default current date
    transType: -1 for all, type number otherwise
     */
    public String sumTransactionLog(String userRole, String secondaryRole, Date startDate, Date endDate, int transType) throws ITrustException {
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
            String loggedInRole = bean.getRole();
            populateDataFromBeanList(loggedInData, loggedInRole);

            //second bar chart: secondary user role v.s. number of transaction
            long secondaryMID = bean.getSecondaryMID();
            String secondaryUserRole = secondaryRole.equals("all") ? this.authDAO.getUserRole(secondaryMID).getUserRolesString() : secondaryRole;
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

        return "<div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + dateTransNum
                + "&amp;chxr=1,0,"
                + dateMaxString
                + "&amp;chxl=0:"
                + dates
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Month+and+Year\"></div><div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + typeTransNum
                + "&amp;chxr=1,0,"
                + typeMaxString
                + "&amp;chxl=0:"
                + types
                + "&amp;chbh=25,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Type\"></div><div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + loggedInTransNum
                + "&amp;chxr=1,0,"
                + loggedInMaxString
                + "&amp;chxl=0:"
                + loggedInUsers
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Logged-in+User\"></div><div><img src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
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
}
