package com.ss.commonutils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.samsung.android.telephony.MultiSimManager;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SRoaming {
    public static boolean judgeIfEasyMode(Context context){
        int dbvalue  = Settings.System.getInt(context.getContentResolver(), "easy_mode_switch", 1);
        return dbvalue == 0 ? true :false;
    }
    
    /**
     * Descriptions: force set network of specified sim slot to 3G/2G because 263 does not support 4G
     * @param context
     * @param slot, usually 0 or 1, maybe more, depend on phone count
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public void switchToNetworkType(Context context, int slot, int targetNetworktype) {
        if(context != null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (slot >= 0 && slot < MultiSimManager.getSimSlotCount()) {
                Class ownerClass = tm.getClass();

                //set parameters
                Class[] argsClass = new Class[2];
                argsClass[0] = int.class;
                argsClass[1] = int.class;

                Object[] params = new Object[2];
                params[0] = MultiSimManager.getSubscriptionId(slot)[0];
                params[1] = targetNetworktype;//RILConstants.NETWORK_MODE_WCDMA_PREF;

                try {
                    Method method = ownerClass.getMethod("setPreferredNetworkType", argsClass);
                    try {
                        Log.d("GlobalRoaming", "switchToNetwork3G by reflection, slot : " + slot + "  subscription id : " + MultiSimManager.getSubscriptionId(slot)[0]);
                        method.invoke(tm, params);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("GlobalRoaming", "switchToNetwork3G, invalid context or slot id : " + slot);
            }
        }
    }
}
