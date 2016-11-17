package com.mod.sunjae.assist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Settings_Page extends AppCompatActivity implements View.OnClickListener {
    TextView textSetBack;
    TextView textSetScan;
    TextView textSetsaveTime;
    EditText textScanTime;
    CheckBox checkA;
    TextView textA;
    DbHelper dbHelper;
    int check = 1;
    private ListAdapter listAdapterList = new ListAdapter(); // WIFI List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        textSetBack = (TextView) findViewById(R.id.textSetBack);
        textSetScan = (TextView) findViewById(R.id.textInfoDevlop);
        checkA = (CheckBox) findViewById(R.id.checkA);
        textA = (TextView) findViewById(R.id.textInfoSuppot);
        textScanTime = (EditText) findViewById(R.id.ScanTimeText);
        textSetsaveTime = (TextView) findViewById(R.id.textsetsaveTime);
        textA.setOnClickListener(this);
        textSetScan.setOnClickListener(this);
        textSetBack.setOnClickListener(this);

        dbHelper = new DbHelper();
        dbHelper.database = openOrCreateDatabase(dbHelper.dbName, MODE_WORLD_WRITEABLE, null);
        dbHelper.createTable();

        Context context = getApplicationContext();
        ArrayList<String> str = dbHelper.backsetint();
        textScanTime.setText(str.get(0)); // 디비에서 가져와서 넣어줌
       if(str.get(1).equals("0"))
            checkA.setChecked(false);
        textSetsaveTime.setText(((Integer.parseInt(textScanTime.getText().toString()))*15)+"초");

    }


    @Override
    public void onBackPressed()
    {
        try {
            if(!checkA.isChecked())
                check = 0;
            else
                check = 1;
            dbHelper.update_set(Integer.parseInt(textScanTime.getText().toString()), check);
        }catch(Exception e){
            Log.v("Tag",e.getMessage()+"");
        }
        dbHelper.closeDatabase();
        finish();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.textSetBack:
                try {
                    if(!checkA.isChecked())
                        check = 0;
                    else
                        check = 1;
                    int set = Integer.parseInt(textScanTime.getText().toString());
                    dbHelper.update_set(set, check);
                }catch(Exception e){
                    Log.v("Tag",e.getMessage()+"");
                }
                dbHelper.closeDatabase();
                finish();
                break;
            case R.id.textInfoDevlop:
                textScanTime.setFocusable(true);
                break;
            case R.id.textInfoSuppot:
                checkA.setChecked(!(checkA.isChecked()));
                if(!checkA.isChecked())
                    check = 0;
                else
                    check = 1;
                Log.v("Tag",check + "");
        }
    }
}
