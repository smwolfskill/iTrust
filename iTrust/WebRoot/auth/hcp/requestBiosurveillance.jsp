<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="edu.ncsu.csc.itrust.action.RequestBiosurveillanceAction" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - Request Biosurveillance";
%>

<%@include file="/header.jsp"%>

<h1>Request Biosurveillance</h1>
<br />


<%

    RequestBiosurveillanceAction action = new RequestBiosurveillanceAction(DAOFactory.getProductionInstance());

    String dateString = "";
    Date date;

    String icdCode = "";
    String zipCode = "";
    String thresholdString = "";
    Double threshold = 0.0;

    try {
        dateString = request.getParameter("date");
        dateString = (dateString == null) ? "" : dateString;
        date = ("".equals(dateString)) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(dateString));
    } catch (Exception e) {
        date = null;
    }

    try {
        thresholdString = request.getParameter("threshold");
        thresholdString = (thresholdString == null) ? "" : thresholdString;
        threshold = Double.parseDouble(thresholdString);
    } catch (Exception e) {
        threshold = null;
    }

    icdCode = request.getParameter("icdCode");
    icdCode = (icdCode == null) ? "" : icdCode;
    zipCode = request.getParameter("zipCode");
    zipCode = (zipCode == null) ? "" : zipCode;
%>

<form action="requestBiosurveillance.jsp" id="biosurveillanceSelectionForm" method="post">
<div align=center>
    <table class="fTable" align="center">
        <tr>
            <td> Diagnosis Code (ICD): </td>
            <td>
                <input name="icdCode" value="<%= icdCode %>" size="10">
            </td>
            <td> ZIP Code: </td>
            <td>
                <input name="zipCode" value="<%= zipCode%>" size="10">
            </td>
        </tr>
        <tr>
            <td> Date: </td>
            <td>
                <input name="date" value="<%= dateString %>" size="10">
                <input type=button value="Select Date" onclick="displayDatePicker('date');">
            </td>
            <td>Threshold: </td>
            <td>
                <input name="threshold" value="<%= thresholdString %>">
            </td>
        </tr>
    </table>
    <br/>
    <input type="submit" name="getEpidemic" value="Request Epidemic Analysis">
    <input type="submit" name="seeTrend" value="See Trends">
</div>
</form>
<br/>
<table class="fTable" align='center'>

    <%
        if( request.getParameter("seeTrend") != null ) {
    %>

    <tr>
        <td ><div><%= (action.seeTrends(request.getParameter("icdCode"), request.getParameter("zipCode"), date)) %></div></td>
    </tr>
    <tr>
        <td>Note: Threshold not taken into account for "See Trends".</td>
    </tr>
    <%
    } else if(request.getParameter("detectEpidemic") != null ) {
    %>
        <tr>
            <td ><div><%= (action.detectEpidemic(request.getParameter("icdCode"), request.getParameter("zipCode"), date, threshold)) %></div></td>
        </tr>
    <%
    }
    %>

</table>

<%@include file="/footer.jsp"%>