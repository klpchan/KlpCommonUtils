package com.klpchan.commonutils.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.klpchan.commonutils.R;


public class MainActivity extends AppCompatActivity {

    Button btn;
    
    Context mContext;
    Activity mActivity;
    ListView sdkFunctionalityList;
    private TextView tvText;
    
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
            default:
            	break;
        }
        if (intent != null)
            startActivity(intent);
    }
    
}
