<%@page import="edu.ncsu.csc.itrust.action.FilteredEventLoggingAction"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean"%>
<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="edu.ncsu.csc.itrust.enums.Role" %>

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
        String startDateString = request.getParameter("startDate");
        startDate = ("".equals(startDateString) || startDateString == null) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(startDateString));
        String endDateString = request.getParameter("endDate");
        endDate = ("".equals(endDateString) || endDateString == null) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(endDateString));
        if (request.getParameter("submitSum") != null ) {
            url = action.sumTransactionLog(request.getParameter("userRole"), request.getParameter("secondaryRole"), startDate, endDate, request.getParameter("transactionType"));
        }
        else {
            accesses = action.viewTransactionLog(request.getParameter("userRole"), request.getParameter("secondaryRole"), startDate, endDate, request.getParameter("transactionType"));
        }
    } catch (Exception e) {
        System.out.println(e.getStackTrace());
        accesses = action.viewTransactionLog(null, null, null, null, null);
    }
    String displayStartDate = (startDate == null) ? "":new SimpleDateFormat("MM/dd/yyyy").format(startDate);
    String displayEndDate = (endDate == null)? "":new SimpleDateFormat("MM/dd/yyyy").format(endDate);
    String userRoleSelect = request.getParameter("userRole");
    String secondaryRoleSelect = request.getParameter("secondaryRole");

%>
<form action="viewFilteredTransactionLog.jsp" id="logRoleSelectionForm" method="post">

    <input type="hidden" name="sortBy" value=""></input>

    <div align=center>
        <table class="fTable" align="center">
            <tr class="subHeader">
                <td>View log for: </td>
                <td>
                    <label for="userRoleSelectMenu">User Role</label>
                    <select name="userRole" id="userRoleSelectMenu" selected = "<%= StringEscapeUtils.escapeHtml("" + (userRoleSelect)) %>">
                        <option value="all"> All</option>
                        <%
                            for( Role role: Role.values()) {
                        %>
                        <option value="<%= role.getUserRolesString() %>" <%if(role.getUserRolesString().equals(userRoleSelect)){%>selected<%}%> ><%= role.getUserRolesString() %></option>
                        <%
                            }
                        %>

                    </select>
                </td>
                <td>
                    <label for="secondaryRoleSelectMenu">Secondary Role</label>
                    <select name="secondaryRole" id="secondaryRoleSelectMenu">
                        <option value="all"> All</option>
                        <%
                            for( Role role: Role.values()) {
                        %>
                        <option value="<%= role.getUserRolesString() %>" <%if(role.getUserRolesString().equals(secondaryRoleSelect)){%>selected<%}%>><%= role.getUserRolesString() %></option>
                        <%
                            }
                        %>

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
                        <option value="all"> All</option>
                        <%
                            for( TransactionType transactionType: TransactionType.values()) {
                        %>
                        <option value="<%= transactionType.getCode() %>" ><%= transactionType.name() %></option>
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
            String userRoleString[] = new String[2];
            userRoleString[0] = action.getUserRole(t.getLoggedInMID()).getUserRolesString();
            Role secondUserRole = action.getUserRole(t.getSecondaryMID());
            userRoleString[1] = (secondUserRole == null) ? "N/A" : secondUserRole.getUserRolesString();
    %>

    <tr>
        <td ><%= StringEscapeUtils.escapeHtml("" + (t.getTimeLogged())) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (userRoleString[0]))%></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (userRoleString[1])) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (t.getTransactionType().name())) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + (t.getAddedInfo())) %></td>
    </tr>
    <%
            }
        }

    %>
</table>



<script type='text/javascript'>
    function sortBy(dateOrRole) {
        document.getElementsByName('sortBy')[0].value = dateOrRole;
        document.forms[0].submit.click();
    }

</script>

<%@include file="/footer.jsp"%>