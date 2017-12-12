<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="edu.ncsu.csc.itrust.action.WeeklyScheduleAction" %>

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
        date = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
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
            int earliest = heatmapData.earliestAndLatest.key;
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
                    for(int hour = 0; hour < heatmapData.colorMap[0].length; hour++) {
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
            </table>
            <table align="right">
                <%
                    for(int i = 0; i < heatmapData.apptEntries.size(); i++) {
                %>
                <tr>
                    <%
                        int apptCount = heatmapData.apptEntries.get(i);
                        String colorBlank = action.colorMap(apptCount, heatmapData.maxNumAppt);
                    %>
                    <th><%= apptCount %></th>
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
