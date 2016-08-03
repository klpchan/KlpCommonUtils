package com.klpchan.commonutils.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.klpchan.commonutils.LogUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

public class locationUtil {
    /**
     * using for getting your current location
     * Test Exmaple shows as below:
		CommonUtilsEnv.setDebug(true);
		Location location = locationUtil.getCurrentLocation(this);
		List<Address> ads = locationUtil.getAddressByLL(this, location.getLatitude(), 
				location.getLongitude(), 1);
		textView = (TextView) findViewById(R.id.content);
		textView.setText(locationUtil.printAddressInfo(ads.get(0)));
		Permission ACCESS_FINE_LOCATION/ACCESS_COARSE_LOCATION/ACCESS_GPS is necessary at manifes file
     */
    static LocationManager locationManager;
    static boolean isNetworkEnabled = false, isGPSEnabled = false; 
    static boolean isPassiveEnabled = false; // Not use
    static Location location;
    static double latitude, longitude;
	
    /**
     * @param context
     * @return
     */
    public static Location getCurrentLocation(Context context) {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            LogUtil.d("getCurrentLocation gps is enable " + isGPSEnabled);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
//                Common.showGPSDisabledAlert("Please enable your location or connect to cellular network.", context);
            } else {
                if (isNetworkEnabled) {
                    LogUtil.d("NetworkEnabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        LogUtil.d("GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        	LogUtil.d("getCurrentLocation " + e.getMessage());
            e.printStackTrace();
        }
        
        LogUtil.d("getCurrentLocation, location is " + location 
        		+ " latitude is " + latitude + " longitude is " + longitude);

        return location;
    }
    
    /**
     * It return the system setting about provider.
     * @param context
     * @return true if provider available, false or else.
     */
    public static boolean isProviderAvailable(Context context) {
		
		locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
		
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

		LogUtil.d("isProviderAvailable: \nisGPSEnabled : " + isGPSEnabled 
				+ "\nisNetworkEnabled : " + isNetworkEnabled 
				+ "\nisPassiveEnabled : " + isPassiveEnabled);
		
		return isGPSEnabled || isNetworkEnabled;
	}
    
    public static void showGPSDisabledAlert(String msg, final Context ctx) {
        AlertDialog alert;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ctx.startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }
    
    /**
     * This geocode service is provided by Google which need network connected.
     * @return
     */
    public static List<Address> getAddressByLL(Context context,double lati,double longi,int maxResult) {
        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        LogUtil.d("getAddressByLL, lati is " + lati + " longti is " + longi);
    	try {
    		addresses=geocoder.getFromLocation(lati, longi, maxResult);
    		if(addresses.size()>0){
    			for (int i = 0; i < addresses.size(); i++) {
    				printAddressInfo(addresses.get(i));	
				}
    		}
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		Toast.makeText(context, "getAddressByLL error", Toast.LENGTH_LONG).show();
    		e.printStackTrace();
    	}
    	return addresses;
	}
    
    public static StringBuilder printAddressInfo(Address address) {
    	StringBuilder stringBuilder = new StringBuilder();
		for(int i=0;i<address.getMaxAddressLineIndex();i++){
			stringBuilder.append(address.getAddressLine(i)).append("\n");												
		}
		stringBuilder.append(address.getLocality()).append("_");
		stringBuilder.append(address.getPostalCode()).append("_");
		stringBuilder.append(address.getCountryCode()).append("_");
		stringBuilder.append(address.getCountryName()).append("_");
		LogUtil.d(stringBuilder.toString());
		return stringBuilder;
	}
}
