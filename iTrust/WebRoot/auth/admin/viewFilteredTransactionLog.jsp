<%@page import="edu.ncsu.csc.itrust.action.FilteredEventLoggingAction"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ncsu.csc.itrust.action.GetUserNameAction"%>
<%@ page import="edu.ncsu.csc.itrust.enums.Role" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - View Transaction Log";
%>

<%@include file="/header.jsp"%>

<%
    session.removeAttribute("personnelList");


    FilteredEventLoggingAction action = new FilteredEventLoggingAction(DAOFactory.getProductionInstance());
    List<TransactionBean> accesses = new ArrayList<>(); //stores entries in the access log
    String url = "";


    try {
        Date startDate = (new SimpleDateFormat("MM/dd/yyyy").parse(request.getParameter("startDate")));
        Date endDate = (new SimpleDateFormat("MM/dd/yyyy").parse(request.getParameter("endDate")));
        System.out.println("start");
        if (request.getParameter("submit").equals("Sum Filtered Records")) {
            System.out.println("summmmm");
            url = action.sumTransactionLog(request.getParameter("userRole"), request.getParameter("secondaryRole"), startDate, endDate, request.getParameter("transactionType"));
            System.out.println(url);
        }
        else {
            System.out.println("default");
            accesses = action.viewTransactionLog(request.getParameter("userRole"), request.getParameter("secondaryRole"), startDate, endDate, request.getParameter("transactionType"));
        }
    } catch (Exception e) {
        //        e.printHTML(pageContext.getOut());
        System.out.println("exception-null");
        accesses = action.viewTransactionLog(null, null, null, null, null);
    }


%>
<h1>Viewing Transaction Log</h1>
<br />
<table class="fTable" align='center'>



<%--    boolean hasData = false;
//        int index = 0;
//        loggingAction.logEvent(TransactionType.ACCESS_LOG_VIEW, loggedInMID, 0, "");
//        System.out.println("beginning");
//--%>

    <%
        if( request.getParameter("submit") != null && request.getParameter("submit").equals("Sum Filtered Records")) {
    %>
        <tr>
            <td ><%= (url) %></td>
        </tr>
    <%
        }
        else {
    %>

    <tr>
        <th>Date</th>
        <th>User Role</th>
        <th>Secondary Role</th>
        <th>Transaction Type</th>
        <th>Additional Info</th>
    </tr>

    <%
        for(TransactionBean t : accesses) {
            System.out.println(t.toString());
            String userRoleString[] = new String[2];

            if(t.getLoggedInMID()>1e3)
                userRoleString[0] = "Doctor";
            else
                userRoleString[0] = "Patient";

            if(t.getSecondaryMID()>1e3)
                userRoleString[1] = "Doctor";
            else
                userRoleString[1] = "Patient";


    %>
    <tr>
        <td ><%= StringEscapeUtils.escapeHtml("" + (t.getTimeLogged())) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (userRoleString[0]))%></td>

        <td><%= StringEscapeUtils.escapeHtml("" + (userRoleString[1])) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (t.getTransactionType().getActionPhrase())) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (t.getAddedInfo())) %></td>
    </tr>
    <%
            }
        }

        String startDate = action.getDefaultStart(accesses);
        String endDate = action.getDefaultEnd(accesses);
        if("role".equals(request.getParameter("sortBy"))) {
            startDate = request.getParameter("startDate");
            endDate = request.getParameter("endDate");
        }
    %>
</table>
<br />
<br />

<form action="viewFilteredTransactionLog.jsp" id="logRoleSelectionForm" method="post">

    <input type="hidden" name="sortBy" value=""></input>

    <div align=center>
        <table class="fTable" align="center">
            <tr class="subHeader">
                <td>View log for: </td>
                <td>
                    <select name="userRole" id="userRoleSelectMenu">
                        <option value="1000000000"> All</option>
                        <option value="<%= Role.HCP.getMidFirstDigit() %>"> Doctor</option>
                        <option value="<%= Role.PATIENT.getMidFirstDigit() %>"> Patient</option>

                    </select>
                </td>
                <td>
                    <select name="secondaryRole" id="secondaryRoleSelectMenu">
                        <option value="1000000000"> All</option>
                        <option value="<%= Role.HCP.getMidFirstDigit() %>"> Doctor</option>
                        <option value="<%= Role.PATIENT.getMidFirstDigit() %>"> Patient</option>

                    </select>
                </td>
            </tr>
            <tr class="subHeader">
                <td>Start Date:</td>
                <td>
                    <input name="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (startDate)) %>" size="10">
                    <input type=button value="Select Date" onclick="displayDatePicker('startDate');">
                </td>
                <td>End Date:</td>
                <td>
                    <input name="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (endDate)) %>">
                    <input type=button value="Select Date" onclick="displayDatePicker('endDate');">
                </td>
            </tr>
            <tr class="subHeader">
                <td>Transaction Type:</td>
                <td>
                    <select name="transactionType" id="transactionTypeSelectMenu">
                        <option value="1000000000"> All</option>
                        <%
                        for( TransactionType transactionType: TransactionType.values()) {
                        %>
                        <option value="<%= transactionType.getCode() %>"><%= transactionType.getCode() %></option>
                        <%
                            }
                        %>

                    </select>
                </td>
            </tr>
        </table>
        <br />
        <input type="submit" name="submit" value="View Filtered Records">
        <input type="submit" name="submit" value="Sum Filtered Records">
    </div>
</form>

<script type='text/javascript'>
    function sortBy(dateOrRole) {
        document.getElementsByName('sortBy')[0].value = dateOrRole;
        document.forms[0].submit.click();
    }

</script>

<%@include file="/footer.jsp"%>