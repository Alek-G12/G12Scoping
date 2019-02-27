package com.g12.scoping;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    
    //User Interface references
    private FloatingActionButton fab;
    private EditText mEquipmentName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Kick off the Login Activity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog newInspectionDialog = createNewInspectionDialog();
                newInspectionDialog.show();
            }
        });
    }
    
    private AlertDialog createNewInspectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_new_inspection, null);
        mEquipmentName = dialogView.findViewById(R.id.eamName);
        builder.setTitle("New Inspection").setMessage("Create a new equipment inspection").setView(
                dialogView).setPositiveButton("Create", onClickListener);
        return builder.create();
    }
    
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i){
            //Check that equipment name is not empty
            String equipmentName = mEquipmentName.getText().toString();
            if(TextUtils.isEmpty(equipmentName)){
                Toast.makeText(MainActivity.this, "Equipment name must not be empty", Toast.LENGTH_SHORT).show();
            } else {
                //TODO: Change logic to create inspection
                StringBuilder sb = new StringBuilder("Created Inspection - ").append(mEquipmentName.getText().toString());
                String message = new String(sb);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
