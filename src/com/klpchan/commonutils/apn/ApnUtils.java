package com.klpchan.commonutils.apn;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;


import com.klpchan.commonutils.LogUtil;
import com.klpchan.commonutils.simcard.SimCardUtils;
import com.samsung.android.telephony.MultiSimManager;

/**
 * Created by samsung on 2016/3/31.
 */
public class ApnUtils {
    private static Uri APN_LIST_URI = Uri.parse("content://telephony/carriers");
    private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private static final String APN_NAME = "SRoamingAPN";
    private Context context;
    private int apnID = -1;
    private static ApnUtils instance;

    @Deprecated
    public static ApnUtils getInstance(Context context) {
        if (context == null)
            return null;

        if (null == instance)
            instance = new ApnUtils(context);

        return instance;
    }

    public ApnUtils(Context context) {
        this.context = context;
    }

/*    @Deprecated
    public boolean checkIfAPNexist(){
        //2、检测一个APN是否存
        ContentResolver resolver = context.getContentResolver();
        //从apn列表中查找apn名称为myapn的apn信息
        Cursor c = resolver.query(APN_LIST_URI, new String[] { "_id", "name", "apn" }, "apn like '%myapn%'", null, null);
        if  (c != null && c.moveToNext()) {
            int id = c.getShort(c.getColumnIndex("_id")); //获取该apn的id信息
            LogUtil.d("checkIfAPNexist id is " + id);
            return true;
        } else {
            return false;
        }
    }*/

/*    public boolean isExistApn(String apn,String mcc,String mnc) {
        LogUtil.d ("isExistApn: apn = " + apn);
        if (null != apn) {
            LogUtil.d( "isExistApn: apn = " + apn.toString());
        } else {
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        Cursor qc = resolver.query(APN_LIST_URI, new String[]{"name", "apn", "mcc", "mnc"}, "name =? and apn = ? and mcc =? and mnc = ? ",
                new String[]{APN_NAME, apn, mcc, mnc}, null);

        if (qc != null) {
            LogUtil.d( "isExistApn: count = " + qc.getCount());
        } else {
            return false;
        }
        return qc.getCount() != 0;
    }*/

    protected String getSIMInfo(int slotID) {
        String result = MultiSimManager.getSubscriberId(slotID);
        if (result != null && result.length() >= 5){
            result = result.substring(0,5);
        }
        return result;
    }

    public int addApn(int slotID) {
    	LogUtil.d("addApn : slotID is " + slotID + " soft sim apn is YO");
    	
        String NUMERIC = getSIMInfo(slotID);
        LogUtil.d("addApn : NUMERIC is " + NUMERIC);

        if (NUMERIC == null || NUMERIC.length() < 5) {
            LogUtil.e("addApn : No NUMERIC at SlotID" + slotID);
            return -1;
        }


        if (!SimCardUtils.isSlotReady(slotID)){
            LogUtil.e("addApn : SIM CARD not ready at slotID " + slotID);
            return -1;
        }

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put("name",APN_NAME); //apn name
        values.put("apn", "YO");  //apn value
        values.put("type", "default,dun");
        values.put("numeric", NUMERIC);
        if (NUMERIC!=null && NUMERIC.length() >= 5){
            values.put("mcc", NUMERIC.substring(0, 3));
            values.put("mnc", NUMERIC.substring(3, 5));
        }
        values.put("proxy", "");
        values.put("port", "");
        values.put("mmsproxy", "");
        values.put("mmsport", "");
        values.put("user", "");
        values.put("server", "");
        values.put("password", "");
        values.put("mmsc", "");
        Cursor c = null;
        try {
            Uri newRow = resolver.insert(APN_LIST_URI, values);
            if (newRow != null) {
                c = resolver.query(newRow, null, null, null, null);
                int idindex = c.getColumnIndex("_id");
                c.moveToFirst();
                apnID = c.getInt(idindex);
                LogUtil.d("addApn apnID is " + apnID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (c != null)
            c.close();
        return apnID;
    }

    public boolean setDefaultApn(int apnId) {
        LogUtil.d("setDefaultApn apnId is " + apnId);
        if (apnId <= -1){
            return false;
        }
        boolean res = false;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", apnId);
        try {
            resolver.update(PREFERRED_APN_URI, values, null, null);
            Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name", "apn" }, "_id=" + apnId, null, null);
            if (c != null) {
                res = true;
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LogUtil.d("setDefaultApn result is " + res + " apnId is " + apnId);
        return res;
    }

    public boolean deleteApn(int slotID) {
        boolean flag =true;
        Uri createUri = Uri.parse("content://telephony/carriers");
        ContentResolver contentResolver = context.getContentResolver();
        String NUMERIC = getSIMInfo(slotID);
        int num = -1;
        try {
            LogUtil.d("deleteApn : apn name is " + APN_NAME + " slotID is " + slotID + " NUMERIC is " + NUMERIC);
/*            contentResolver.delete(createUri, "name =? and apn = ? and mcc =? and mnc = ? ",
                    new String[]{APN_NAME, apn, NUMERIC.substring(0, 3), NUMERIC.substring(3, NUMERIC.length())});*/
             num = contentResolver.delete(createUri, "name =? ",new String[]{APN_NAME});
        } catch (NullPointerException e) {
            //TODO: handle exception
            e.printStackTrace();
            flag =false;
        }
        LogUtil.d("deleteApn : the number of item deleted is " + num);
        return flag;
    }
}
