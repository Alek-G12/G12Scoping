package com.g12.scoping;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.g12.scoping.fragments.SectionFragment;
import com.g12.scoping.models.Inspection;
import com.g12.scoping.models.Section;

import io.realm.Realm;

public class InspectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   SectionFragment.OnSectionChangeListener {
    
    private static final String INSPECTION = "inspection";
    private static final String USER = "user";
    private static final String NAME = "name";
    
    private Realm realm = Realm.getDefaultInstance();
    
    private Inspection mInspection;
    
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    
    private FragmentManager fragmentManager;
    
    private int mSectionId;
    private String mInspectionName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
    
        Bundle extras = this.getIntent().getExtras();
        String user = "";
        if(extras != null){
            mInspectionName = extras.getString(INSPECTION);
            user = extras.getString(USER);
        }
        
        fragmentManager = this.getSupportFragmentManager();
    
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setFitsSystemWindows(true);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setFitsSystemWindows(true);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.inflateHeaderView(R.layout.nav_drawer_header);
        TextView navHeadTitle = mNavigationView.getHeaderView(0).findViewById(
                R.id.headerInspectionName);
        navHeadTitle.setText(mInspectionName);
        TextView navHeadUser = mNavigationView.getHeaderView(0).findViewById(R.id.headerUser);
        navHeadUser.setText(user);
        setUpActionBar();
        AddSectionsToNavView();
        initFragment();
    }
    
    private void setUpActionBar(){
        Toolbar Toolbar;
        Toolbar = findViewById(R.id.inspectionToolBar);
        setSupportActionBar(Toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }
    
    private void AddSectionsToNavView(){
        this.setTitle(mInspectionName);
        //Gets the managed object from the database
        Inspection realmInspection = realm.where(Inspection.class).equalTo(NAME, mInspectionName)
                                          .findFirst();
        //Gets a non-managed copy from the object to pass between Threads.
        if(realmInspection != null){
            mInspection = realm.copyFromRealm(realmInspection);
        }
        Menu menu = mNavigationView.getMenu();
        int itemOrder = 0;
        for(Section section : mInspection.getSections()){
            menu.add(0, itemOrder, itemOrder, section.getName());
            itemOrder++;
        }
        menu.setGroupCheckable(0, true, true);
    }
    
    private void initFragment(){
        SectionFragment fragment = SectionFragment.newInstance(1, mInspection.getSections().get(0));
        mSectionId = 0;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.InspectionFragmentContainer, fragment);
        transaction.commit();
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        item.setChecked(true);
        this.setTitle(item.getTitle());
        mSectionId = item.getItemId();
        SectionFragment fragment = SectionFragment.newInstance(1, mInspection.getSections()
                                                                             .get(mSectionId));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.InspectionFragmentContainer, fragment);
        transaction.commit();
        mDrawerLayout.closeDrawers();
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy(){
        realm.close();
        super.onDestroy();
    }
    
    @Override
    public void onSectionChange(final Section section){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm){
                realm.insertOrUpdate(section);
            }
        });
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }
    
}
