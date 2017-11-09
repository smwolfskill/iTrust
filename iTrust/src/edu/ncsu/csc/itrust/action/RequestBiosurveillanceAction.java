package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.exception.FormValidationException;

import java.text.SimpleDateFormat;
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

        /* Check zip code validation */
        try{
            if (!zipCode.matches("([0-9]{5})|([0-9]{5}-[0-9]{4})"))
                throw new FormValidationException("invalid zip code");
            if (Integer.valueOf(zipCode)<10000 || Integer.valueOf(zipCode)>99999)
                throw new FormValidationException("length should exactly equal to 5");
        } catch (Exception e) { return "invalid zip code"; }

        /* Check diagnosis code validation */
        try {
            if (!(icdCode.equals("84.50") || icdCode.equals("487.00")) ) {      //not sure about icd Code yet
                throw new FormValidationException("Exception");
            }
        } catch (Exception e) { return "invalid diagnosis code"; }

        return null;
    }

    /* return the order of the week of the date from 1/1 in same year */
    public long weekNumber(Date date) {
        Date startDateOfYear = new Date(date.getYear(),1,1);
        return (date.getTime() - startDateOfYear.getTime())/(1000 * 60 * 60 * 24 * 7) + 1;
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
