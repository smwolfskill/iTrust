package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.DiagnosisStatisticsBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.util.ArrayList;
import java.util.Date;

public class RequestBiosurveillanceAction {
    DiagnosesDAO diagDAO;
    private final long DAY = 1000*60*60*24L;

    public RequestBiosurveillanceAction (DAOFactory factory){
        diagDAO = factory.getDiagnosesDAO();
    }

    /**
     * Detect an epidemic.
     * @param icdCode Code of the epidemic.
     * @param zipCode zip code to consider local region/area of.
     * @param date Date to check for epidemic within two weeks prior to.
     * @param threshold If applicable, proportion to apply: 1.0 is normal, 0.5 is twice as likely to report epidemic.
     * @return "Yes", "No", "No analysis can occur.", "invalid diagnosis code", "invalid zip code", or "invalid date"
     * @throws ITrustException
     * @throws FormValidationException
     */
    public String detectEpidemic(String icdCode, String zipCode, Date date, Double threshold) throws ITrustException, FormValidationException {

        ArrayList<DiagnosisStatisticsBean> diagnosisStatisticsBean = null;
        /* Check zip code validation */
        try{
            if (!zipCode.matches("([0-9]{5})|([0-9]{5}-[0-9]{4})"))
                throw new FormValidationException("invalid zip code");
            if (Integer.valueOf(zipCode)<10000 || Integer.valueOf(zipCode)>99999)
                throw new FormValidationException("zip code length should be exactly equal to 5");
        } catch (FormValidationException e) { return "invalid zip code"; }

         /* Check icdCode validation */
        try {
            if(icdCode.contains("."))
                Double.parseDouble(icdCode);
            else
                Integer.parseInt(icdCode);
        } catch (Exception e) { return "invalid diagnosis code"; }

        try {
            Date start = new Date(date.getTime() - 14*DAY);
            diagnosisStatisticsBean = diagDAO.getWeeklyCounts(icdCode, zipCode, start, date);
        } catch (DBException e) {
            throw new ITrustException(e.getMessage());
        }

        if(icdCode.length() >= 3 && icdCode.substring(0, 3).equals("84.")) {
            return isMalariaEpidemic(diagnosisStatisticsBean, threshold) ? "Yes" : "No";
        }
        else if(icdCode.length() >= 4 && icdCode.substring(0, 4).equals("487.")) {
            return isInfluenzaEpidemic(diagnosisStatisticsBean) ? "Yes" : "No";
        }
        return "No analysis can occur";
    }

    /**
     * Determine the number of weeks that have passed since Jan. 1st until (date).
     * @param date
     * @return Week of the year.
     */
    public long weekNumber(Date date) {
        Date startDateOfYear = new Date(date.getYear(),0,1);
        return (date.getTime() - startDateOfYear.getTime())/(7*DAY) + 1;
    }

    /**
     * Determine if there is a Malaria epidemic.
     * @param diagnosisStatistics List of DiagnosisStatisticsBeans.
     * @param threshold Threshold proportion: i.e. 1.0 is normal, 0.5 is twice as likely to report epidemic.
     * @return True if there are at least 2 consecutive weeks of Malaria count over the average * threshold.
     * @throws DBException
     */
    private boolean isMalariaEpidemic(ArrayList<DiagnosisStatisticsBean> diagnosisStatistics, double threshold) throws DBException {
        if (diagnosisStatistics.size()<2)
            return false;

        Date earliest = diagDAO.findEarliestIncident("84.");
        ArrayList<Double> averages;
        if(earliest != null) {
            double yearCount = 0;
            ArrayList<Long> malariaSums = new ArrayList<>(0);
            for(int i = 0; i < diagnosisStatistics.size(); i++) { //init
                malariaSums.add(i, 0L);
            }

            if (!isEarlierMonth(earliest, diagnosisStatistics.get(0).getStartDate())) {
                //If month of earliest case is after month interested in, start averages with next year.
                earliest.setYear(earliest.getYear() + 1);
            }

            // Get averages of all weeks interested in for all years prior to the current year.
            Date startDate = new Date(diagnosisStatistics.get(0).getStartDate().getTime());
            startDate.setYear(earliest.getYear());
            Date endDate = new Date(diagnosisStatistics.get(1).getEndDate().getTime());
            endDate.setYear(earliest.getYear());

            while (startDate.before(diagnosisStatistics.get(0).getStartDate())) {
                ArrayList<DiagnosisStatisticsBean> yearResult = diagDAO.getWeeklyCounts("84.50", diagnosisStatistics.get(0).getZipCode(), startDate, endDate);
                startDate.setYear(startDate.getYear() + 1);
                endDate.setYear(endDate.getYear() + 1);

                yearCount++;
                for (int i = 0; i < yearResult.size(); i++) {
                    malariaSums.set(i, malariaSums.get(i) + yearResult.get(i).getRegionStats());
                }
            }
            if(yearCount == 0) { // date we're checking is before first case of malaria, so can't be epidemic
                return false;
            }

            // Calculate weekly averages
            averages = new ArrayList<>(0);
            for(int i = 0; i < malariaSums.size(); i++) {
                averages.add(i, malariaSums.get(i) / yearCount);
            }
        } else { // no cases on record
            return false;
        }

        //Finally, determine if there are 2 consecutive weeks over average, taking threshold into account.
        boolean lastWeekOverThreshold = false;
        for(int i = 0; i < diagnosisStatistics.size(); i++) {
            if(diagnosisStatistics.get(i).getRegionStats() > threshold * averages.get(i)) {
                if(lastWeekOverThreshold) return true; //prev. week was over threshold - 2 consecutive weeks
                lastWeekOverThreshold = true;
            }
        }

        return false;
    }

    /**
     * Determine if (first) date is earlier or in the same week of the year as (second).
     * @param first Date.
     * @param second Date.
     * @return True if so.
     */
    private boolean isEarlierMonth (Date first, Date second) {
        return weekNumber(first) <= weekNumber(second);
    }

    /**
     * Determine if there is an Influenza epidemic.
     * @param diagnosisStatistics List of DiagnosisStatisticsBeans.
     * @return True if there are at least 2 consecutive weeks of Influenza count over the threshold.
     */
    private boolean isInfluenzaEpidemic(ArrayList<DiagnosisStatisticsBean> diagnosisStatistics) {
        if (diagnosisStatistics.size()<2)
            return false;

        boolean lastWeekOverThreshold = false;
        for(int i = 0; i < diagnosisStatistics.size(); i++) {
            double weekThreshold = calcThreshold(weekNumber(diagnosisStatistics.get(0).getStartDate()));
            if(diagnosisStatistics.get(i).getRegionStats() > weekThreshold) {
                if(lastWeekOverThreshold) return true; //prev. week was over threshold - 2 consecutive weeks
                lastWeekOverThreshold = true;
            }
        }

        return false;
    }

    /**
     * Determine the Influenza epidemic threshold.
     * @param weekNumber A given week of the year.
     * @return Threshold.
     */
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
