package com.eldar.lessononeb;

/**
 * Created by EldarM on 1/22/2015.
 */
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class TestDateandTime
{
    private static class SpecialDate{
        /** Constructor that takes date label and the date as a string, e.g.
         “Graduation”, “2010/06/29 15:30:00” */
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


        public SpecialDate(String label, String dateString){
            this.label = label;

            Date date;
            try
            {
                date = format.parse(dateString);
            }catch(ParseException e) {
                //don't trash the app but better have a look at the trace
                e.printStackTrace();
                date = new Date();
            }
            cal = new GregorianCalendar();
            cal.setTime(date);
            now = new GregorianCalendar();
            ComputeAnniversary();
        }

        protected void ComputeAnniversary(){
            anniversary = new GregorianCalendar(now.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND));
        }
        /** Override toString() to make a textual representation of the date. */

        @Override

        public String toString(){
            return format.format(cal.getTime());
        }

        /** Get the data label */

        public String getLabel(){
            return label;
        }

        /** Convert into a string representing how much time have passed since the date,
         e.g. “Graduation: 2 years 13 days 4 hours and 12 minutes.” */

        public String timeSince(){
            long years = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
            long shift = anniversaryShiftSec();
            if (shift > 0){
                years--;
                shift = secInYear - shift;
            } else {
                shift = -shift;
            }
            String yearsString = years == 0 ? "" : String.format("%d years", years);
            return yearsString + " " + formatShift(shift);
        }

        /** Convert into a string representing time left to the nearest anniversary. */

        public String timeTillAniversary(){
            long shift = anniversaryShiftSec();
            //System.out.println("Shift: " + shift);
            if (shift < 0){
                shift = secInYear + shift; //Actually minus since shift <0
            }
            return formatShift(shift);
        }

        private String formatShift(long shift){
            long seconds = shift % secInMin;
            long minutes = shift / secInMin % minInHour;
            long hours = shift / (secInMin * minInHour) % hoursInDay;
            long days = shift / (secInMin * minInHour * hoursInDay) % daysInYear;
            return String.format("%d days %d hours %d minutes %d seconds",
                    days, hours, minutes, seconds);
        }

        private long anniversaryShiftSec(){
            return (anniversary.getTime().getTime() - now.getTime().getTime())/1000;
        }
    }//class SpecialDate
    private static class SpecialDateTest extends SpecialDate {

        public SpecialDateTest(String label, String dateString) {

            super(label, dateString);

        }

        public SpecialDateTest setNow(GregorianCalendar now){
            this.now = now;
            ComputeAnniversary();
            return this;
        }
    }
    private static void ExpectEqual(String label, String actual, String expected) {

        if (actual.equals(expected)) {

            System.out.println(label + ": Passed.");

        } else {

            System.out.println(label + ": Expected '" + expected + "' got '" + actual + "'");

        }

    }

    private static void Test(String testName, String dateString, String since, String till) {

        System.out.println("Testing " + testName + " '" + dateString + "'");

        // Somehow set NOW to 2014/10/09 12:00:00, we'll think how to do that later.
        GregorianCalendar now = new GregorianCalendar(2014, 9, 9, 12, 0, 0);

        SpecialDate date = new SpecialDateTest(testName, dateString).setNow(now);

        ExpectEqual(testName, date.getLabel(), testName);

        ExpectEqual(testName, date.toString(), dateString);

        ExpectEqual(testName, date.timeSince(), since);

        ExpectEqual(testName, date.timeTillAniversary(), till);

    }

    public static void main(String[] args) {

        Test("N", "2013/10/09 13:00:00", "364 days 23 hours 0 minutes 0 seconds",

                "0 days 1 hours 0 minutes 0 seconds");

        Test("G", "2010/09/27 09:00:00", "4 years 12 days 3 hours 0 minutes 0 seconds",

                "352 days 21 hours 0 minutes 0 seconds");

        Test("Z", "2012/10/01 10:00:00", "2 years 8 days 2 hours 0 minutes 0 seconds",

                "356 days 22 hours 0 minutes 0 seconds");

        Test("M", "2000/04/28 08:00:00", "14 years 164 days 4 hours 0 minutes 0 seconds",

                "200 days 20 hours 0 minutes 0 seconds");

        Test("X", "1996/12/31 23:55:00", "17 years 281 days 11 hours 5 minutes 0 seconds",

                "83 days 12 hours 55 minutes 0 seconds");

    }
}
