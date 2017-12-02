<%@page import="java.util.List"%>
<%@ page import="edu.ncsu.csc.itrust.action.ViewDeathReportAction" %>
<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - View Death Report";
%>

<%@include file="/header.jsp"%>

<h1>Viewing Death Report</h1>
<br />

<%
    ViewDeathReportAction action = new ViewDeathReportAction(DAOFactory.getProductionInstance());
    List<List<String>> diagnoses;

    String startDate = request.getParameter("startDate");
    startDate = startDate == null ? "" : startDate;
    String endDate = request.getParameter("endDate");
    endDate = endDate == null ? "" : endDate;
    String genderSelect = request.getParameter("gender");
    int startDateInt = -1, endDateInt = -1;
    String gender = null;
    try {
        if("".equals(startDate) && "".equals(endDate) && genderSelect == null) {}
        else {
            startDateInt = Integer.parseInt(startDate);
            endDateInt = Integer.parseInt(endDate);
            if (endDateInt < startDateInt) {
                throw new NumberFormatException();
            }
            gender = genderSelect;
        }
    } catch (NumberFormatException e) {
        %>
<h2>Information not valid</h2><div class="errorList">Enter a valid year.<br /></div>
        <%
    }

%>
<form action="viewDeathReport.jsp" id="logRoleSelectionForm" method="post">

    <div align=center>
        <table class="fTable" align="center">
            <tr class="subHeader">
                <td>View log for: </td>
                <td>
                    <label for="genderSelectMenu">Gender</label>
                    <select name="gender" id="genderSelectMenu" selected = "<%= StringEscapeUtils.escapeHtml("" + (genderSelect)) %>">
                        <option value="All">All</option>
                        <option value="Female">Female</option>
                        <option value="Male">Male</option>

                    </select>
                </td>
            </tr>
            <tr class="subHeader">
                <td>Start Date:</td>
                <td>
                    <input name="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (startDate)) %>">
                </td>
                <td>End Date:</td>
                <td>
                    <input name="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (endDate)) %>">
                </td>
            </tr>
        </table>
        <br />
        <input type="submit" name="submitView" value="View Filtered Death Report">
    </div>
</form>
<br/>
<br/>
<% if(gender != null) { %>
<table class="fTable" align='center'>
    <tr>
        <th>ICD-9CM</th>
        <th>Diagnoses Name</th>
        <th>Number of Diagnoses</th>
    </tr>

    <%
        diagnoses = action.getDeathsForHCP(loggedInMID, gender, startDateInt, endDateInt);

        for(List<String> l : diagnoses) {
    %>

    <tr>
        <td ><%= StringEscapeUtils.escapeHtml("" + l.get(0)) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + l.get(1))%></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + l.get(2)) %></td>
    </tr>
    <%
        }
        loggingAction.logEvent(TransactionType.DEATH_TRENDS_VIEW, loggedInMID.longValue(), 0, "");

    %>
</table>
<table class="fTable" align='center'>
    <tr>
        <th>ICD-9CM</th>
        <th>Diagnoses Name</th>
        <th>Number of Diagnoses</th>
    </tr>
        <%
        diagnoses = action.getDeaths(gender, startDateInt, endDateInt);
        for(List<String> l : diagnoses) {
    %>

    <tr>
        <td ><%= StringEscapeUtils.escapeHtml("" + l.get(0)) %></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + l.get(1))%></td>
        <td ><%= StringEscapeUtils.escapeHtml("" + l.get(2)) %></td>
    </tr>
    <%
        }
}
    %>
</table>

<%@include file="/footer.jsp"%>
