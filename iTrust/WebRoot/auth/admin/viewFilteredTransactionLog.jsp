<%@page import="edu.ncsu.csc.itrust.action.FilteredEventLoggingAction"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean"%>
<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - View Transaction Log";
%>

<%@include file="/header.jsp"%>

<h1>Viewing Transaction Log</h1>
<br />


<%
    session.removeAttribute("personnelList");


    FilteredEventLoggingAction action = new FilteredEventLoggingAction(DAOFactory.getProductionInstance());
    List<TransactionBean> accesses = new ArrayList<>(); //stores entries in the access log
    String url = "";

    Date startDate = null;
    Date endDate = null;
    try {
        startDate = (new SimpleDateFormat("MM/dd/yyyy").parse(request.getParameter("startDate")));
        endDate = (new SimpleDateFormat("MM/dd/yyyy").parse(request.getParameter("endDate")));
        if (request.getParameter("submitSum") != null ) {
            url = action.sumTransactionLog(request.getParameter("userRole"), request.getParameter("secondaryRole"), startDate, endDate, request.getParameter("transactionType"));
        }
        else {
            accesses = action.viewTransactionLog(request.getParameter("userRole"), request.getParameter("secondaryRole"), startDate, endDate, request.getParameter("transactionType"));
        }
    } catch (Exception e) {
        accesses = action.viewTransactionLog(null, null, null, null, null);
    }
    String displayStartDate = (startDate == null) ? "":new SimpleDateFormat("MM/dd/yyyy").format(startDate);
    String displayEndDate = (endDate == null)? "":new SimpleDateFormat("MM/dd/yyyy").format(endDate);

%>
<form action="viewFilteredTransactionLog.jsp" id="logRoleSelectionForm" method="post">

    <input type="hidden" name="sortBy" value=""></input>

    <div align=center>
        <table class="fTable" align="center">
            <tr class="subHeader">
                <td>View log for: </td>
                <td>
                    <select name="userRole" id="userRoleSelectMenu">
                        <option value="1000000000"> All</option>
                        <option value="hcp"> Doctor</option>
                        <option value="patient"> Patient</option>

                    </select>
                </td>
                <td>
                    <select name="secondaryRole" id="secondaryRoleSelectMenu">
                        <option value="1000000000"> All</option>
                        <option value="hcp"> Doctor</option>
                        <option value="patient"> Patient</option>

                    </select>
                </td>
            </tr>
            <tr class="subHeader">
                <td>Start Date:</td>
                <td>
                    <input name="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (displayStartDate)) %>" size="10">
                    <input type=button value="Select Date" onclick="displayDatePicker('startDate');">
                </td>
                <td>End Date:</td>
                <td>
                    <input name="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (displayEndDate)) %>">
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
        <input type="submit" name="submitView" value="View Filtered Records">
        <input type="submit" name="submitSum" value="Sum Filtered Records">
    </div>
</form>
<br/>
<br/>
<table class="fTable" align='center'>



<%--    boolean hasData = false;
//        int index = 0;
//        loggingAction.logEvent(TransactionType.ACCESS_LOG_VIEW, loggedInMID, 0, "");
//        System.out.println("beginning");
//--%>

    <%
        if( request.getParameter("submitSum") != null ) {
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
        //    System.out.println(t.toString());
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

        /*String startDate = action.getDefaultStart(accesses);
        String endDate = action.getDefaultEnd(accesses);
        if("role".equals(request.getParameter("sortBy"))) {
            startDate = request.getParameter("startDate");
            endDate = request.getParameter("endDate");
        }*/



    %>
</table>



<script type='text/javascript'>
    function sortBy(dateOrRole) {
        document.getElementsByName('sortBy')[0].value = dateOrRole;
        document.forms[0].submit.click();
    }

</script>

<%@include file="/footer.jsp"%>