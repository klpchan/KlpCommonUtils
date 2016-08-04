package com.klpchan.commonutils.demo;

import java.util.List;
import com.klpchan.commonutils.CommonUtilsEnv;
import com.klpchan.commonutils.R;
import com.klpchan.commonutils.location.locationUtil;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class LocationUtilDemoActivity extends AppCompatActivity {

	TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo_location);
		
		//turn on log switcher
		CommonUtilsEnv.setDebug(true);
		
		textView = (TextView) findViewById(R.id.content);
		
		if (locationUtil.isProviderAvailable(this)) {
			Location location = locationUtil.getCurrentLocation(this);
			if (location != null) {
				List<Address> ads = locationUtil.getAddressByLL(this, location.getLatitude(), 
						location.getLongitude(), 1);
				/*it need to judge becuase sometimes we can only get latitude and longtitue by GPS,
				 * but Get address failed cause network not connected.*/
				if (ads.size() >= 1) {
					textView.setText(locationUtil.printAddressInfo(ads.get(0)));
				}
			}
		} else {
			locationUtil.showGPSDisabledAlert("Provider not available", this);
		}
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
