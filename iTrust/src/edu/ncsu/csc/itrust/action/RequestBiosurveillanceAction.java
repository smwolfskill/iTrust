package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.DiagnosisStatisticsBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public String detectEpidemic(String icdCode, String zipCode, Date date, Double threshold) throws ITrustException, FormValidationException {

        ArrayList<DiagnosisStatisticsBean> diagnosisStatisticsBean = null;
        /* Check zip code validation */
        try{
            if (!zipCode.matches("([0-9]{5})|([0-9]{5}-[0-9]{4})"))
                throw new FormValidationException("invalid zip code");
            if (Integer.valueOf(zipCode)<10000 || Integer.valueOf(zipCode)>99999)
                throw new FormValidationException("length should exactly equal to 5");
        } catch (Exception e) { return "invalid zip code"; }

        try {
            Integer.parseInt(icdCode);
        } catch (Exception e) { return "invalid diagnosis code"; }

        try {
            Date start = new Date(date.getTime() - 1000*60*60*24*14);
            diagnosisStatisticsBean = diagDAO.getWeeklyCounts(icdCode, zipCode, start, date);
        } catch (DBException e) {
            throw new ITrustException(e.getMessage());
        }

        switch (icdCode) {
            case "84.50":
                return isMalaria(diagnosisStatisticsBean, threshold)?"Yes":"No";
            case "487.00":
                return isInfluenzaEpidemic(diagnosisStatisticsBean)?"Yes":"No";
            default:
                return "No analysis can occur.";
        }
    }

    /* return the order of the week of the date from 1/1 in same year */
    private long weekNumber(Date date) {
        Date startDateOfYear = new Date(date.getYear(),1,1);
        return (date.getTime() - startDateOfYear.getTime())/(1000 * 60 * 60 * 24 * 7) + 1;
    }

    private boolean isMalaria(ArrayList<DiagnosisStatisticsBean> diagnosisStatisticsBean, double threshold) {
        return true;
    }

    private boolean isInfluenzaEpidemic(ArrayList<DiagnosisStatisticsBean> diagnosisStatisticsBean) {

        long regionCount = diagnosisStatisticsBean.get(0).getRegionStats();

        if(regionCount > calcThreshold(weekNumber(diagnosisStatisticsBean.get(0).getStartDate())))
        {
            regionCount = diagnosisStatisticsBean.get(1).getRegionStats();
            if(regionCount > calcThreshold(weekNumber(diagnosisStatisticsBean.get(1).getStartDate()))){
                return true;
            }
        }
        return false;
    }

    private double calcThreshold(double weekNumber) {
        return 5.34 + 0.271 * weekNumber + 3.45 * Math.sin(2 * Math.PI * weekNumber / 52.0)
                + 8.41 * Math.cos(2 * Math.PI * weekNumber / 52.0);
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
