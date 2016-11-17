package com.mod.sunjae.assist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Edit_Page extends AppCompatActivity implements View.OnClickListener {
    ScanResult sr;
    CheckBox cEditBWIFI;
    CheckBox cEditBJin;
    CheckBox cEditBMu;
    Button btnEditOK;
    TextView btnEditBack;
    TextView textEditWIFI;
    EditText editName;
    RelativeLayout EditwifiLayout;
    WifiManager wm;
    List apList;
    List setlist;
    SeekBar sEditBarAudio;
    SeekBar sEditBarLight;
    Intent intent;
    ArrayList<String> priorSet;
    boolean wifiState = false;
    private ListAdapter EditlistAdapterWIFI = new ListAdapter(); // WIFI List
    AudioManager audioManager;
    DbHelper dbHelper;
    //============================================================================
    List BSSIDlist; //bssid 리스트
    String str; //ssid 변수
    //=======================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__page);

        //====================================================
        //DB class 선언==========================
        dbHelper = new DbHelper();
        //=======================================
        dbHelper.database = openOrCreateDatabase(dbHelper.dbName, MODE_WORLD_WRITEABLE, null);
        // dbHelper.createDatabase();
        dbHelper.createTable();
        //====================================================
        BSSIDlist = new ArrayList();
        priorSet = new ArrayList<String>();
        //=======================================================
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        btnEditOK = (Button) findViewById(R.id.btnEditOK);
        btnEditBack = (TextView) findViewById(R.id.textEditBack);
        textEditWIFI = (TextView) findViewById(R.id.textEditWIFI);
        EditwifiLayout = (RelativeLayout) findViewById(R.id.EditWIFILayout);
        EditlistAdapterWIFI.listView = (ListView) findViewById(R.id.EditListview);
        sEditBarAudio = (SeekBar) findViewById(R.id.sEditBarAudio);
        sEditBarLight = (SeekBar) findViewById(R.id.sEditBarLight);
        cEditBJin = (CheckBox) findViewById(R.id.cEditBoxJin);
        cEditBMu = (CheckBox) findViewById(R.id.cEditBoxMu);
        cEditBWIFI = (CheckBox) findViewById(R.id.cEditBoxWIFI);
        editName = (EditText) findViewById(R.id.editName);
        textEditWIFI.setOnClickListener(this);
        btnEditOK.setOnClickListener(this);
        btnEditBack.setOnClickListener(this);
        Context context = getApplicationContext();
        EditlistAdapterWIFI.listView.setOnItemClickListener(onClickListItemWIFI);
        EditlistAdapterWIFI.ListSetB(context);
        intent = getIntent();
        BSSIDlist = new ArrayList();

        //0511 sun
        if (wm.isWifiEnabled() == true)
            wifiState = true;
        audioManager = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
        //  set_Audio = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sEditBarAudio.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sEditBarAudio.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        //name 토큰
        String str = intent.getExtras().getString("ListString");
        str = str.substring(3, str.length() - 1);
        String[] name = new String(str).split("\\　");

        editName.setText(name[0]);
        textEditWIFI.setText(name[1]);
        if (name[0].equals("기본")==false) {
            priorSet = dbHelper.select_ssid(name[0], name[1]);

            if (Integer.parseInt(priorSet.get(2)) == 1) {
                cEditBJin.setChecked(true);
            } else if (Integer.parseInt(priorSet.get(2)) == 2) {
                cEditBMu.setChecked(true);
            } else if (Integer.parseInt(priorSet.get(2)) == 3) {
                cEditBJin.setChecked(true);
                cEditBMu.setChecked(true);
            }
            if (Integer.parseInt(priorSet.get(3)) == 1) {
                cEditBWIFI.setChecked(true);
            }

            sEditBarAudio.setProgress(Integer.parseInt(priorSet.get(0)));
            sEditBarLight.setProgress(Integer.parseInt(priorSet.get(1)));
        }else
        {
            cEditBWIFI.setVisibility(View.GONE);
            textEditWIFI.setVisibility(View.GONE);
        }
    }

    private AdapterView.OnItemClickListener onClickListItemWIFI = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            textEditWIFI.setText(EditlistAdapterWIFI.adapter.getItem(position).toString());
            textEditWIFI.setTextColor(Color.parseColor("#000000"));
            EditwifiLayout.setVisibility(View.GONE);

            //SSID를 가진 BSSID를 모두 BSSIDlist에 담는 과정
            str = EditlistAdapterWIFI.adapter.getItem(position).toString();
            str = str.substring(10);
            int listsize = apList.size();
            for (int i = 0; i < listsize; i++) {
                sr = (ScanResult) apList.get(i);
                if (str.equals(sr.SSID.toString()))
                    BSSIDlist.add(sr.BSSID);
            }
        }
    };
    private BroadcastReceiver wifiR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                searchWifi();
                unregisterReceiver(wifiR);
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (EditwifiLayout.getVisibility() == View.VISIBLE)
            EditwifiLayout.setVisibility(View.GONE);
        else {
            intent = new Intent(Edit_Page.this, First_Page.class);
            startActivity(intent);
            finish();
        }
    }

    public void scan() {
        // 0408 순재 추가
        if (Build.VERSION.SDK_INT >= 18) {  //  4.3 버전 이상인지 체크한다.
            if (wm.isScanAlwaysAvailable()) {  //  항상검색 허용 설정이 활성화상태인지 체크한다.
                wm.startScan();  // 바로 스캔 실행
            }
            else {
                wm.setWifiEnabled(true);
            }
        }
        else
            wm.setWifiEnabled(true);
//-------------------------------------------------------
        wm.startScan();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiR, filter);
    }

    // wifi 스캔 후에 스캔 한 갯수만큼 출력하기
    public void searchWifi() {
        apList = wm.getScanResults();

        if (wm.getScanResults() != null) {
            int size = apList.size();
            List list = new ArrayList();
            for (int j = 0; j < apList.size(); j++) //SSID 중복 제거 (맥주소 통일화)
            {
                ScanResult sr2 = (ScanResult) apList.get(j);
                list.add(sr2.SSID.toString());
            }
            setlist = new ArrayList(new HashSet(list));
            for (int i = 0; i < setlist.size(); i++) {
                EditlistAdapterWIFI.adapter.add((i + 1) + ". WIFI : " + setlist.get(i));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textEditBack:
                intent = new Intent(Edit_Page.this, First_Page.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnEditOK:
                //===============확인 버튼 구현 부분=====================
                String name = editName.getText().toString();
                String ssid = textEditWIFI.getText().toString();
                //============tokenizer==================================
                String fullStr;
                fullStr = intent.getExtras().getString("ListString");
                String[] dividedStr = fullStr.split("\\　");
                //========================================================
                int volume = sEditBarAudio.getProgress();
                int light = sEditBarLight.getProgress();
                int vibe = 0;//진동O, 무음O
                int connect = 0;
                if(cEditBMu.isChecked()==false && cEditBJin.isChecked()==true)vibe = 1;//진동O
                if(cEditBMu.isChecked()==true && cEditBJin.isChecked()==false)vibe = 2;//무음O
                if(cEditBMu.isChecked()==true && cEditBJin.isChecked()==true)vibe = 3;//진동O && 무음O

                if(cEditBWIFI.isChecked()==true)connect = 1;

                if(dividedStr[2].equals(ssid) == false){
                    dbHelper.update_ssid(dividedStr[1], dividedStr[2], name, str, volume, light, vibe, connect);
                    dbHelper.delete_bssid(dividedStr[1], dividedStr[2]);
                    for (int p = 0; p < BSSIDlist.size(); p++) {
                        dbHelper.insertData2(name, str, BSSIDlist.get(p).toString());
                    }
                }
                else{
                    dbHelper.update_ssid(dividedStr[1], dividedStr[2], name, dividedStr[2], volume, light, vibe, connect);
                }

                    dbHelper.closeDatabase();
                    //=======================================================
                    intent = new Intent(Edit_Page.this, First_Page.class);
                    startActivity(intent);
                    finish();
                    break;  //ssid 설정 안하고 확인버튼 누르면 바로 ssid 선택하게 함
                //================================================================================
            case R.id.textEditWIFI:
                EditwifiLayout.setVisibility(View.VISIBLE);
                scan();
                break;
        }
    }
}
