package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.DiagnosisStatisticsBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.util.ArrayList;
import java.util.Date;
import static java.lang.Math.toIntExact;

public class RequestBiosurveillanceAction {
    DiagnosesDAO diagDAO;
    ICDCodesDAO icdDAO;
    private final long MILLIS_PER_DAY = 1000*60*60*24L;

    public RequestBiosurveillanceAction (DAOFactory factory){
        diagDAO = factory.getDiagnosesDAO();
        icdDAO = factory.getICDCodesDAO();
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
    public String detectEpidemic(String icdCode, String zipCode, Date date, Double threshold) throws ITrustException, IllegalArgumentException {
        if(threshold == null || threshold < 0.0) {
            throw new IllegalArgumentException("threshold should exist and be non-negative.");
        }
        ArrayList<DiagnosisStatisticsBean> diagnosisStatisticsBean = null;
        /* Check zip code validation */
        try{
            if (!zipCode.matches("([0-9]{5})|([0-9]{5}-[0-9]{4})"))
                throw new FormValidationException("invalid zip code");
        } catch (FormValidationException e) {
            return "invalid zip code";
        }

         /* Check icdCode validation */
        try {
            Double.parseDouble(icdCode);
        } catch (Exception e) { return "invalid diagnosis code"; }

        try {
            Date start = new Date(date.getTime() - 14* MILLIS_PER_DAY);
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
        return (date.getTime() - startDateOfYear.getTime())/(7* MILLIS_PER_DAY) + 1;
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
    * return: html code for the bar chart, if n
    * icdCode: the diagnose code that the user typed in
    * zipCode: the zip code that the user typed in
    * date: the date the user typed in, null if it is in a wrong format
    */
    public String seeTrends(String icdCode, String zipCode, Date date) throws DBException {
        DiagnosisBean diag = icdDAO.getICDCode(icdCode);
        if (diag == null) {
            return "Invalid diagnosis code. Please try again!";
        }
        try {
            if (zipCode == null || 10000 > Integer.valueOf(zipCode) || 99999 < Integer.valueOf(zipCode)) {
                return "Invalid zip code. Please try again!";
            }
        } catch(NumberFormatException e) {
            return "Invalid zip code. Please try again!";
        }
        if (date == null) {
            return "Invalid date. Please try again!";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        Date upper = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -49);
        Date lower = cal.getTime();
        List<DiagnosisStatisticsBean> regionDiagBeans = diagDAO.getWeeklyCounts(icdCode, zipCode, lower, upper);
        List<DiagnosisStatisticsBean> stateDiagBeans = diagDAO.getWeeklyCounts(icdCode, zipCode.substring(0, 2) + "%%%", lower, upper);
        List<DiagnosisStatisticsBean> allDiagBeans = diagDAO.getWeeklyCounts(icdCode, "%%%%%", lower, upper);

        //get maximum of allData
        List<Integer> allData = new ArrayList<>();
        for (DiagnosisStatisticsBean b : allDiagBeans) {
            allData.add(toIntExact(b.getRegionStats()));
        }
        int wholeMax = Collections.max(allData);
        if (wholeMax == 0) wholeMax = 1;

        //Preparing the html label and data
        String wholeMaxString = String.valueOf(wholeMax);
        String regionDataString = "";
        String stateDataString = "";
        String allDataString = "";
        for (int i = 0; i < 8; i++) {
            regionDataString += formatGraphData(toIntExact(regionDiagBeans.get(i).getRegionStats()), wholeMax) + ",";
            stateDataString += formatGraphData(toIntExact(stateDiagBeans.get(i).getRegionStats()), wholeMax) + ",";
            allDataString += formatGraphData(allData.get(i), wholeMax) + ",";
        }
        regionDataString = regionDataString.substring(0, regionDataString.length() - 1);
        stateDataString = stateDataString.substring(0, stateDataString.length() - 1);
        allDataString = allDataString.substring(0, allDataString.length() - 1);

        String result = "<img id=\"diagchart\" src=\"https://chart.googleapis.com/chart?cht=bvg" +
                "&amp;chs=480x320" +
                "&amp;chd=t:" +
                regionDataString +
                "|" +
                stateDataString +
                "|" +
                allDataString +
                "&amp;chxr=1,0," +
                wholeMaxString +
                "&amp;chco=4D89F9,37FF92,F98602" +
                "&amp;chdl=Region|State|All" +
                "&amp;chbh=10,2,10" +
                "&amp;chxt=x,y" +
                "&amp;chxl=0:|Week+1|Week+2|Week+3|Week+4|Week+5|Week+6|Week+7|Week+8" +
                "&amp;chtt=Diagnoses+by+Week\">";
        return result;
    }

    private String formatGraphData(int num, int max) {
        return String.valueOf(num * 100 / max);
    }
}