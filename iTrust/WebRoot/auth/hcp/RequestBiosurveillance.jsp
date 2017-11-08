<%--
  Created by IntelliJ IDEA.
  User: xiaorui
  Date: 2017/11/7
  Time: 下午6:17
  To change this template use File | Settings | File Templates.
--%>
<%@page import="edu.ncsu.csc.itrust.action.FilteredEventLoggingAction"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean"%>
<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="edu.ncsu.csc.itrust.enums.Role" %>
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

    Date date;

    try {
        String dateString = request.getParameter("date");
        date = ("".equals(dateString) || dateString == null) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(dateString));
    } catch (Exception e) {
        date = null;
    }
%>

<form action="requestBiosurveillance.jsp" id="biosurveillanceSelectionForm" method="post">
<div align=center>
    <table class="fTable" align="center">
        <tr>
            <td> icdCode: </td>
            <td>
                <input name="icdCode" value="" size="10">
            </td>
            <td> zipCode: </td>
            <td>
                <input name="zipCode" value="" size="10">
            </td>
            <td> Date: </td>
            <td>
                <input name="date" value="" size="10">
                <input type=button value="Select Date" onclick="displayDatePicker('date');">
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



    <%--    boolean hasData = false;
    //        int index = 0;
    //        loggingAction.logEvent(TransactionType.ACCESS_LOG_VIEW, loggedInMID, 0, "");
    //        System.out.println("beginning");
    //--%>

    <%
        if( request.getParameter("seeTrend") != null ) {
    %>
    <tr>
        <td ><%= (action.seeTrends(request.getParameter("icdCode"), request.getParameter("zipCode"), date)) %></td>
    </tr>
    <%
    } else if(request.getParameter("detectEpidemic") != null ) {
    %>
        <tr>
            <td ><%= (action.detectEpidemic(request.getParameter("icdCode"), request.getParameter("zipCode"), date)) %></td>
        </tr>
    <%
    }
    %>

</table>

<%@include file="/footer.jsp"%>