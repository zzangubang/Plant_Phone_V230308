package com.example.plant_iot_phone2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment ListManage;

    public static Context mContext;

    // LoginActivity에서 받은 id 정보.
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;

        // LoginActivity에서 받은 id 정보.
        Intent getIntent = getIntent();
        id = getIntent.getStringExtra("id");

        // 프래그먼트.
        ListManage = new HomeActivity_ListManage();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.listFrame, ListManage).commitAllowingStateLoss();


    }

    public void HomeFinish() {
        finish();
    }

    // model 업데이트.
    public void Refresh() {
        finish();
        overridePendingTransition(0, 0);
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}