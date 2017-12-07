<%@taglib prefix="itrust" uri="/WEB-INF/tags.tld"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.BeanBuilder"%>
<%@ page import="edu.ncsu.csc.itrust.dao.mysql.PreRegisterDAO" %>
<%@ page import="edu.ncsu.csc.itrust.beans.PreRegisterBean" %>
<%@ page import="edu.ncsu.csc.itrust.exception.DBException" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - Edit Preregistered Patient";
%>

<%@include file="/header.jsp"%>
<%
/* Require a Patient ID first */
    String pidString = (String) session.getAttribute("pid");
    if (pidString == null || pidString.equals("") || 1 > pidString.length()) {
        pidString = request.getParameter("pid");
        if (pidString == null || pidString.equals("")) {
            out.println("pidstring is null");
            response.sendRedirect("/iTrust/auth/getPatientID.jsp?forward=hcp-uap/editPatient.jsp");
            return;
        }
    }

        /* If the patient id doesn't check out, then kick 'em out to the exception handler */
    PreRegisterDAO preRegisterDAO = prodDAO.getPreRegisterDAO();
    boolean isPreregistered = preRegisterDAO.checkPreregisteredPatient(Long.parseLong(pidString));

        /* Now take care of updating information */

    boolean formIsFilled = request.getParameter("formIsFilled") != null
            && request.getParameter("formIsFilled").equals("true");
    PreRegisterBean pr;
    PatientBean p;

    if (request.getParameter("actionType") != null) {
        if (request.getParameter("actionType").equals("activate")) {
            preRegisterDAO.activatePreregisteredPatient(Long.parseLong(pidString),loggedInMID.longValue());
            loggingAction.logEvent(TransactionType.PREREGISTERED_PATIENT_ACTIVATE, loggedInMID.longValue(), Long.parseLong(pidString), "");
%>
<br />
<div align=center>
    <span class="iTrustMessage">Patient Successfully Activated</span>
</div>
<br />
<%
            return;
        } else if (request.getParameter("actionType").equals("deactivate")) {
            preRegisterDAO.deactivatePreregisteredPatient(Long.parseLong(pidString));
            loggingAction.logEvent(TransactionType.PREREGISTERED_PATIENT_DEACTIVATE, loggedInMID.longValue(), Long.parseLong(pidString), "");
%>
<br />
<div align=center>
    <span class="iTrustMessage">Patient Successfully Deactivated</span>
</div>
<br />
<%
            return;
        }
    }

    if (formIsFilled) {
        p = new BeanBuilder<PatientBean>().build(request
                .getParameterMap(), new PatientBean());
        p.setMID(Long.parseLong(pidString));
        pr = new PreRegisterBean();
        pr.setPatient(p);
        pr.setMid(Long.parseLong(pidString));

        if(request.getParameter("heightStr") != "")
            pr.setHeight(request.getParameter("heightStr"));

        if(request.getParameter("heightStr") != "")
            pr.setWeight(request.getParameter("weightStr"));
        pr.setSmoker(request.getParameter("smokerStr"));
        try {
            preRegisterDAO.editPreregisteredPatient(pr, Long.parseLong(pidString));
            loggingAction.logEvent(TransactionType.DEMOGRAPHICS_EDIT, loggedInMID.longValue(), p.getMID(), "");


%>
<br />
<div align=center>
    <span class="iTrustMessage">Information Successfully Updated</span>
</div>
<br />
<%
} catch (DBException e) {
%>
<br />
<div align=center>
    <span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
</div>
<br />
<%
        }
    } else {
        pr = preRegisterDAO.getPreregisteredPatient(Long.parseLong(pidString));
        p = pr.getPatient();
        loggingAction.logEvent(TransactionType.DEMOGRAPHICS_VIEW, loggedInMID.longValue(), p.getMID(), "");
    }

%>

<form id="editForm" action="editPreregisteredPatient.jsp?pid=<%= StringEscapeUtils.escapeHtml(pidString) %>" method="post"><input type="hidden"
                                                                  name="formIsFilled" value="true"> <br />
    <table cellspacing=0 align=center cellpadding=0>
        <tr>
            <td valign=top>
                <table class="fTable" align=center style="width: 350px;">
                    <tr>
                        <th colspan=2>Patient Information</th>
                    </tr>
                    <tr>

                        <td class="subHeaderVertical">First Name:</td>
                        <td><input name="firstName" value="<%= StringEscapeUtils.escapeHtml("" + (p.getFirstName())) %>" type="text"></td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Last Name:</td>
                        <td><input name="lastName" value="<%= StringEscapeUtils.escapeHtml("" + (p.getLastName())) %>" type="text"></td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Email:</td>
                        <td><input name="email" value="<%= StringEscapeUtils.escapeHtml("" + (p.getEmail())) %>" type="text"></td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Address:</td>
                        <td><input name="streetAddress1"
                                   value="<%= StringEscapeUtils.escapeHtml("" + (p.getStreetAddress1())) %>" type="text"><br />
                            <input name="streetAddress2" value="<%= StringEscapeUtils.escapeHtml("" + (p.getStreetAddress2())) %>" type="text"></td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">City:</td>
                        <td><input name="city" value="<%= StringEscapeUtils.escapeHtml("" + (p.getCity())) %>" type="text">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">State:</td>
                        <td><itrust:state name="state" value="<%= StringEscapeUtils.escapeHtml(p.getState()) %>" /></td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Zip:</td>
                        <td>
                            <input name="zip" value="<%= StringEscapeUtils.escapeHtml("" + (p.getZip())) %>" maxlength="10" type="text" size="10">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Phone:</td>
                        <td>
                            <input name="phone" value="<%= StringEscapeUtils.escapeHtml("" + (p.getPhone())) %>" type="text" size="12" maxlength="12">
                    </tr>
                </table>
                <br />
                <table class="fTable" align=center style="width: 350px;">
                    <tr>
                        <th colspan=2>Insurance Information</th>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Name:</td>
                        <td><input name="icName" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcName())) %>" type="text">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Address:</td>
                        <td><input name="icAddress1" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcAddress1())) %>"
                                   type="text"><br />
                            <input name="icAddress2" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcAddress2())) %>" type="text">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">City:</td>
                        <td><input name="icCity" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcCity())) %>" type="text">
                        </td>
                    </tr>

                    <tr>
                        <td class="subHeaderVertical">State:</td>
                        <td><itrust:state name="icState" value="<%= StringEscapeUtils.escapeHtml(p.getIcState()) %>" />
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Zip:</td>
                        <td>
                            <input name="icZip" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcZip())) %>" maxlength="10" type="text" size="10">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Phone:</td>
                        <td>
                            <input name="icPhone" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcPhone())) %>" type="text" size="12" maxlength="12">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Insurance ID:</td>
                        <td><input name="icID" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcID())) %>" type="text">
                        </td>
                    </tr>
                </table>
            </td>
            <td width="15px">&nbsp;</td>
            <td valign=top>
                <table class="fTable" align=center style="width: 350px;">
                    <tr>
                        <th colspan=2>Health Information</th>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Height:</td>
                        <td><input type=text name="heightStr" value="<%= StringEscapeUtils.escapeHtml("" + (pr.getHeight() != null ? pr.getHeight() : "")) %>">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Weight:</td>
                        <td><input type=text name="weightStr" value="<%= StringEscapeUtils.escapeHtml("" + (pr.getWeight() != null ? pr.getWeight() : "")) %>">
                        </td>
                    </tr>
                    <tr>
                        <td class="subHeaderVertical">Smoker:</td>
                        <td><input type=text name="smokerStr" value="<%= StringEscapeUtils.escapeHtml("" + (pr.getSmoker())) %>">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <br />
    <div align=center>
        <% if(p.getDateOfDeactivationStr().equals("")){ %>
        <input type="submit" name="action" style="font-size: 16pt; font-weight: bold;" value="Edit Patient Record">
        <% } else { %>
        <span style="font-size: 16pt; font-weight: bold;">Patient is deactivated.  Cannot edit.</span>
        <% } %>
        <br /><br />
        <input type="hidden" id="actionType" name="actionType" value="">
        <input type="submit" id="activateBtn" name="actionButton" value="Activate Patient" onClick="document.getElementById('actionType').value='activate'">
        <input type="submit" id="deactivateBtn" name="actionButton" value="Deactivate Patient" onClick="document.getElementById('actionType').value='deactivate'">
        <span style="font-size: 14px;">
		Note: in order to set the password for this user, use the "Reset Password" link at the login page.
	</span>
    </div>
</form>
<br />
<br />

<%@include file="/footer.jsp"%>
