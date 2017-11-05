<%@taglib prefix="itrust" uri="/WEB-INF/tags.tld"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="edu.ncsu.csc.itrust.action.SendReminderAction"%>
<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - Send Reminders";
%>

<%@include file="/header.jsp" %>

<%
    boolean formIsFilled = (null != request.getParameter("formIsFilled")) && request.getParameter("formIsFilled").equals("true");

    if (formIsFilled)
    {
        try {
        int numDays = Integer.parseInt(request.getParameter("numberOfDays"));
            if(numDays > 0) {
                SendReminderAction sendReminder = new SendReminderAction(prodDAO, loggedInMID.longValue());
                sendReminder.sendReminderForAppointments(numDays);
                loggingAction.logEvent(TransactionType., loggedInMID.longValue(), 0,
                        "Sent reminders to patients with appointments within " + numDays + ".");
%>
                <div align=center>
                    <span class="iTrustMessage">Reminders sent succesfully !</span>
                    <br />
                    <br />
                </div>
<%          } else { %>
                <div align=center>
                    <span class="iTrustMessage">Provide a positive number.</span>
                    <br />
                    <br />
                </div>
            <%}
        } catch (Exception e) { %>
            <div align=center>
                <span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
            </div>
    <%  }
    }%>

<div align=center>
    <p style="width: 50%; text-align:left;"> Please enter the number of days for which the reminder has to be sent</p>

    <form action="sendReminders.jsp" method="post">
        <input type="hidden" name="formIsFilled" value="true"><br />
        <input type="text" name="numberOfDays"> <br />
        <input type="submit" style="font-size: 16pt; font-weight: bold;" value="Send Reminders">
        <br />
    </form>
</div>

<%@include file="/footer.jsp" %>
