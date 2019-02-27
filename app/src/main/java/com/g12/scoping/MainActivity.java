package com.g12.scoping;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.g12.scoping.models.Inspection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    
    //UI references
    private FloatingActionButton fab;
    private EditText mEquipmentName;
    private Spinner mEquipmentTypeSpinner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        //Initialize Realm
        Realm.init(this);
        
        //Kick off the Login Activity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    
        //Get the UI Instances
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabOnClickListener);
    }
    
    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            AlertDialog newInspectionDialog = createNewInspectionDialog();
            newInspectionDialog.show();
        }
    };
    
    private AlertDialog createNewInspectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_new_inspection, null);
        mEquipmentName = dialogView.findViewById(R.id.eamName);
        mEquipmentTypeSpinner = dialogView.findViewById(R.id.equipmentType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                                                             R.array.EquipmentTypes,
                                                                             android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEquipmentTypeSpinner.setAdapter(adapter);
        builder.setTitle("New Inspection").setMessage("Create a new equipment inspection").setView(
                dialogView).setPositiveButton("Create", newInspectionDialogOnClickListener);
        return builder.create();
    }
    
    private DialogInterface.OnClickListener newInspectionDialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i){
            //Check that equipment name is not empty
            String equipmentName = mEquipmentName.getText().toString();
            String equipmentType = mEquipmentTypeSpinner.getSelectedItem().toString();
            if(TextUtils.isEmpty(equipmentName)){
                Toast.makeText(MainActivity.this, "Equipment name must not be empty",
                               Toast.LENGTH_SHORT).show();
                return;
            }
            createInspection(equipmentName, equipmentType);
            //Create a Toast Notification
            StringBuilder sb = new StringBuilder("Created Inspection - ").append(
                    mEquipmentName.getText().toString());
            String message = new String(sb);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    
    private void createInspection(String name, String type){
        //Build Inspection Object
        ////Date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm", Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());
        String dateCreated = sdf.format(new Date());
        ////User
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                getString(R.string.preferencesFileKey), Context.MODE_PRIVATE);
        String createdBy = sharedPreferences.getString("user", null);
        
        //Save the Inspection to the Database
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try{
            Inspection inspection = new Inspection(Inspection.getEquipmentType(type), name,
                                                   dateCreated, createdBy);
            realm.insert(inspection);
            realm.commitTransaction();
            Log.d("DB", "Inspection Entry created");
        } catch(Exception e){
            Log.e("DB", "Wrong Equipment Type. Realm Transaction Canceled");
            realm.cancelTransaction();
        }
        
    }
    
}
