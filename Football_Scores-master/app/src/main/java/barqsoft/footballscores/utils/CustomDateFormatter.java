package barqsoft.footballscores.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.R;

/**
 * Created by Paulina on 2015-11-25.
 */
public class CustomDateFormatter {

    private static final long DAY_IN_MILISECONDS = 86400000;

    public static String getDayName(Context context, Date date) {

        if (DateUtils.isToday(date.getTime()))
            return context.getResources().getString(R.string.today);

        if (isTomorrow(date.getTime()))
            return context.getResources().getString(R.string.tomorrow);

        if (isYesterday(date.getTime()))
            return context.getResources().getString(R.string.yesterday);


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", new Locale(context.getString(R.string.locale)));
        return dateFormat.format(date);
    }

    public static String getTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    public static String getDayNameAndTime(Context context, Date date) {
        return getDayName(context, date) +
                context.getResources().getString(R.string.day_and_hour_connective) +
                getTime(date);
    }

    private static boolean isTomorrow(long when) {
        return DateUtils.isToday(when - DAY_IN_MILISECONDS);
    }

    private static boolean isYesterday(long when) {
        return DateUtils.isToday(when + DAY_IN_MILISECONDS);
    }

}
