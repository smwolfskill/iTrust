package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;

import java.util.Date;

public class RequestBiosurveillanceAction {
    DiagnosesDAO diagDAO;

    public RequestBiosurveillanceAction (DAOFactory factory){
        diagDAO = factory.getDiagnosesDAO();
    }

    /*
    * return: "Yes", "No", "No analysis can occur.", "invalid diagnosis code", "invalid zip code", or "invalid date"
    * icdCode: the diagnose code that the user typed in
    * zipCode: the zip code that the user typed in
    * date: the date the user typed in
    */
    public String detectEpidemic(String icdCode, String zipCode, Date date) {
        return null;
    }

    /*
    * return: html code for the bar chart
    * icdCode: the diagnose code that the user typed in
    * zipCode: the zip code that the user typed in
    * date: the date the user typed in
    */
    public String seeTrends(String icdCode, String zipCode, Date date) {
        return null;
    }
}
