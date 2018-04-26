package com.activiti.android.ui.utils;

/**
 * Created by Bogdan Roatis on 4/26/2018.
 *
 */
public class TimeUtils {

    public static String DEFAULT_DATE_FORMAT = "dd-MM-yyyy HH : mm";

    /**
     * Method for correcting date format that comes from the server and contains uppercase year and day
     * @param date The date format to be corrected
     * @return corrected date format
     */
    public static String normalizeDate(String date) {
        String wrongDayFormat = "DD";
        String wrongYearFormat = "YYYY";
        return date.replace(wrongDayFormat, wrongDayFormat.toLowerCase()).replace(wrongYearFormat, wrongYearFormat.toLowerCase());
    }
}
