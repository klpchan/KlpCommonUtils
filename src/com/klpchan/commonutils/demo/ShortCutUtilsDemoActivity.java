package com.klpchan.commonutils.demo;

import com.klpchan.commonutils.CommonUtilsEnv;
import com.klpchan.commonutils.R;
import com.klpchan.commonutils.shortcut.ShortCutUtils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ShortCutUtilsDemoActivity extends AppCompatActivity implements OnClickListener{

	CheckBox mFixedChineseCB;
	
	Button maddShortCutBtn;
	
	Button mdelShortCutBtn;
	
	Button mcheckShortCutBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo_shortcut);
		
		//turn on log switcher
		CommonUtilsEnv.setDebug(true);
		
		mFixedChineseCB = (CheckBox) findViewById(R.id.cb_fixchinese);
		maddShortCutBtn = (Button) findViewById(R.id.btn_addshortcut);
		mdelShortCutBtn = (Button) findViewById(R.id.btn_delshortcut);
		mcheckShortCutBtn = (Button) findViewById(R.id.btn_checkshortcut);
		
		maddShortCutBtn.setOnClickListener(this);
		mdelShortCutBtn.setOnClickListener(this);
		mcheckShortCutBtn.setOnClickListener(this);

		mFixedChineseCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				CommonUtilsEnv.SWITCHER_SHORTCUT_CHINESE_ALWASY = isChecked;
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mFixedChineseCB.setChecked(CommonUtilsEnv.SWITCHER_SHORTCUT_CHINESE_ALWASY);
	}
/*
	@Override
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_addshortcut:
			ShortCutUtils.addShortcut(ShortCutUtilsDemoActivity.this, 
					 R.string.app_name, R.drawable.ic_launcher, "中文快捷名");
			break;
		case R.id.btn_delshortcut:
			ShortCutUtils.delShortcut(ShortCutUtilsDemoActivity.this, R.string.app_name, "中文快捷名");
			break;	
		case R.id.btn_checkshortcut:
			Toast.makeText(ShortCutUtilsDemoActivity.this, "If ShortCut exist : " + 
					ShortCutUtils.hasShortcutByPackageName(this), Toast.LENGTH_SHORT).show();
			break;		
		default:
			break;
		}
	}
}
