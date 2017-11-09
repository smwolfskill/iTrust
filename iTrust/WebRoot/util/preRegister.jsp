<%@page import="edu.ncsu.csc.itrust.action.AddPatientAction"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.exception.ITrustException"%>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - Pre-Register";
%>

<%@include file="/header.jsp"%>

<h1>Pre-Register</h1>
<%
    AddPatientAction action = new AddPatientAction(prodDAO, prodDAO.getPersonnelDAO().searchForPersonnelWithName("Shape", "Shifter").get(0).getMID());

    /*long mid = action.checkMID(request.getParameter("mid"));
    String role = null;

    try {
        role = action.checkRole(mid, request.getParameter("role"));
    } catch (ITrustException e) {
    }*/
%>

<form action="/iTrust/util/preRegister.jsp" method="post">
    <table>
        <tr>
            <td colspan=2><b>Please enter your information</b></td>
        </tr>
        <tr>
            <td>Name:</td>
            <td>
                <input type=TEXT name="name" required>
            </td>
        </tr>
        <tr>
            <td>Email:</td>
            <td>
                <input type="email" name="email" required>
            </td>
        </tr>
        <tr>
            <td>Password:</td>
            <td>
                <input type="password" maxlength="20" name="password" required>
            </td>
        </tr>
        <tr>
            <td>Confirm Password:</td>
            <td>
                <input type="password" maxlength="20" name="confirmPassword" required>
            </td>
        </tr>
        <tr>
            <td>Address:</td>
            <td>
                <input type="text" name="address">
            </td>
        </tr>
        <tr>
            <td>Phone:</td>
            <td>
                <input type="tel" name="phone">
            </td>
        </tr>
        <tr>
            <td>Insurance Information</td>
            <td><hr></td>
        </tr>
        <tr>
            <td>Provider Name:</td>
            <td>
                <input type="text" name="insuranceName">
            </td>
        </tr>
        <tr>
            <td>Address:</td>
            <td>
                <input type="text" name="insuranceAddress">
            </td>
        </tr>
        <tr>
            <td>Phone:</td>
            <td>
                <input type="tel" name="insurancePhone">
            </td>
        </tr>
        <tr>
            <td>Health Information</td>
            <td><hr></td>
        </tr>
        <tr>
            <td>Height:</td>
            <td>
                <input type="text" name="height">
            </td>
        </tr>
        <tr>
            <td>Weight:</td>
            <td>
                <input type="text" name="weight">
            </td>
        </tr>
        <tr>
            <td>Smoker:</td>
            <td>
                <input type="checkbox" name="smoker">
            </td>
        </tr>
        <tr>
            <td colspan=2 align=center>
                <input type="submit" value="Submit">
            </td>
        </tr>
    </table>
</form>

<%@include file="/footer.jsp" %>

