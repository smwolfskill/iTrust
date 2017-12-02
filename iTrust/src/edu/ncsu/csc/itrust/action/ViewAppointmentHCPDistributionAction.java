package edu.ncsu.csc.itrust.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.exception.DBException;


public class ViewAppointmentHCPDistributionAction {

    private PersonnelDAO personnelDAO;
    private ApptDAO apptDAO;

    public ViewAppointmentHCPDistributionAction(DAOFactory factory) {
        this.personnelDAO = factory.getPersonnelDAO();
        this.apptDAO = factory.getApptDAO();
    }

    /**
     *
     * @return a list of distinct specialties
     */
    public List<String> getSpecialties() throws DBException {
        return personnelDAO.getAllSpecialties();
    }

    /**
     *
     * @param startDate the beginning of the date range to search for appointments
     * @param endDate   the end of the date range to search for appointments
     * @param specialty either "all" or a specialty that serves as a filter on searching appointments
     * @return  the html link for the piechart that represents the distribution of appointments of each hcp
     */
    public String getDistribution(Date startDate, Date endDate, String specialty) throws DBException {
        if (startDate == null || endDate == null || startDate.getTime() >= endDate.getTime()) {
            return "Invalid date.";
        }
        Map<String, Integer> hcpApptsCount = apptDAO.getAppointmentCountByHCP(startDate, endDate, specialty);
        int sum = 0;
        String labels = "chl=";
        String counts = "chd=t:";
        for (Map.Entry<String, Integer> m: hcpApptsCount.entrySet()) {
            labels += m.getKey() + "|";
            counts += m.getValue() + ",";
            sum += m.getValue();
        }
        labels = labels.substring(0, labels.length() - 1);
        counts = counts.substring(0, counts.length() - 1);

        String result = "<img id=\"chart1\" width=720 src=\"https://chart.googleapis.com/chart?" +
                        "chtt=" + Integer.toString(sum) + "+Appointments+For+" + specialty + "+from+" +
                        startDate.getMonth() + "-" + startDate.getDay() + "-" + Integer.toString(startDate.getYear() + 1900) + "+to+" +
                        endDate.getMonth() + "-" + endDate.getDay() + "-" + Integer.toString(endDate.getYear() + 1900) + "&amp;" +
                        "cht=p3&amp;chs=500x200&amp;" +
                        counts + "&amp;" +
                        labels + "&amp;" +
                        "chco=E8D0A9|B7AFA3|C1DAD6|F5FAFA|ACD1E9|6D929B\">";
        return result;
    }
}
