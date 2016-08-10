package com.klpchan.commonutils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.klpchan.commonutils.LogUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

//import com.android.internal.telephony.RILConstants;

/**
 * Created by jinwook2.kim on 2015-12-15.
 */
public class NetworkUtil {
    private Context mContext;
    private boolean isFinish = true;
    private InetworkResponse response;
    private Handler mMainHandler;
    
    public static final String NETWORK_TYPE_WIFI       = "wifi";
    public static final String NETWORK_TYPE_3G         = "eg";
    public static final String NETWORK_TYPE_2G         = "2g";
    public static final String NETWORK_TYPE_WAP        = "wap";
    public static final String NETWORK_TYPE_UNKNOWN    = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

    public interface onActionChangeListener{
        void onNetworkStatusChanged();
        void onNetworkConnected();
        void onNetworkListenFinished();
    }

    private onActionChangeListener mActionChangeListener;

    public NetworkUtil(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * This runnable used when waiting time is over.
     */
    public Runnable mWaitingNetworkChangedRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.d("[listenNetworkBroadcastForTime]mWaitingNetworkChangedRunnable start, unregister network receiver");
            try {
                if (mActionChangeListener!=null){
                    mActionChangeListener.onNetworkListenFinished();
                }
                if (mContext != null){
                    mContext.unregisterReceiver(mNetworkChangeReceiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * This receiver used to listen network change.
     * if network change happened,it will call mActionChangeListener's action which is defined by client
     */
    public BroadcastReceiver mNetworkChangeReceiver = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
		@Override
        public void onReceive(Context context, Intent intent) {
//            LogUtil.d("[listenNetworkBroadcastForTime]onReceive, network state changed");
            NetworkInfo.State wifiState ;
            NetworkInfo.State mobileState ;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if ( (mobileState != null && NetworkInfo.State.CONNECTED == mobileState) || (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) ) {
                LogUtil.d("[listenNetworkBroadcastForTime]onReceive, mobile network connected");
                if (mMainHandler != null){
                    LogUtil.d("[listenNetworkBroadcastForTime]onReceive, remove runnable!");
                    mMainHandler.removeCallbacks(mWaitingNetworkChangedRunnable);
                }
                if (mActionChangeListener != null){
                    mActionChangeListener.onNetworkConnected();
                }
                try {
                    context.unregisterReceiver(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.d("[listenNetworkBroadcastForTime]onReceive, no network");
                if (mActionChangeListener != null){
                    mActionChangeListener.onNetworkStatusChanged();
                }
            }
        }
    };

    /**
     * This method used for listen special intent for particular time.
     * @param context the context of call
     * @param listenIntent  the intent string need to listen
     * @param listener the listener defined by client
     * @param durationMileSec the time to listen
     */
    public void listenNetworkBroadcastForTime(final Context context, String listenIntent, final onActionChangeListener listener, int durationMileSec){
        if (context == null){
            LogUtil.e("listenNetworkBroadcastForTime context is null");
            return;
        }
        if (TextUtils.isEmpty(listenIntent)){
            LogUtil.e("listenNetworkBroadcastForTime : listenIntent is empty");
            return;
        }
        if (listener == null){
            LogUtil.w("listenNetworkBroadcastForTime : listener is null");
        }
        if (durationMileSec < 0){
            LogUtil.e("listenNetworkBroadcastForTime : listenIntent is empty");
            return;
        }

        mContext = context;
        mActionChangeListener = listener;

        IntentFilter filter = new IntentFilter(listenIntent);
        mContext.registerReceiver(mNetworkChangeReceiver, filter);

        mMainHandler = new Handler(mContext.getMainLooper());
        if (mMainHandler != null){
            mMainHandler.postDelayed(mWaitingNetworkChangedRunnable, durationMileSec);
        }
    }

    public boolean checkConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    
    public int[] getNetworkConnectType() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Network networks[] = connMgr.getAllNetworks();
        int networkType[] = new int[networks.length];
        for (int i = 0; i < networks.length; i++) {
			networkType[i] = connMgr.getNetworkInfo(networks[i]).getType();
		}
        LogUtil.d("getNetworkConnectType : Result is " + Arrays.toString(networkType));
		return networkType;
	}

    public boolean connectUrl(String url, InetworkResponse response){

        if(!checkConnectivity()){
            Log.d("network", "check network connection!");
            return false;
        }

        if(!isFinish){
            Log.d("network", "previous network task is until executing");
            return false;
        }

        if(response == null){
            Log.d("network", "InetworkResponse param is null");
            return false;
        }

        isFinish = false;
        this.response = response;
        new HttpsTask().execute(url);
        return true;
    }

    private class HttpsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            response.onPostNetwork(result);
        }

        @Override
        protected String doInBackground(String... urls) {
           // try {
                String result = "test";//downloadUrl(urls[0]);
                return result;

           /* } catch (IOException e) {
                e.printStackTrace();
                return Log.getStackTraceString(e);
            }*/
        }

        /*private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                // TODO Move to method with param

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("54.199.174.174")
                        .appendPath("account")
                        .appendPath("getTerms")
                        .appendQueryParameter("deviceId", "test")
                        .appendQueryParameter("countryCode", "kr");
                //String paramQuery = builder.build().getEncodedQuery();


                URL url = new URL(builder.build().toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //
                //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setReadTimeout(10000 /* milliseconds *///);
               // conn.setConnectTimeout(15000 /* milliseconds */);
                /*conn.setRequestMethod("GET");
                conn.setDoInput(true);

                //
                // set host verfifier
                //conn.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

                //
                conn.setRequestProperty("accessKey", "teetetet");
/*
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(paramQuery);
                writer.flush();
                writer.close();
                os.close();*/


                // Starts the query
               /* conn.connect();
                int response = conn.getResponseCode();
                Log.d("GlobalRoaming", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
                isFinish = true;
            }
        }*/

        @SuppressWarnings("unused")
		public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    };
    
    /**
     * Get network type
     * 
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }

    /**
     * Get network type name
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getNetworkTypeName(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        String type = NETWORK_TYPE_DISCONNECT;
        if (manager == null || (networkInfo = manager.getActiveNetworkInfo()) == null) {
            return type;
        };

        if (networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if ("WIFI".equalsIgnoreCase(typeName)) {
                type = NETWORK_TYPE_WIFI;
            } else if ("MOBILE".equalsIgnoreCase(typeName)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORK_TYPE_3G : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            } else {
                type = NETWORK_TYPE_UNKNOWN;
            }
        }
        return type;
    }
    
    public static boolean isWIFIconnected(Context context) {
		return getNetworkType(context) == ConnectivityManager.TYPE_WIFI;
	}
    
    public static boolean isMobileconnected(Context context) {
		return getNetworkType(context) == ConnectivityManager.TYPE_MOBILE;
	}

    /**
     * Whether is fast mobile network
     * 
     * @param context
     * @return
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
}
