package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.awt.*;
import java.util.*;
import java.util.List;

public class WeeklyScheduleAction {
    public final Color BASE_COLOR = new Color(250,250,250);         // color if no appointment
    public final String BASE_COLOR_STR;
    private ApptDAO apptDAO;

    /**
     * HeatmapData --- Contains data returned by getHeatmapForWeekOf representing
     *                 a heatmap of a week's appointment distribution.
     */
    public class HeatmapData {
        public String[][] colorMap; //[day][hour] = color to show for day at hour (earliest + hour)
        public IntPair earliestAndLatest; //(earliest, latest)
        public ArrayList<Integer> apptEntries; //sorted array of all appt entries per hour, excluding duplicates
        public int maxNumAppt = -1; //maximum #appts in an hour

        public HeatmapData(String[][] colorMap, IntPair earliestAndLatest,
                           ArrayList<Integer> apptEntries, int maxNumAppt) {
            this.colorMap = colorMap;
            this.earliestAndLatest = earliestAndLatest;
            this.apptEntries = apptEntries;
            this.maxNumAppt = maxNumAppt;
        }
    }

    public class IntPair {
        public int key;
        public int value;
        public IntPair(int key, int value) {this.key = key; this.value = value;}
    }


    public WeeklyScheduleAction (DAOFactory factory){
        apptDAO = factory.getApptDAO();
        BASE_COLOR_STR = colorToHexStr(BASE_COLOR);
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



    /**
     * get heat map data of a week's appointment distribution
     * @param date is the date to get the week of
     * @return the data of heat map
     * @throws ITrustException
     */
    public HeatmapData getHeatmapForWeekOf(Date date) throws ITrustException {
        //1. Get appointments for week of (date)
        List<ApptBean> appts = null;
        try {
            appts = apptDAO.getApptsForWeekOf(date);
        } catch (DBException e) {
            throw new ITrustException(e.getMessage());
        }

        //2. Get earliest and latest times
        IntPair earliestAndLatest = getEarliestAndLatestTime(appts);
        int earliest = earliestAndLatest.key;
        int latest = earliestAndLatest.value;

        //3. Assign appointments by hour array
        int[][] apptsByDayByHour = new int[7][latest-earliest+1];
        for(int day = 0; day < apptsByDayByHour.length; day++) { //initialize #appts to 0
            for(int hour = 0; hour < apptsByDayByHour[0].length; hour++) {
                apptsByDayByHour[day][hour] = 0;
            }
        }
        int maxNumAppts = setApptsArray(appts, apptsByDayByHour, earliest); //set appts array and get max #appts

        //4.
        ArrayList<Integer> apptEntries = getDistinctApptEntries(apptsByDayByHour);

        //5. Assign colormap
        String[][] colorMap = new String[7][latest-earliest+1]; //[#cols][#rows]
        //Assign colors
        for(int day = 0; day < colorMap.length; day++) {
            for(int hour = 0; hour < colorMap[0].length; hour++) {
                colorMap[day][hour] = colorMap(apptsByDayByHour[day][hour], maxNumAppts);
            }
        }

        return new HeatmapData(colorMap, earliestAndLatest, apptEntries, maxNumAppts);
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
     * Fill in appointments by day and hour and return the max number of appts. in an hour.
     * @param appts list of appointments
     * @param apptsByDayByHour [day][hour] = #appts in day at hour (earliest + hour)
     * @param earliest Military time-based earliest appointment hour in the week.
     * @return max number of appts. in a given hour.
     */
    private int setApptsArray(List<ApptBean> appts, int[][] apptsByDayByHour, int earliest) {
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
    private IntPair getEarliestAndLatestTime(List<ApptBean> appts) {
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
        return new IntPair(earliestHour, latestHour);
    }

    private ArrayList<Integer> getDistinctApptEntries(int[][] apptsByDayByHour) {
        HashSet<Integer> apptEntries = new HashSet<>();
        for(int day = 0; day < apptsByDayByHour.length; day++) {
            for(int hour = 0; hour < apptsByDayByHour[0].length; hour++) {
                apptEntries.add(apptsByDayByHour[day][hour]); //will not insert if present
            }
        }

        //Sort in ascending order and return
        ArrayList<Integer> sorted = new ArrayList<Integer>(apptEntries);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Map number of appointments to a color.
     * @param numAppts Number of appointments in an hour timespan.
     * @param maxNumAppts the maximum number of appointments in an hour
     * @return String representing the color mapped.
     */
    public String colorMap(int numAppts, int maxNumAppts) {
        int end = 0;
        int start = 180;
        Color clr = BASE_COLOR; //start at white
        if(maxNumAppts != 0 && numAppts != 0) {
            int val = start + numAppts * ((end - start) / maxNumAppts);
            clr = new Color(250, val, val);
        }
        return colorToHexStr(clr);
    }

    /**
     * convert the color code to hex string
     * @param clr the color
     * @return "#RRGGBB"
     */
    private String colorToHexStr(Color clr) {
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
