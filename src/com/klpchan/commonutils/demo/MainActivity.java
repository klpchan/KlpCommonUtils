package com.klpchan.commonutils.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.klpchan.commonutils.R;
import com.klpchan.commonutils.listener.DynamicListener;
import com.klpchan.commonutils.listener.DynamicListener.onActionChangeListener;
import com.klpchan.commonutils.network.InetworkResponse;
import com.klpchan.commonutils.network.NetworkUtil;


public class MainActivity extends AppCompatActivity {

    Button btn;
    
    Context mContext;
    Activity mActivity;
    ListView sdkFunctionalityList;
    private TextView tvText;
    
    private ProgressDialog mProgressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        mContext = MainActivity.this;
        mActivity = MainActivity.this;

        tvText = (TextView) findViewById(R.id.text);
        String[] sdkFunctionalityListValue = new String[]{
                "Validation LocationUtil",/* 0 */
                "Validation ShortCutUtil",/* 1 */
                "Validation ScreenAttribute",/* 2 */
                "Validation XutilsDataBase",/* 3 */
                "Validation WebViewBase", /* 4 */
                "Validation DynamicListener", /* 5 */
        };

        sdkFunctionalityList = (ListView) findViewById(R.id.Md_list_company);

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, sdkFunctionalityListValue);
        sdkFunctionalityList.setAdapter(stringArrayAdapter);

        sdkFunctionalityList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectedListItem(position);
            }

        });

        sdkFunctionalityList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                tvText.setText("");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        
    }

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
    }    
    
    protected void selectedListItem(int position) {
        Intent intent = null;

        switch (position) {
            case 0:
                intent = new Intent(mContext, LocationUtilDemoActivity.class);
                break;
            case 1:
            	intent = new Intent(mContext, ShortCutUtilsDemoActivity.class);
            	break;
            case 2:
            	intent = new Intent(mContext, ScreenAttrDemoActivity.class);
            	break;
            case 3:
            	intent = new Intent(mContext, XutilDBDemoActivity.class);
            	break;
            case 4:
            	intent = new Intent(mContext, WebViewDemoActivity.class);
            	break;	
            case 5:
            	callDynamicListen();
            	break;
            default:
            	break;
        }
        if (intent != null)
            startActivity(intent);
    }

	private void callDynamicListen() {
		NetworkUtil networkUtil = new NetworkUtil(this);
		networkUtil.connectUrl("www.baidu.com", new InetworkResponse() {
			
			@Override
			public void onPostNetwork(String response) {
				// TODO Auto-generated method stub
				;
			}
		});
		
		DynamicListener dynamicListener = new DynamicListener();
		dynamicListener.listenSpecialTargetActionForTime(this, ConnectivityManager.CONNECTIVITY_ACTION, 
				new onActionChangeListener() {
					
					@Override
					public void onTargetIntentReceived() {
						// TODO Auto-generated method stub
						dismissInProgressDialog();
						Toast.makeText(MainActivity.this, getString(R.string.stop_listen_success), 
								Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onStartListen() {
						// TODO Auto-generated method stub
						showInProgressDialog(R.string.start_listen);
					}
					
					@Override
					public void onListenTimeOut() {
						// TODO Auto-generated method stub
						dismissInProgressDialog();
						Toast.makeText(MainActivity.this, getString(R.string.stop_listen_time_out), 
								Toast.LENGTH_LONG).show();
					}
					/**
					 * After Received target intent, it also need to verify the Extra value.
					 * Such as network discount.
					 * 1 Target intent is ConnectivityManager.CONNECTIVITY_ACTION;
					 * 2 Extra ConnectivityManager.EXTRA_NO_CONNECTIVITY should be true.
					 */
					@Override
					public boolean isExtraConfirmed(Intent intent) {
						// TODO Auto-generated method stub			
						return intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
					}
				}, 5000);
	}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	dismissInProgressDialog();
    	super.onDestroy();
    }
    
    private void showInProgressDialog(int resID) {
        if (mProgressDialog == null) {
            mProgressDialog =ProgressDialog.show(this, null,
                    getString(resID), true, true);
            mProgressDialog.setCancelable(false);
        } else {
            mProgressDialog.setMessage(getString(resID));
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    private void dismissInProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    
}
