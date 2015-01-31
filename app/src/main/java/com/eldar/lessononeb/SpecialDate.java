package com.eldar.lessononeb;

/**
 * Created by EldarM on 1/22/2015.
 */
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

class SpecialDate {

    private static final String LOG_TAG = SpecialDate.class.getCanonicalName();
    /**
     * Constructor that takes date label and the date as a string, e.g.
     * “Graduation”, “2010/06/29 15:30:00”
     */
    private final long secInMin = 60;
    private final long minInHour = 60;
    private final long hoursInDay = 24;
    private final long daysInYear = 365;
    private final long secInYear = secInMin * minInHour * hoursInDay * daysInYear;

    private String label;
    private GregorianCalendar cal;
    protected SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private GregorianCalendar anniversary;
    protected GregorianCalendar now;


    public SpecialDate(String label, String dateString) {
        this.label = label;

        Date date;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            //don't trash the app but better have a look at the trace
            e.printStackTrace();
            date = new Date();
        }
        cal = new GregorianCalendar();
        cal.setTime(date);
        now = new GregorianCalendar();
        ComputeAnniversary();
    }
    public static Vector<SpecialDate> readDatesList(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Vector<SpecialDate> dates = new Vector<SpecialDate>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length != 2) {
                    Log.e(LOG_TAG, "Incorrect string format: '" + line + "'");
                    continue; //Wrong Format
                }
                dates.add(new SpecialDate(parts[0], parts[1]));
            }
        } catch (IOException e) {

            Log.e(LOG_TAG, "Error reading the dates", e);
        }
        return dates;
    }

    public static void writeDatesList(OutputStream os, Vector<SpecialDate> dates){
        StringBuffer result = new StringBuffer();
        for (SpecialDate date : dates){
            result.append(date.getLabel());
            result.append(":");
            result.append(date.toString());
            result.append("\n");
        }
        try {
            os.write(result.toString().getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void ComputeAnniversary() {
        anniversary = new GregorianCalendar(now.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
    }

    /**
     * Override toString() to make a textual representation of the date.
     */

    @Override

    public String toString() {
        return format.format(cal.getTime());
    }

    /**
     * Get the data label
     */

    public String getLabel() {
        return label;
    }

    /**
     * Convert into a string representing how much time have passed since the date,
     * e.g. “Graduation: 2 years 13 days 4 hours and 12 minutes.”
     */

    public String timeSince() {
        long years = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        long shift = anniversaryShiftSec();
        if (shift > 0) {
            years--;
            shift = secInYear - shift;
        } else {
            shift = -shift;
        }
        String yearsString = years == 0 ? "" : String.format("%d years", years);
        return yearsString + ", " + formatShift(shift);
    }

    /**
     * Convert into a string representing time left to the nearest anniversary.
     */

    public String timeTillAnniversary() {
        long shift = anniversaryShiftSec();
        //System.out.println("Shift: " + shift);
        if (shift < 0) {
            shift = secInYear + shift; //Actually minus since shift <0
        }
        return formatShift(shift);
    }

    private String formatShift(long shift) {
        long seconds = shift % secInMin;
        long minutes = shift / secInMin % minInHour;
        long hours = shift / (secInMin * minInHour) % hoursInDay;
        long days = shift / (secInMin * minInHour * hoursInDay) % daysInYear;
        return String.format("%d days, %d hours, %d minutes, and %d seconds",
                days, hours, minutes, seconds);
    }

    private long anniversaryShiftSec() {
        now = new GregorianCalendar();
        return (anniversary.getTime().getTime() - now.getTime().getTime()) / 1000;
    }


}
