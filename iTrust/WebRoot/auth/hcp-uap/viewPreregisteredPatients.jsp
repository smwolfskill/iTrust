<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@ page import="edu.ncsu.csc.itrust.beans.PatientBean" %>
<%@ page import="edu.ncsu.csc.itrust.beans.PreRegisterBean" %>
<%@ page import="edu.ncsu.csc.itrust.dao.mysql.PreRegisterDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - View Preregistered Patients";
%>

<%@include file="/header.jsp" %>

<style type="text/css" title="currentStyle">
    @import "/iTrust/DataTables/media/css/demo_table.css";
</style>

<h2>Pre-Registered Patients</h2>

<%
    PreRegisterDAO preRegisterDAO = prodDAO.getPreRegisterDAO();
%>

<form action="viewPreregisteredPatients.jsp" method="post" name="myform">
    <table class="display fTable" id="patientList" align="center">
        <thead>
            <tr class="">
                <th>Name</th>
                <th>MID</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<PreRegisterBean> preregisteredPatients = preRegisterDAO.getPreregisteredPatients();
                for (PreRegisterBean bean : preregisteredPatients) {
                    PatientBean patient = bean.getPatient();
            %>
            <tr>
                <td>
                    <a href="editPreregisteredPatient.jsp?pid=<%= StringEscapeUtils.escapeHtml("" + patient.getMID()) %>">
                        <%= StringEscapeUtils.escapeHtml("" + patient.getFullName()) %>
                    </a>
                </td>
                <td><%= StringEscapeUtils.escapeHtml("" + patient.getMID()) %></td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</form>

<%@include file="/footer.jsp" %>
