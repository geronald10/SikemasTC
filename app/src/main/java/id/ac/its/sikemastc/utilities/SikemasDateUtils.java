package id.ac.its.sikemastc.utilities;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import id.ac.its.sikemastc.R;

/**
 * Kelas untuk handling konversi tanggal
 */
public final class SikemasDateUtils {

    public static String formatTime(String inputTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        Date time = null;
        try {
            time = inputFormat.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        Log.d("cek converted time", outputFormat.format(time));
        return outputFormat.format(time);
    }

    public static String formatDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String outputDate = null;
        try {
            if (inputDate != null) {
                Date date = inputFormat.parse(inputDate);
                SimpleDateFormat outputLocalDateIndonesian = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                outputDate = outputLocalDateIndonesian.format(date);
            }
            else
                return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }

    public static String formatDateFromTimestamp(String inputTimestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String outputDate = null;
        try {
            if (inputTimestamp != null) {
                Date date = inputFormat.parse(inputTimestamp);
                SimpleDateFormat outputLocalDateIndonesian = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                outputDate = outputLocalDateIndonesian.format(date);
            } else
                return "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }

    public static String formatTimeFromTimestamp(String inputTimestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("inputtimestamp", inputTimestamp);
        String outputTime = null;
        try {
            if (inputTimestamp != null) {
                Date time = inputFormat.parse(inputTimestamp);
                SimpleDateFormat outputLocalDateIndonesian = new SimpleDateFormat("hh:mm a", new Locale("id", "ID"));
                outputTime = outputLocalDateIndonesian.format(time);
            } else
                return "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputTime;
    }

    public static String getCurrentDate(Context context) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat localDateIndonesian = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
        return localDateIndonesian.format(calendar.getTime());
    }
}
