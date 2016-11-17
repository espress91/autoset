package com.mod.sunjae.assist;

import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Service_Page extends Service {
    private int priorState = TelephonyManager.CALL_STATE_IDLE;
    private String telenumber = null;
    private TelephonyManager telManager;
    private WifiManager sWm;
    AudioManager audioManager;
    private NotificationManager sNm;
    private ScanResult sSr;
    private List sList;
    private Handler sHl;
    boolean wifiState = false;
    boolean wifiConfig = false;
    int start=0;
    String lastSSID="";
    ArrayList<String> compBSSID;
    ArrayList<String> setResult = null;
    int saveTime = 0;
    private int Timer = 15 * 1000; // 주기 값
    int offlineset = 0;
    int setsaveTime = 20;
    boolean notiok = true;
    //===DbHelper 선언===============================================================
    DbHelper dbHelper;
    //===============================================================================

    @Override
    public void onCreate() {
        super.onCreate();
        //======DbHelper 선언=======================================
        dbHelper = new DbHelper();
        compBSSID = new ArrayList<String>();
        setResult = new ArrayList<String>();
        //==========================================================
        //==========================================================================================
        dbHelper.database = openOrCreateDatabase(dbHelper.dbName, MODE_WORLD_WRITEABLE, null);

        //==========================================================================================
        dbHelper.createTable();
        try {
            if(dbHelper.backset() < 1) {
                dbHelper.insertData3(20, 1);
            }
        }catch (Exception e) {}
        sWm = (WifiManager)getSystemService(WIFI_SERVICE);
        sNm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        sHl = new Handler();
        audioManager = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
       sR.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //부재중 통화러너블
        telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(smsR, filter2);
        return super.onStartCommand(intent, flags, startId);
    }

    //--------------------------------------
    Runnable sR = new Runnable() {
        @Override
        public void run() {
            //주기적으로 실행하기위한 Runnable
            //15초마다 확인하면서 saveTime과 같다면 스캔

            if(start >= saveTime) {
                Log.v("Service","Start");
                start = 0;
                ArrayList<String> str = dbHelper.backsetint();
                setsaveTime = Integer.parseInt(str.get(0)); // 디비에서 가져와서 넣어줌
                if(str.get(1).equals("0"))
                    notiok = false;
                else
                    notiok = true;
                Service_Wifi_Start();
            }else
                start++;
            sHl.postDelayed(this, Timer);
      }
    };

    public void Service_Wifi_Start() {
        if (Build.VERSION.SDK_INT >= 18) {  //  4.3 버전 이상인지 체크한다.
            if(sWm.isWifiEnabled() == false)
                wifiState = false;
            else
                wifiState = true;
            if (sWm.isScanAlwaysAvailable())   //  항상검색 허용 설정이 활성화상태인지 체크한다.
                sWm.startScan();  // 바로 스캔 실행
            else
                sWm.setWifiEnabled(true);
        }
        else
            sWm.setWifiEnabled(true);
        sWm.startScan(); // wifi 검색

        // 스캔 종료 체크
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(sWr, filter);
    }

    private BroadcastReceiver sWr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                searchWifi();
                        //여기에 딜레이 후 시작할 작업들을 입력
                        if (wifiState == false)
                            sWm.setWifiEnabled(false);
            }
        }
    };

    private BroadcastReceiver smsR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            telManager.listen(new PhoneStateListener(){
                public void onCallStateChanged(int state, String incomingNumber){

                        if(state == TelephonyManager.CALL_STATE_RINGING){
                            priorState = TelephonyManager.CALL_STATE_RINGING;
                            telenumber = incomingNumber;
                        }
                        else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                            priorState = TelephonyManager.CALL_STATE_OFFHOOK;
                            telenumber = null;
                        }
                        else if(state == TelephonyManager.CALL_STATE_IDLE){
                            if(priorState == TelephonyManager.CALL_STATE_OFFHOOK){
                                priorState = TelephonyManager.CALL_STATE_IDLE;
                                telenumber = null;
                            }
                            else if(priorState == TelephonyManager.CALL_STATE_RINGING){
                                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                                        audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) == 0) {
                                    SmsManager mSmsManager = SmsManager.getDefault();
                                    mSmsManager.sendTextMessage(telenumber, null, "현재 부재중이라 전화를 받을 수 없습니다.", null, null);
                                }
                                priorState = TelephonyManager.CALL_STATE_IDLE;
                                telenumber = null;
                            }
                        }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
    };

    public void searchWifi()
    {
        int size;
        String last = lastSSID;
        Boolean saveTo = false;
        Boolean lowLevel = false;

        // level 순 정렬
        if((sList=sWm.getScanResults()) !=null) {
            ScanResult sr;
            size = sList.size();
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level <rhs.level ? 1 : (lhs.level==rhs.level ? 0 : -1));
                }
            };
            Collections.sort(sList, comparator);
          //----------------------------------------------------
            lastSSID = lastSSID;
            for(int i = 0; i< size; i++)
            {
                sSr = (ScanResult) sList.get(i);
                //===========================================================================================
                String[] bssidArr = {""};
                try {
                    if((compBSSID = dbHelper.compareSSID(sSr.SSID)).isEmpty() == true)
                        continue;
                    bssidArr = compBSSID.toArray(new String[compBSSID.size()]);
                }catch(Exception e){
                    Log.v("Service_Error", "Error : " + e);
                    break;
                }

                //=============================================================================================
                for(int k = 0; k < bssidArr.length; k++){
                   if(sSr.BSSID.equals(bssidArr[k].toString())) { //인식되는 ssid = sSr.SSID, //인식되는 bssid = sSr.BSSID
                        setResult = dbHelper.select_set(sSr.BSSID);  //setResult = ArrayLiset: name, ssid, volume, light, vibe, connect 차례로 있음

                       //낮은 레벨에 와이파이가 존재하면 연결하지 않는다.

                       switch(sSr.level/10)
                       {
                           case -9 :
                           case -8 : lowLevelWifiset();
                                    lowLevel = true; break;
                           default: lowLevel = false;
                       }

                       // 마지막 알림과 설정이 같은지 확인 작업 필요
                       if(last.equals(sSr.SSID)==true && Integer.parseInt(setResult.get(5)) == 1  && lowLevel == false && sWm.isWifiEnabled() == false) {
                           offlineset = 1;
                           return;
                       }else
                        offlineset = 0;

                        if(sWm.getConnectionInfo().getSSID().equals("\""+sSr.SSID+"\"") == false && Integer.parseInt(setResult.get(5)) == 1 && offlineset == 0 && lowLevel == false||
                                last.equals("") && Integer.parseInt(setResult.get(5)) == 1 && lowLevel == false&& offlineset == 0){
                            lastSSID = sSr.SSID;
                            wifiConfig = true;
                            saveTime =0;
                            offlineset = 0;
                            if(notiok == true)
                                Noti();
                            Setting();
                            WIFIConfig();
                        } else if (Integer.parseInt(setResult.get(5)) == 0 && last.equals(sSr.SSID) == false && lowLevel == false) {
                            lastSSID = sSr.SSID;
                            saveTime =0;
                            offlineset = 0;
                            if(notiok == true)
                                Noti();
                            Setting();
                        } else if(last.equals(sSr.SSID)==true && saveTo == false) {
                            //지속적으로 같은 것이 올 경우에 대비하여 시간초를 증가시킴
                            saveTo = true;
                           if(saveTime == 0 || saveTime == setsaveTime)
                               saveTime = 1;
                           else
                               saveTime *= 2;
                           if(saveTime > setsaveTime)
                               saveTime = 20;
                       }
                        return;
                    }
                }
            }
            //설정 한 Wifi가 존재하지 않을 경우
            lastSSID = "";
            offlineset = 0;
            setResult =  dbHelper.select_set("Assist");
            if(!setResult.isEmpty() && lastSSID.equals("") == false) {
                Setting();
            }
            lowLevelWifiset();
            if(saveTime == 0 || saveTime == setsaveTime)
                saveTime = 1;
            else
                saveTime *= 2;
            if(saveTime > setsaveTime)
                saveTime = 20;
        }
    }
    public void lowLevelWifiset() {
        if(wifiConfig == true)
        {
            wifiConfig = false;
            sWm.setWifiEnabled(false);
        }
    }
    public void Noti() {
        // 알림 객체 생성
        Notification noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("WIFI Change!!")
                .setContentTitle("WIFI Change - '" + sSr.SSID + "'") //와이파이 ssid값 뽑아야
                .setContentText("'" + setResult.get(0) +"'") //와이파이 name 뽑아야
                .setWhen(System.currentTimeMillis())
                .build();

        // 알림 방식 지정
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        sNm.notify(100, noti);
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void Setting() {

        int set_Audio = 0, set_Brite = 0;
        int SoundisChecked;
        // DB값 저장
        set_Audio = Integer.parseInt(setResult.get(2));
        set_Brite = Integer.parseInt(setResult.get(3));
        SoundisChecked = Integer.parseInt(setResult.get(4));

        //입력된 와이파이로 변경하기
         int wifi = Integer.parseInt(setResult.get(5));




        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, set_Brite);
        Log.v("Audio",set_Audio+"");
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, set_Audio, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, set_Audio, 0);

        if (SoundisChecked == 1) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if (SoundisChecked == 2) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if(SoundisChecked == 3){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    //WIFI 연결 <이순재> 더 보안해야함.
    public void WIFIConfig()
    {
            sWm.setWifiEnabled(true);
            wifiState = true;

        //0515 sun 연결된 와이파이가 이미 맞다면 바로 반환
        if(sWm.getConnectionInfo().getSSID().equals("\""+sSr.SSID+"\"")){
            return;
        }

        WifiConfiguration wfc = new WifiConfiguration();

        //0515 sun 추가 더 필요
        wfc.SSID = "\"".concat(sSr.SSID).concat("\"");//ssid.concat("\"");
        wfc.status = WifiConfiguration.Status.DISABLED;
        wfc.priority = 40;
        WifiS(wfc);
        int networkId = sWm.addNetwork(wfc);
        //이미 저장되어 있는 와이파이이면
        if (networkId != -1) {
            sWm.enableNetwork(networkId, true);
        }
        else {
            List<WifiConfiguration> list = sWm.getConfiguredNetworks();
            if (list.isEmpty()) {
               Log.v("Error", "list가 빔");
            }

            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + sSr.SSID + "\"")) {
                    sWm.disconnect();
                    sWm.enableNetwork(i.networkId, true);
                    sWm.reconnect();
                    break;
                }
            }
        }
    }

    public void WifiS(WifiConfiguration wfc) {
        if (sSr.capabilities.contains("WEP") == true) {
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.wepKeys[0] = "";   //--이부분중요. \"부분 빼고 그냥 비밀번호만 넣는다
            wfc.wepTxKeyIndex = 0;
        } else if (sSr.capabilities.contains("WPA") == true || sSr.capabilities.contains("WPA2") == true) {
            //wpa나 wpa2일때
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wfc.preSharedKey = "\"".concat("").concat("\"");


        }else if(sSr.capabilities.contains("OPEN") == true){
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.clear();
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
    }


}