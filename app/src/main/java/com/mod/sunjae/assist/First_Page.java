package com.mod.sunjae.assist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class First_Page extends AppCompatActivity implements View.OnClickListener {
    Toast toast;
    long backKeyTime;
    TextView textBtnSetting;
    TextView textBtnAdd;
    TextView textBtnRemove;
    TextView textBtnInfo;
    TextView textBase;
    RelativeLayout HomeA;
    ListAdapter listAdapter = new ListAdapter();
    TextView Viewer;
    RelativeLayout ViewerLayout;
    RelativeLayout ListLayout;
    RelativeLayout RemoveLayout;
    Intent intent;
    String removeString;
    boolean basename = false;
    DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first__list_page);
        textBtnAdd = (TextView) findViewById(R.id.textBtnAdd);
        textBtnSetting = (TextView) findViewById(R.id.textBtnSetting);
        HomeA = (RelativeLayout) findViewById(R.id.HomeA);
        Viewer = (TextView) findViewById(R.id.Viewer);
        ViewerLayout = (RelativeLayout) findViewById(R.id.ViewerLayout);
        ListLayout = (RelativeLayout) findViewById(R.id.ListLayout);
        RemoveLayout = (RelativeLayout) findViewById(R.id.RemoveLayout);
        listAdapter.listView = (ListView) findViewById(R.id.listView);
        textBtnRemove = (TextView) findViewById(R.id.RemoveButton);
        textBtnInfo = (TextView) findViewById(R.id.textBtnInfo);
        textBase = (TextView) findViewById(R.id.textbase);
        Viewer.setOnClickListener(this);
        HomeA.setOnClickListener(this);
        textBtnSetting.setOnClickListener(this);
        textBtnAdd.setOnClickListener(this);
        textBtnRemove.setOnClickListener(this);
        textBtnInfo.setOnClickListener(this);
        textBase.setOnClickListener(this);
        Context context = getApplicationContext();
        listAdapter.ListSetW(context);

        //=========구현===============================================
        dbHelper = new DbHelper();

        dbHelper.database = openOrCreateDatabase(dbHelper.dbName, MODE_WORLD_WRITEABLE, null);

        dbHelper.createTable();
        dbHelper.showallData();        //dbHelper.showallData2()는 bssid 목록 확인하는 메소드//dbHelper.showallData()는 기존 메소드
        dbHelper.closeDatabase();
        //============================================================

        //보기용!!


        for(int i=0; i < dbHelper.num; i++){
            listAdapter.adapter.add("⚪ 　"+dbHelper.arrname.get(i).toString()+"　"+dbHelper.arrssid.get(i).toString()+"　");
            if(dbHelper.arrname.get(i).toString().equals("기본") == true) {
                Log.v("db","dbHelper.arrname.get(i).toString().equals(\"기본\")");
                basename = true;
            }
        }

        if(basename != true){
            textBase.setVisibility(View.VISIBLE);
        }else
            textBase.setVisibility(View.GONE);

        listAdapter.listView.setOnItemClickListener(onClickList);
        listAdapter.listView.setOnItemLongClickListener(onLongClickList);
       // listAdapter.listView.add;
    }

    //리스트뷰 아이템 선택시 이벤트
    private AdapterView.OnItemClickListener onClickList = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewerLayout.setVisibility(View.GONE);
            intent = new Intent(First_Page.this, Edit_Page.class);
            intent.putExtra("ListString",listAdapter.adapter.getItem(position).toString());
            startActivity(intent);
            finish();
        }
    };

    private AdapterView.OnItemLongClickListener onLongClickList = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ViewerLayout.setVisibility(View.GONE);
            ListLayout.setVisibility(View.INVISIBLE);
            RemoveLayout.setVisibility(View.VISIBLE);
            removeString = listAdapter.adapter.getItem(position).toString();
            return true;
        }
    };

    //뒤로가기 이벤트
    @Override
    public void onBackPressed()
    {
        if(ViewerLayout.getVisibility() == View.VISIBLE) {
            ViewerLayout.setVisibility(View.GONE);
            return;
        }
        if(RemoveLayout.getVisibility() == View.VISIBLE) {
            ListLayout.setVisibility(View.VISIBLE);
            RemoveLayout.setVisibility(View.GONE);
        }
        if (System.currentTimeMillis() > backKeyTime + 2000) {
            backKeyTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyTime + 2000) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.Viewer:
                ViewerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.HomeA:
                ViewerLayout.setVisibility(View.GONE);
                break;
            case R.id.textbase:
                ViewerLayout.setVisibility(View.GONE);
                intent = new Intent(First_Page.this, Add_Page.class);
                intent.putExtra("base", "1");
                startActivity(intent);
                finish();
                break;
            case R.id.textBtnAdd:
                ViewerLayout.setVisibility(View.GONE);
                intent = new Intent(First_Page.this, Add_Page.class);
                if(basename == true)
                    intent.putExtra("base", "0");
                else
                    intent.putExtra("base", "1");
                startActivity(intent);
                finish();
                break;
            case R.id.textBtnSetting:
                ViewerLayout.setVisibility(View.GONE);
                intent = new Intent(First_Page.this, Settings_Page.class);
                startActivity(intent);
                break;
            case R.id.textBtnInfo:
                ViewerLayout.setVisibility(View.GONE);
                intent = new Intent(First_Page.this, Info_Page.class);
                startActivity(intent);
                break;
            //Remove버튼 : DB추가
            case R.id.RemoveButton:
                //=============tokenizer=======================================================================================
                String[] removedStr = removeString.split("\\　");
                int i;
                for (i = 0; i < removedStr.length-1; i++) ;
                //==============================================================================================================
                dbHelper.database = openOrCreateDatabase(dbHelper.dbName, MODE_WORLD_WRITEABLE, null);
                dbHelper.delete_ssid(removedStr[1], removedStr[2]);
                dbHelper.delete_bssid(removedStr[1], removedStr[2]);
                dbHelper.closeDatabase();
                //==================================================================================================================
                listAdapter.adapter.remove(removeString);
                ListLayout.setVisibility(View.VISIBLE);
                RemoveLayout.setVisibility(View.GONE);
                break;
        }
    }
}
