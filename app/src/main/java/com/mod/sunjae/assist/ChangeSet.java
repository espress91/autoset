package com.mod.sunjae.assist;

import android.content.ContentResolver;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

/**
 * Created by SunJae on 2016-05-11.
 */
public class ChangeSet extends Add_Page implements SeekBar.OnSeekBarChangeListener {
    ContentResolver cr;
    Window window;
    AudioManager audioManager;

    public ChangeSet(ContentResolver cr, Window window) {
        this.cr = cr;
        this.window = window;
    }
    
    public void changeLight(SeekBar sBarLight) {
        try {
            if(Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS_MODE)!=0 ) {
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        try{
            set_brightness = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            Log.e("Exception e " + e.getMessage(), null);
        }
        sBarLight.setOnSeekBarChangeListener(this);
    }
    public void changeAudio(SeekBar sBarAudio, CheckBox cBJin, CheckBox cBMu, AudioManager am){
        audioManager = am;
        sBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress / 2, AudioManager.FLAG_PLAY_SOUND);
             //   AudioText.setText("음량 설정    " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        cBJin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            }
        });
        cBMu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress < 10) {
            progress = 10;
            // sBarLight.setProgress(progress);
            seekBar.setProgress(progress);
        } else if (progress > 250) {
            progress = 250;
        }

        WindowManager.LayoutParams parms = window.getAttributes();
        parms.screenBrightness = (float) progress / 250;
        parms.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        window.setAttributes(parms);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
