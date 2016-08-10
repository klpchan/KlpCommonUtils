package com.klpchan.commonutils.demo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.example.klpxutildb.db.DbManager;
import com.example.klpxutildb.db.XutilDBEnv;
import com.example.klpxutildb.db.ex.DbException;
import com.klpchan.commonutils.R;
import com.klpchan.commonutils.db.Parent;

public class XutilDBDemoActivity extends AppCompatActivity implements OnClickListener{

	@Bind(R.id.btn_add)
    Button mAddbtn;
    @Bind(R.id.btn_delete)
    Button mDelbtn;
    @Bind(R.id.btn_update)
    Button mUpdatebtn;
    @Bind(R.id.btn_query)
    Button mQuerybtn;
    
    TextView mContent;
    
    List<Parent> parents;
    
    DbManager db;
    
    int ItemNumbs = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.activity_demo_xutildb);     
        
        ButterKnife.bind(this);

        XutilDBEnv.init(getApplication());
        
        db = initDaoConfig();
        
        mAddbtn = (Button) findViewById(R.id.btn_add);
        mDelbtn = (Button) findViewById(R.id.btn_delete);
        mUpdatebtn = (Button) findViewById(R.id.btn_update);
        mQuerybtn = (Button) findViewById(R.id.btn_query);
        mContent = (TextView) findViewById(R.id.text);
        
        mAddbtn.setOnClickListener(this);
        mDelbtn.setOnClickListener(this);
        mUpdatebtn.setOnClickListener(this);
        mQuerybtn.setOnClickListener(this);        
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.btn_add ) {
            try {
                parents = new ArrayList<Parent>();

                for (int i = 0; i < 5; i++) {
                    parents.add(i, createParent());
//                    db.save(parents.get(i));
//                    db.saveBindingId(parents.get(i));
                }
                
                db.saveBindingId(parents);
                
            } catch (DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (id == R.id.btn_delete) {
            try {
//                db.delete(Parent.class);
                long count = db.selector(Parent.class).count();
//                db.delete(parents.get((int) (count-1)));
                List<Parent> queryList = db.findAll(Parent.class);
                if (count >= 1) {
                	
                    db.deleteById(Parent.class,queryList.get(queryList.size() - 1).getId());
                }
            } catch (DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (id == R.id.btn_query) {  
			try {
				List<Parent> queryList = db.findAll(Parent.class);
				mContent.setText(Arrays.toString(queryList.toArray()));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }


    private Parent createParent() {
        Parent parent = new Parent();
        parent.name = "klp" + System.currentTimeMillis();
        parent.setAdmin(true);
        parent.setEmail("klpchan@qq.com" + System.currentTimeMillis());
        parent.setTime(new Date());
        parent.setDate(new java.sql.Date(new Date().getTime()));
        return parent;
    }


    private DbManager initDaoConfig() {
/*        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
        .setDbName("testklpdb")
        .setAllowTransaction(true)
        .setDbDir(new File("/sdcard"))
        .setDbVersion(1)
        .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                // TODO: ...
                // db.addColumn(...);
                // db.dropTable(...);
                // ...
            }
        });
        
        DbManager db = XutilDBEnv.getDb(daoConfig);*/
        DbManager db = XutilDBEnv.getDb();
        return db;
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	ButterKnife.unbind(this);
    	super.onDestroy();
    }
}

