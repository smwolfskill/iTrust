package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import javafx.util.Pair;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyScheduleAction {
    ApptDAO apptDAO;

    //private final long MILLIS_PER_DAY = 1000*60*60*24L;

    public WeeklyScheduleAction (DAOFactory factory){
        apptDAO = factory.getApptDAO();
    }

    /**
     * Get all appointments in a given week.
     * @param date Date.
     * @return List of appointments in the week.
     * @throws ITrustException
     */
    public List<ApptBean> getApptsForWeekOf(Date date) throws ITrustException {
        try {
            List<ApptBean> appts = apptDAO.getApptsForWeekOf(date);
            return appts;
        } catch (DBException e) {
            throw new ITrustException(e.getMessage());
        }
    }

    public class HeatmapData {
        public String[][] colorMap;
        public Pair<Integer,Integer> earliestAndLatest;
        public int maxNumAppt = -1;
        public HeatmapData(String[][] colorMap, Pair<Integer,Integer> earliestAndLatest, int maxNumAppt) {
            this.colorMap = colorMap;
            this.earliestAndLatest = earliestAndLatest;
            this.maxNumAppt = maxNumAppt;
        }
    }

    public HeatmapData getHeatmapForWeekOf(Date date) throws ITrustException {
        List<ApptBean> appts = null;
        try {
            appts = apptDAO.getApptsForWeekOf(date);
        } catch (DBException e) {
            throw new ITrustException(e.getMessage());
        }
        Pair<Integer,Integer> earliestAndLatest = getEarliestAndLatestTime(appts);
        int earliest = earliestAndLatest.getKey();
        int latest = earliestAndLatest.getValue();
        int[][] apptsByDayByHour = new int[7][latest-earliest+1];
        for(int day = 0; day < apptsByDayByHour.length; day++) {
            for(int hour = 0; hour < apptsByDayByHour[0].length; hour++) {
                apptsByDayByHour[day][hour] = 0;
            }
        }
        int maxNumAppts = getMaxNumAppts(appts, apptsByDayByHour, earliest);

        String[][] colorMap = new String[7][latest-earliest+1]; //[#cols][#rows]
        //Assign colors
        for(int day = 0; day < colorMap.length; day++) {
            for(int hour = 0; hour < colorMap[0].length; hour++) {
                colorMap[day][hour] = colorMap(apptsByDayByHour[day][hour], maxNumAppts);
            }
        }

        return new HeatmapData(colorMap, earliestAndLatest, maxNumAppts);
    }

    /**
     * Convert hour of day into military time string format.
     * @param hourOfDay 0-based hour of day.
     * @return String
     */
    public String hourOfDay_toString(int hourOfDay) {
        if(hourOfDay < 10) {
            return "0" + hourOfDay + ":00";
        }
        return hourOfDay + ":00";
    }

    /**
     *
     * @param appts
     * @param apptsByDayByHour
     * @return max number of appts. in a given hour.
     */
    private int getMaxNumAppts(List<ApptBean> appts, int[][] apptsByDayByHour, int earliest) {
        Calendar cal = Calendar.getInstance();
        int max = 0;

        for(int i = 0; i < appts.size(); i++) {
            cal.setTime(appts.get(i).getDate());
            int hour = cal.get(Calendar.HOUR_OF_DAY); //0..23
            int day = cal.get(Calendar.DAY_OF_WEEK) - 1; //0..6

            apptsByDayByHour[day][hour - earliest]++;
            if(apptsByDayByHour[day][hour - earliest] > max) {
                max = apptsByDayByHour[day][hour - earliest];
            }
        }
        return max;
    }

    /**
     * Set earliest and latest hour of day there is an appointment.
     * @param appts List of appointments.
     * @return will contain (earliest, latest).
     */
    private Pair<Integer, Integer> getEarliestAndLatestTime(List<ApptBean> appts) {
        Calendar cal = Calendar.getInstance();

        int earliestHour = 23;
        int latestHour = 0;

        //Find earliest and latest hour there is an appointment
        for(ApptBean appt : appts) {
            cal.setTime(appt.getDate());
            int apptHour = cal.get(Calendar.HOUR_OF_DAY);
            if(apptHour < earliestHour) {
                earliestHour = apptHour;
            }
            if(apptHour > latestHour) {
                latestHour = apptHour;
            }
        }

        if(earliestHour > latestHour) { //8am...8pm default, but no appts
            earliestHour = 7;
            latestHour = 19;
        }
        return new Pair<Integer, Integer>(earliestHour, latestHour);
    }

    /**
     * Map number of appointments to a color.
     * @param numAppts Number of appointments in an hour timespan.
     * @return String representing the color mapped.
     */
    public String colorMap(int numAppts, int maxNumAppts) {
        int end = 0;
        int start = 180;
        Color clr = new Color(250, 250, 250); //start at white
        if(maxNumAppts != 0 && numAppts != 0) {
            //int val = 255 - (numAppts * 255 / maxNumAppts);
            int val = start + numAppts * ((end - start) / maxNumAppts);
            clr = new Color(250, val, val);
        }
        String map = "#" + Integer.toHexString(clr.getRed());
        if(clr.getGreen() < 16) {
            map += "0";
        }
        map += Integer.toHexString(clr.getGreen());
        if(clr.getBlue() < 16) {
            map += "0";
        }
        map += Integer.toHexString(clr.getBlue());
        return map;
    }
}
