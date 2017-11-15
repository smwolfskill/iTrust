package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.beans.DiagnosisStatisticsBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.DiagnosesDAO;
import edu.ncsu.csc.itrust.dao.mysql.ICDCodesDAO;
import edu.ncsu.csc.itrust.exception.DBException;

import java.util.*;

import static java.lang.Math.toIntExact;

public class RequestBiosurveillanceAction {
    DiagnosesDAO diagDAO;
    ICDCodesDAO icdDAO;

    public RequestBiosurveillanceAction (DAOFactory factory){
        diagDAO = factory.getDiagnosesDAO();
        icdDAO = factory.getICDCodesDAO();
    }

    /*
    * return: "Yes", "No", "No analysis can occur.", "invalid diagnosis code", "invalid zip code", or "invalid date"
    * icdCode: the diagnose code that the user typed in
    * zipCode: the zip code that the user typed in
    * date: the date the user typed in
    */
    public String detectEpidemic(String icdCode, String zipCode, Date date, Double threshold) {
        return null;
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
