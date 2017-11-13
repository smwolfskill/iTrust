<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.exception.ITrustException"%>
<%@ page import="edu.ncsu.csc.itrust.beans.PatientBean" %>
<%@ page import="edu.ncsu.csc.itrust.action.*" %>
<%@ page import="edu.ncsu.csc.itrust.validate.PatientValidator" %>
<%@ page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO" %>

<%@include file="/global.jsp"%>

<%
    pageTitle = "iTrust - Pre-Register";
%>

<%@include file="/header.jsp"%>

<h1>Pre-Register</h1>
<%
    PatientDAO patientDAO = prodDAO.getPatientDAO();
    long adminMID = prodDAO.getPersonnelDAO().searchForPersonnelWithName("Shape", "Shifter").get(0).getMID();
%>

<form action="/iTrust/util/preRegister.jsp" method="post">
    <table>
        <tr>
            <td colspan=2><b>Please enter your information</b></td>
        </tr>
        <tr>
            <td>First Name:</td>
            <td>
                <input type=TEXT name="firstName" required>
            </td>
        </tr>
        <tr>
            <td>Last Name:</td>
            <td>
                <input type=TEXT name="lastName" required>
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
            <td>Contact Information</td>
            <td><hr></td>
        </tr>
        <tr>
            <td>Street Address 1:</td>
            <td>
                <input type="text" name="address1">
            </td>
        </tr>
        <tr>
            <td>Street Address 2:</td>
            <td>
                <input type="text" name="address2">
            </td>
        </tr>
        <tr>
            <td>City:</td>
            <td>
                <input type="text" name="city">
            </td>
        </tr>
        <tr>
            <td>State:</td>
            <td>
                <input type="text" name="state">
            </td>
        </tr>
        <tr>
            <td>ZIP Code:</td>
            <td>
                <input type="text" name="zip">
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
                <input type="text" name="icName">
            </td>
        </tr>
        <tr>
            <td>Street Address 1:</td>
            <td>
                <input type="text" name="icAddress1">
            </td>
        </tr>
        <tr>
            <td>Street Address 2:</td>
            <td>
                <input type="text" name="icAddress2">
            </td>
        </tr>
        <tr>
            <td>City:</td>
            <td>
                <input type="text" name="icCity">
            </td>
        </tr>
        <tr>
            <td>State:</td>
            <td>
                <input type="text" name="icState">
            </td>
        </tr>
        <tr>
            <td>ZIP Code:</td>
            <td>
                <input type="text" name="icZip">
            </td>
        </tr>
        <tr>
            <td>Phone:</td>
            <td>
                <input type="tel" name="icPhone">
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

        <%
            String returnMessage = "";
            String color = "green";

            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String pass = request.getParameter("password");
            String confirmPass = request.getParameter("confirmPassword");

            String address1 = request.getParameter("address1");
            String address2 = request.getParameter("address2");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String zip = request.getParameter("zip");
            String phone = request.getParameter("phone");

            String icName = request.getParameter("icName");
            String icAddress1 = request.getParameter("icAddress1");
            String icAddress2 = request.getParameter("icAddress2");
            String icCity = request.getParameter("icCity");
            String icState = request.getParameter("icState");
            String icZip = request.getParameter("icZip");
            String icPhone = request.getParameter("icPhone");

            String height = request.getParameter("height");
            String weight = request.getParameter("weight");
            String[] smoker = request.getParameterValues("smoker");

            try {
                if (email != null && patientDAO.searchForPatientsWithEmail(email).size() > 0) {
                    returnMessage = "Email already in use.";
                    color = "red";
                } else if (pass != null && !pass.equals("") && pass.equals(confirmPass)) {
                    PatientBean p = new PatientBean();

                    p.setFirstName(firstName);
                    p.setLastName(lastName);
                    p.setEmail(email);
                    p.setPassword(pass);
                    p.setConfirmPassword(confirmPass);

                    if (!address1.equals("")) {
                        p.setStreetAddress1(address1);
                    }
                    if (!address2.equals("")) {
                        p.setStreetAddress2(address2);
                    }
                    if (!city.equals("")) {
                        p.setCity(city);
                    }
                    if (!state.equals("")) {
                        p.setState(state);
                    }
                    if (!zip.equals("")) {
                        p.setZip(zip);
                    }
                    if (!phone.equals("")) {
                        p.setPhone(phone);
                    }

                    if (!icName.equals("")) {
                        p.setIcName(icName);
                    }
                    if (!icAddress1.equals("")) {
                        p.setIcAddress1(icAddress1);
                    }
                    if (!icAddress2.equals("")) {
                        p.setIcAddress2(icAddress2);
                    }
                    if (!icCity.equals("")) {
                        p.setIcCity(icCity);
                    }
                    if (!icState.equals("")) {
                        p.setIcState(icState);
                    }
                    if (!icZip.equals("")) {
                        p.setIcZip(icZip);
                    }
                    if (!icPhone.equals("")) {
                        p.setIcPhone(icPhone);
                    }

                    // Validate the form data
                    new PatientValidator().validate(p);
                    long pid = new AddPatientAction(prodDAO, adminMID).addPreRegisteredPatient(p, height, weight, smoker == null ? "0" : "1");
                    returnMessage = "Account pre-registered. Your MID is " + pid + ".";
                    loggingAction.logEvent(TransactionType.PATIENT_CREATE, pid, 0, "");
                } else if (pass != null && !pass.equals("") && !confirmPass.equals("")) {
                    returnMessage = "Password does not match.";
                    color = "red";
                }
        %>
        <span style="color: <%=color%>"><b><i id="returnMessage"><%= returnMessage %></i></b></span><br />
        <%
            } catch (FormValidationException e) {
                e.printHTML(pageContext.getOut());
            }
        %>
    </table>
</form>

<%@include file="/footer.jsp" %>

