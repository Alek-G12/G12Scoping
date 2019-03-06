package com.g12.scoping;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;

public class InspectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Realm realm = Realm.getDefaultInstance();
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    
    private String inspectionName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        setContentView(R.layout.activity_inspection);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        Menu menu = mNavigationView.getMenu();
        AddSectionsToNavView();
    }
    
    private void AddSectionsToNavView(){
    
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        //TODO: Add code here to swap the fragments based on the selected Section.
        return true;
    }
}
