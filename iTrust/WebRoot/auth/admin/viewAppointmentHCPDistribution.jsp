<%@page errorPage="/auth/exceptionHandler.jsp" %>
        <%@ page import="java.util.Date" %>
        <%@ page import="java.text.SimpleDateFormat" %>
        <%@ page import="edu.ncsu.csc.itrust.action.ViewAppointmentHCPDistributionAction" %>
        <%@ page import="java.util.List" %>

        <%@include file="/global.jsp"%>

        <%
            pageTitle = "iTrust - View Appointment Distribution among HCPs";
        %>

        <%@include file="/header.jsp"%>

        <h1>View Appointment Distribution among HCPs</h1>
        <br />


        <%

            ViewAppointmentHCPDistributionAction action = new ViewAppointmentHCPDistributionAction(DAOFactory.getProductionInstance());

            String startDateString = "";
            Date startDate;

            String endDateString = "";
            Date endDate;

            try {
                startDateString = request.getParameter("startDate");
                startDateString = (startDateString == null) ? "" : startDateString;
                startDate = ("".equals(startDateString)) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(startDateString));
            } catch (Exception e) {
                startDate = null;
            }

            try {
                endDateString = request.getParameter("endDate");
                endDateString = (endDateString == null) ? "" : endDateString;
                endDate = ("".equals(endDateString)) ? null : (new SimpleDateFormat("MM/dd/yyyy").parse(endDateString));
            } catch (Exception e) {
                endDate = null;
            }

            String specialtySelected = request.getParameter("specialty");
        %>

        <form action="viewAppointmentHCPDistribution.jsp" id="viewAppointmentHCPDistributionform" method="post">
            <div align=center>
                <table class="fTable" align="center">

                    <tr>
                        <td>
                            <select name="specialty" id="specialtySelection" selected = "<%= StringEscapeUtils.escapeHtml("" + (specialtySelected)) %>">
                                <option value="all"> All</option>
                                <%
                                    List<String> specialties = action.getSpecialties();
                                    for( String s: specialties) {
                                %>
                                <option value="<%= s %>" <%if(s.equals(specialtySelected)){%>selected<%}%> > <%= s %> </option>
                                <%
                                    }
                                %>

                            </select>
                        </td>
                        <td> Start Date: </td>
                        <td>
                            <input name="startDate" value="<%= startDateString %>" size="10">
                            <input type=button value="Select Start Date" onclick="displayDatePicker('startDate');">
                        </td>
                        <td> End Date: </td>
                        <td>
                            <input name="endDate" value="<%= endDateString %>" size="10">
                            <input type=button value="Select End Date" onclick="displayDatePicker('endDate');">
                        </td>
                    </tr>
                </table>
                <br/>
                <input type="submit" name="seeDistribution" value="See Distribution">
            </div>
        </form>
        <br/>
        <table class="fTable" align='center'>

            <%
                if( request.getParameter("seeDistribution") != null ) {
            %>

            <tr>
                <td ><div><%= (action.getDistribution(startDate, endDate, specialtySelected)) %></div></td>
            </tr>

            <%
                }
            %>

        </table>

        <%@include file="/footer.jsp"%>

