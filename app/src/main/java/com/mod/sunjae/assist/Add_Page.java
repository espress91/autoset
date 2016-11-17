package com.mod.sunjae.assist;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class Add_Page extends AppCompatActivity implements View.OnClickListener {
    List setlist;
    ScanResult sr;
    SeekBar sBarAudio;
    SeekBar sBarLight;
    CheckBox cBoxWIFI;
    CheckBox cBJin;
    CheckBox cBMu;
    WifiManager wm;
    List apList;
    Button btnOK;
    TextView btnBack;
    TextView textWIFI;
    TextView WIFIname;
    RelativeLayout wifiLayout;
    EditText editAddName;
    Intent intent;
    TextView AudioText;
    TextView textAddBase;
    boolean wifiState = false;

    //시스템 제어
    public int set_brightness;
    int set_Audio;
    Vibrator vib;

    private ListAdapter listAdapterWIFI = new ListAdapter(); // WIFI List
    AudioManager audioManager;
    DbHelper dbHelper;
    //============================================================================
    List BSSIDlist; //bssid 리스트
    String str; //ssid 변수


    //=======================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page);

        //====================================================
        //DB class 선언==========================
        dbHelper = new DbHelper();
        //=======================================
        dbHelper.database = openOrCreateDatabase(dbHelper.dbName, MODE_WORLD_WRITEABLE, null);
        // dbHelper.createDatabase();
        dbHelper.createTable();
        //====================================================
        BSSIDlist = new ArrayList();
        //=======================================================
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        btnOK = (Button) findViewById(R.id.btnOK);
        btnBack = (TextView) findViewById(R.id.textBack);
        textWIFI = (TextView) findViewById(R.id.textWIFI);
        wifiLayout = (RelativeLayout) findViewById(R.id.WIFILayout);
        listAdapterWIFI.listView = (ListView) findViewById(R.id.mListview);
        sBarAudio = (SeekBar) findViewById(R.id.sBarAudio);
        sBarLight = (SeekBar) findViewById(R.id.sBarLight);
        cBJin = (CheckBox) findViewById(R.id.cBoxJin);
        cBMu = (CheckBox) findViewById(R.id.cBoxMu);
        cBoxWIFI = (CheckBox) findViewById(R.id.checkBoxWIFI);
        editAddName = (EditText) findViewById(R.id.editAddName);
        AudioText = (TextView) findViewById(R.id.textView4);
        textAddBase = (TextView) findViewById(R.id.textAddbase);
        WIFIname = (TextView) findViewById(R.id.textView6);
        //0511 sun
        if(wm.isWifiEnabled() == true)
            wifiState = true;

        Intent intent = getIntent();
        if(intent.getExtras().getString("base").equals("1"))
        {
            editAddName.setVisibility(View.INVISIBLE);
            textAddBase.setVisibility(View.VISIBLE);
            textWIFI.setVisibility(View.INVISIBLE);
            cBoxWIFI.setVisibility(View.INVISIBLE);
            cBoxWIFI.setChecked(false);
            WIFIname.setVisibility(View.INVISIBLE);
        }

        //OnClick이벤트
        textWIFI.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        Context context = getApplicationContext();
        listAdapterWIFI.listView.setOnItemClickListener(onClickListItemWIFI);
        listAdapterWIFI.ListSetB(context);

        // 시스템 제어
        audioManager = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
        set_Audio = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sBarAudio.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sBarAudio.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // ChangeSet(audioManager);
        onResume();
        ChangeSet set = new ChangeSet(getContentResolver(), getWindow());
        set.changeLight(sBarLight);
        set.changeAudio(sBarAudio, cBJin, cBMu, audioManager);

    }
    //아이템 클릭해서 setText 입력
    private AdapterView.OnItemClickListener onClickListItemWIFI = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            textWIFI.setText(listAdapterWIFI.adapter.getItem(position).toString());
            textWIFI.setTextColor(Color.parseColor("#000000"));
            wifiLayout.setVisibility(View.GONE);

            //SSID를 가진 BSSID를 모두 BSSIDlist에 담는 과정
            str = listAdapterWIFI.adapter.getItem(position).toString();
            if(str.substring(1,2).equals("."))
                str = str.substring(10);
            else
                str = str.substring(11);
            int listsize = apList.size();
            //List BSSIDlist = new ArrayList();     상현 수정
            for(int i = 0; i<listsize; i++) {
                sr = (ScanResult) apList.get(i);
                if(str.equals(sr.SSID.toString()))
                    BSSIDlist.add(sr.BSSID);
            }
            //str, sr.BSSID 쓰세요.
        }
    };

    //뒤로가기 버튼 Click 이벤트
    @Override
    public void onBackPressed()
    {
        if(wifiLayout.getVisibility() == View.VISIBLE)
            wifiLayout.setVisibility(View.GONE);
        else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, set_Audio, 0);
            intent = new Intent(Add_Page.this, First_Page.class);
            startActivity(intent);
            finish();
        }
    }

    //기본 Button 클릭 이벤트
    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.textBack :
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, set_Audio, 0);
                intent = new Intent(Add_Page.this, First_Page.class);
                startActivity(intent);
                finish();
            break;
            case R.id.btnOK:
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, set_Audio, 0);
                //===============확인 버튼 구현 부분=====================
                String name = editAddName.getText().toString();
                String ssid = textWIFI.getText().toString();

                int volume = sBarAudio.getProgress();
                int light = sBarLight.getProgress();
                int vibe = 0;//진동O, 무음O
                int connect = 0;
                if(cBMu.isChecked()==false && cBJin.isChecked()==true)vibe = 1;//진동O
                if(cBMu.isChecked()==true && cBJin.isChecked()==false)vibe = 2;//무음O
                if(cBMu.isChecked()==true && cBJin.isChecked()==true)vibe = 3;//진동O && 무음O

                if(cBoxWIFI.isChecked()==true)connect = 1;

                //예외처리
                if(ssid.equals("클릭!") == false){
                dbHelper.insertData(name, str, volume, light, vibe, connect);

                for(int p = 0; p < BSSIDlist.size(); p++){
                    dbHelper.insertData2(name, str, BSSIDlist.get(p).toString());
                }

                dbHelper.closeDatabase();
                //=======================================================
                intent = new Intent(Add_Page.this, First_Page.class);
                startActivity(intent);
                finish();
                break;
                }     //ssid 설정 안하고 확인버튼 누르면 바로 ssid 선택하게 함
                else if(ssid.equals("클릭!") == true && textAddBase.getText().toString().equals("기본") == true) {
                    Log.v("TAG","기본");
                    dbHelper.insertData("기본", " ", volume, light, vibe, connect);
                    dbHelper.insertData2("기본", " ","Assist");
                    dbHelper.closeDatabase();
                    //=======================================================
                    intent = new Intent(Add_Page.this, First_Page.class);
                    startActivity(intent);
                    finish();
                    break;
                }

                //================================================================================
            case R.id.textWIFI:
                wifiLayout.setVisibility(View.VISIBLE);
                scan();
                break;
        }
    }

    //스캔 소스
    // 기본 BrodcastReciver 액션 할떄 wifi스캔과 같은지 비교해서 scan
    private BroadcastReceiver wifiR = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                if(wifiState == false)
                    wm.setWifiEnabled(false);
                searchWifi();
                unregisterReceiver(wifiR);
            }
        }
    };

    public void scan(){
            if (Build.VERSION.SDK_INT >= 18) {  //  4.3 버전 이상인지 체크한다.
                if (wm.isScanAlwaysAvailable()) {  //  항상검색 허용 설정이 활성화상태인지 체크한다.
                    Log.v("Service", "4.3이상입니다.");
                    wm.startScan();  // 바로 스캔 실행
                } else {
                    wm.setWifiEnabled(true);
                    Log.v("Service", "항상 검색X");
                }
            } else
                wm.setWifiEnabled(true);
            wm.startScan();
       // }

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
            for(int j = 0; j<apList.size(); j++){ //SSID 중복 제거 (맥주소 통일화)
                ScanResult sr2 = (ScanResult) apList.get(j);
                list.add(sr2.SSID.toString());
            }
            setlist = new ArrayList(new HashSet(list));
            for (int i = 0; i < setlist.size(); i++) {
                listAdapterWIFI.adapter.add((i + 1) + ". WIFI : " + setlist.get(i));
            }
        }
    }
}
