<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="edu.ncsu.csc.itrust.action.WeeklyScheduleAction" %>
<%@ page import="edu.ncsu.csc.itrust.beans.ApptBean" %>
<%@ page import="java.util.List" %>
<%@ page import="javafx.util.Pair" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - Weekly Scheduling";
%>

<%@include file="/header.jsp"%>

<h1>Weekly Scheduling</h1>
<br />


<%
    WeeklyScheduleAction action = new WeeklyScheduleAction(DAOFactory.getProductionInstance());

    String dateString = "";
    Date date;

    try {
        dateString = request.getParameter("date");
        dateString = (dateString == null) ? "" : dateString;
        date = ("".equals(dateString)) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(dateString));
    } catch (Exception e) {
        date = new Date(); //current date
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        dateString = format.format(date);
    }
%>

<form action="viewWeeklySchedule.jsp" id="scheduleDateForm" method="post">
    <div align=center>
        <table class="fTable" align="center">
            <tr>
                <td> Date: </td>
                <td>
                    <input name="date" value="<%= dateString %>" size="10">
                    <input type=button value="Select Date" onclick="displayDatePicker('date');">
                </td>
            </tr>
        </table>
        <br/>
        <input type="submit" name="viewSchedule" value="View Schedule">
    </div>
</form>
<br/>
<table class="fTable" align='center'>
    <%
        if( request.getParameter("viewSchedule") != null ) {
            WeeklyScheduleAction.HeatmapData heatmapData = action.getHeatmapForWeekOf(date);
            int earliest = heatmapData.earliestAndLatest.getKey();
            int latest = heatmapData.earliestAndLatest.getValue();
            %>
            <table align="center" cellspacing="5">
                <colgroup span="8">
                </colgroup>
                <tr>
                    <th></th>
                    <th>Sun</th>
                    <th>Mon</th>
                    <th>Tues</th>
                    <th>Wed</th>
                    <th>Thurs</th>
                    <th>Fri</th>
                    <th>Sat</th>
                </tr>
                <%
                    for(int hour = heatmapData.colorMap[0].length - 1; hour >= 0; hour--) {
                        %>
                        <tr>
                            <th><%= action.hourOfDay_toString(hour + earliest) %></th>
                            <%
                                for(int day = 0; day < 7; day++) {
                                    %>
                                    <th style="background-color:<%= heatmapData.colorMap[day][hour] %>"></th>
                                    <%
                                }
                            %>
                        </tr>
                        <%
                    }
                %>
                <!--<tr>
                    <th></th>
                    <th style="background-color:#0000FF">1</th>
                    <th style="background-color:#000088">1481348143714</th>
                </tr>
                <tr>
                    <th style="background-color:#00008F">           </th>
                    <th style="background-color:#0000FF">aef</th>
                </tr>-->
            </table>
            <table align="right">
                <%
                    for(int i = 0; i <= heatmapData.maxNumAppt; i++) {
                %>
                <tr>
                    <th><%= i %></th>
                    <%
                        String colorBlank = action.colorMap(i, heatmapData.maxNumAppt);
                    %>
                    <th style="background-color:<%= colorBlank %>"></th>
                </tr>
                <%
                    }
                %>
            </table>
    <%
    }
    %>

</table>

<%@include file="/footer.jsp"%>
