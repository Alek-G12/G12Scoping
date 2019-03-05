package com.g12.scoping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;

public class InspectionActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
    }
}
