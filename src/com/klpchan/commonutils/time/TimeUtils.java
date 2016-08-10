package com.klpchan.commonutils.time;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.klpchan.commonutils.LogUtil;

/**
 * Created by klpchan on 2016/4/22.
 */
public class TimeUtils {

    @Deprecated
    public static String utc2Local(String utcTime, String utcTimePatten,
                                   String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

    /**
     * convert time format from UTC to local time
     * @param date, UTC time, format is "2016-04-20 02:48:10"
     * @return data with current local,for example, Beijing time is 2016-04-20 10:48:10
     */
    public static String getStringFromUTC(String date) {
        if (date == null || date.isEmpty()){
            return null;
        }
        final String year = date.substring(0, 4);
        final String month = date.substring(5, 7);
        final String day = date.substring(8, 10);
        final String hour = date.substring(11, 13);
        final String minute = date.substring(14, 16);
        final String second = date.substring(17, 19);
        Calendar result = new GregorianCalendar(Integer.valueOf(year),
                        Integer.valueOf(month) - 1, Integer.valueOf(day),
                        Integer.valueOf(hour), Integer.valueOf(minute),
                        Integer.valueOf(second));
        result.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Date date1 = null;

        try {
            date1 = new Date(result.getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.d("getStringFromUTC: date is " + date1);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String res = null;
        if (dateFormat != null && date1 != null){
            res = dateFormat.format(date1);
        }
        return res;
    }
    
    /**
     * show dialog to user of go into setting-> time and date
     * @param context
     * @param titleID
     * @param messageID
     * @param positiveTextID
     */
    public static void showTimeSettingDialog(final Context context,int titleID,int messageID,
    		int positiveTextID){
        LogUtil.d("showTimeSettingDialog context is " + context);
        if (context == null){
            LogUtil.e("showTimeSettingDialog context is null");
            return;
        }

        AlertDialog mAlertDialog;
        String message = context.getString(messageID);
        AlertDialog.Builder b = new AlertDialog.Builder(context);

        b.setTitle(context.getString(titleID));
        b.setMessage(message);

        b.setPositiveButton(context.getString(positiveTextID),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        goTimeSetting(context);
                        dialog.dismiss();
                    }
                });

//        b.setCancelable(false);

        mAlertDialog = b.create();
        mAlertDialog.show();
    }
    
    /**
     * go into setting->time and date
     * @param context
     */
    public static void goTimeSetting(Context context){
        if (context == null){
            LogUtil.e("goTimeSetting fail,context is null");
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.settings.DATE_SETTINGS");
        context.startActivity(intent);
    }
}
