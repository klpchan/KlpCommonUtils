package com.klpchan.commonutils.demo;

import com.klpchan.commonutils.R;
import com.klpchan.commonutils.screenattr.DensityUtil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class ScreenAttrDemoActivity extends AppCompatActivity {
	private TextView mTV;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_screenattr);
        
        mTV = (TextView) findViewById(R.id.tv);
        
        DisplayMetrics DM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(DM);
        
        int wdip = DensityUtil.px2dip(getResources().getDisplayMetrics().widthPixels);
        int hdip = DensityUtil.px2dip(getResources().getDisplayMetrics().heightPixels);
        
        // ��ӡ��ȡ�Ŀ�͸�
        mTV.setText("densityDpi: "
                + DM.densityDpi
                + "\n"
                + "density: "
                + DM.density
                + "\n"
                + "scaledDensity: "
                + DM.scaledDensity
                + "\n"
                + "heightPixels(The absolute height of the display in pixels.): \n "
                + DM.heightPixels
                + "\n"
                + "widthPixels(The absolute width of the display in pixels.): \n "
                + DM.widthPixels
                + "\n"
                + "xdpi(The exact physical pixels per inch of the screen in the X dimension): \n "
                + DM.xdpi
                + "\n"
                + "ydpi(The exact physical pixels per inch of the screen in the Y dimension): \n "
                + DM.ydpi + "\n"

                + "wdip: " + wdip + "\n"

                + "hdip: " + hdip + "\n");
    }
}
