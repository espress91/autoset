package com.mod.sunjae.assist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Info_Page extends AppCompatActivity implements View.OnClickListener {
    TextView textInfoBack;
    TextView textInfoDevlop;
    TextView textInfoCreater;
    TextView textInfoSuppot;
    RelativeLayout InfoCreatedLayout;
    RelativeLayout InfoNoticeLayout;
    RelativeLayout InfoReferenceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
        textInfoBack = (TextView) findViewById(R.id.textInfoBack);
        textInfoDevlop = (TextView) findViewById(R.id.textInfoDevlop);
        textInfoSuppot = (TextView) findViewById(R.id.textInfoSuppot);
        textInfoCreater = (TextView) findViewById(R.id.textInfoCreater);
        InfoCreatedLayout = (RelativeLayout) findViewById(R.id.InfoCreatedLayout);
        InfoNoticeLayout = (RelativeLayout) findViewById(R.id.InfoNoticeLayout);
        InfoReferenceLayout = (RelativeLayout) findViewById(R.id.InfoReferenceLayout);
        textInfoBack.setOnClickListener(this);
        textInfoDevlop.setOnClickListener(this);
        textInfoSuppot.setOnClickListener(this);
        textInfoCreater.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
       InfoNoticeLayout.setVisibility(View.GONE);
        InfoReferenceLayout.setVisibility(View.GONE);
        InfoCreatedLayout.setVisibility(View.GONE);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textInfoBack :
                InfoNoticeLayout.setVisibility(View.GONE);
                InfoReferenceLayout.setVisibility(View.GONE);
                InfoCreatedLayout.setVisibility(View.GONE);
                finish();
                break;
            case R.id.textInfoDevlop :
                InfoNoticeLayout.setVisibility(View.VISIBLE);
                InfoReferenceLayout.setVisibility(View.GONE);
                InfoCreatedLayout.setVisibility(View.GONE);
                break;
            case R.id.textInfoSuppot :
                InfoNoticeLayout.setVisibility(View.GONE);
                InfoReferenceLayout.setVisibility(View.VISIBLE);
                InfoCreatedLayout.setVisibility(View.GONE);
                break;
            case R.id.textInfoCreater :
                InfoNoticeLayout.setVisibility(View.GONE);
                InfoReferenceLayout.setVisibility(View.GONE);
                InfoCreatedLayout.setVisibility(View.VISIBLE);
                break;

        }
    }
}
