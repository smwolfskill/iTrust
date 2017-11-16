package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.umd.cs.findbugs.graph.Graph;

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
        try {
            accesses = transDAO.getFilteredTransactions(userRole, secondaryRole, startDate, endDate, transType);
            return accesses;
        } catch (DBException e) {
            System.out.println(e.getExtendedMessage());
            return null;
        }
    }


    public Role getUserRole(final long mid) {
        try {
            return this.authDAO.getUserRole(mid);
        } catch (ITrustException e) {
            return null;
        }
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
            Role loggedInRole = getUserRole(bean.getLoggedInMID());
            String loggedInRoleString = loggedInRole.getUserRolesString();
            populateDataFromBeanList(loggedInData, loggedInRoleString);

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

        GraphStringContainer loggedIn = parseGraphData(loggedInData);
        GraphStringContainer secondary = parseGraphData(secondaryData);
        Map<String, Integer> sortedDateData = new TreeMap<>(dateData);
        GraphStringContainer date = parseGraphData(sortedDateData);
        GraphStringContainer transactionType = parseGraphData(transactionTypeData);

        return "<div><img id=\"chart1\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + date.transNumString
                + "&amp;chxr=1,0,"
                + date.maxString
                + "&amp;chxl=0:"
                + date.field
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Month+and+Year&amp;chts=000000,18,l\"></div><div><img id=\"chart2\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + transactionType.transNumString
                + "&amp;chxr=1,0,"
                + transactionType.maxString
                + "&amp;chxl=0:"
                + transactionType.field
                + "&amp;chbh=25,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Type&amp;chts=000000,18,l\"></div><div><img id=\"chart3\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + loggedIn.transNumString
                + "&amp;chxr=1,0,"
                + loggedIn.maxString
                + "&amp;chxl=0:"
                + loggedIn.field
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Logged-in+User&amp;chts=000000,18,l\"></div><div><img id=\"chart4\" src=\"https://chart.googleapis.com/chart?chxt=x,y&amp;cht=bvs&amp;chd=t1:"
                + secondary.transNumString
                + "&amp;chxr=1,0,"
                + secondary.maxString
                + "&amp;chxl=0:"
                + secondary.field
                + "&amp;chbh=45,5&amp;chs=1000x300&amp;chco=76A4FB&amp;chls=2.0&amp;chtt=Transactions+by+Secondary+User&amp;chts=000000,18,l\"></div>";
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

    /**
     *
     * @param data The map containing data labels and points for use in the graph
     * @return The string data to be used in the graph
     */
    private GraphStringContainer parseGraphData(Map<String, Integer> data) {
        GraphStringContainer container = new GraphStringContainer();
        container.field = "";
        container.transNumString = "";
        int max = Collections.max(data.values());
        container.maxString = String.valueOf(max);
        for (Map.Entry<String, Integer> entry: data.entrySet()){
            container.field += "|" + entry.getKey();
            int transNum = entry.getValue()*100/max;
            container.transNumString += String.valueOf(transNum) + ",";
        }
        container.transNumString = container.transNumString.substring(0, container.transNumString.length() - 1);
        return container;
    }
}

class GraphStringContainer {
    String field;
    String maxString;
    String transNumString;
}