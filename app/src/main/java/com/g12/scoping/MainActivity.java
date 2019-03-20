package com.g12.scoping;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.g12.scoping.models.Inspection;
import com.g12.scoping.tasks.CreateInspectionTask;
import com.g12.scoping.tasks.LoadEquipmentTypesTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private SharedPreferences sharedPreferences;
    private Set<String> types;
    private File configFile;
    
    /**
     * UI References
     */
    private FloatingActionButton mFab;
    private EditText mEquipmentName;
    private Spinner mEquipmentTypeSpinner;
    private RecyclerView mInspectionRecyclerView;
    private Realm realm;
    private String user;
    
    /**
     * OnClick Listeners implementations
     */
    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            AlertDialog newInspectionDialog = createNewInspectionDialog();
            newInspectionDialog.show();
        }
    };
    
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    
        sharedPreferences = this.getSharedPreferences(getString(R.string.preferencesFileKey),
                                                      Context.MODE_PRIVATE);
        configFile = new File(Environment
                                      .getExternalStorageDirectory() + File.separator + "Scoping" + File.separator + "questions.xml");
        user = sharedPreferences.getString("user", null);
        //Kick off the Login Activity if no active session
        if(!sharedPreferences.getBoolean("active", false)){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    
        loadEquipmentTypes();
        initUI();
    }
    
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App").setMessage(
                "Are you sure? You will be logged out and the app will close").setPositiveButton(
                "Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("active", false);
                        editor.apply();
                        MainActivity.super.onBackPressed();
                    }
                });
        builder.show();
    }
    
    /**
     * Set the User Interface, register onClickListeners, etc...
     */
    private void initUI(){
        //Toolbar
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        this.setSupportActionBar(toolbar);
        
        //Floating Button
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(fabOnClickListener);
        
        //Inspection Recycler View
        mInspectionRecyclerView = findViewById(R.id.inspectionRecyclerView);
        mInspectionRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mInspectionRecyclerView.setLayoutManager(layoutManager);
        
        //Fill the Inspection List ^
        RealmQuery<Inspection> query = realm.where(Inspection.class);
        RealmResults<Inspection> inspectionList = query.findAll();
        RecyclerView.Adapter adapter = new InspectionAdapter(inspectionList);
        mInspectionRecyclerView.setAdapter(adapter);
    }
    
    /**
     * Check if there is an equipment types list, if there's not, ask the user for permission
     * to read the file and fire an async task to read it.
     */
    private void loadEquipmentTypes(){
        types = sharedPreferences.getStringSet("types", null);
        if(types == null){
            String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            Log.d("EqLst", "Equipment List NOT Found");
            Log.d("Perm", "Checking Permission READ_EXTERNAL_STORAGE");
            if(ContextCompat.checkSelfPermission(this,
                                                 permission) != PackageManager.PERMISSION_GRANTED){
                Log.d("Perm", "Prompting User for Permission...");
                ActivityCompat.requestPermissions(this, new String[]{permission},
                                                  REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                Log.d("Perm", "Permission was already granted");
                LoadEquipmentTypesTask loadEquipmentTypesTask = new LoadEquipmentTypesTask(this);
                loadEquipmentTypesTask.execute(configFile.getPath());
            }
        } else {
            Log.d("EqLst", "Equipment List Found");
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        switch(requestCode){
            case REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("Perm", "Permission Granted by user");
                    LoadEquipmentTypesTask loadEquipmentTypesTask = new LoadEquipmentTypesTask(
                            this);
                    loadEquipmentTypesTask.execute(configFile.getPath());
                }
                break;
        }
    }
    
    private AlertDialog createNewInspectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_new_inspection, null);
        mEquipmentName = dialogView.findViewById(R.id.eamName);
        mEquipmentTypeSpinner = dialogView.findViewById(R.id.equipmentType);
        types = sharedPreferences.getStringSet("types", null);
        ArrayAdapter<String> adapter = null;
        List<String> listTypes = new ArrayList<>(types);
        Collections.sort(listTypes);
        try{
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                                         listTypes);
        } catch(NullPointerException e){
            Log.d("EqLst", "Equipment List is null");
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEquipmentTypeSpinner.setAdapter(adapter);
        builder.setTitle("New Inspection").setMessage("Create a new equipment inspection").setView(
                dialogView).setPositiveButton("Create", newInspectionDialogOnClickListener);
        return builder.create();
    }
    
    private void createInspection(String name, String type){
        //Start Async Task to Create Inspection model and sub models from XML
        CreateInspectionTask createInspectionTask = new CreateInspectionTask();
        createInspectionTask.execute(type, name, user, configFile.getPath());
        Log.d("DB", "Inspection Entry created");
        
    }
    
    private class InspectionAdapter
            extends RecyclerView.Adapter<InspectionAdapter.InspectionViewHolder> {
    
        private List<Inspection> inspectionList;
        
        private InspectionAdapter(List<Inspection> inspectionList){
            this.inspectionList = inspectionList;
        }
        
        @NonNull
        @Override
        public InspectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.inspection_list_item, parent, false);
            return new InspectionViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull InspectionViewHolder holder, int position){
            final Inspection inspection = inspectionList.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm", Locale.US);
            sdf.setTimeZone(TimeZone.getDefault());
            holder.inspectionName.setText(inspection.getName());
            try{
                holder.equipmentType.setText(inspection.getEquipmentType());
            } catch(Exception e){
                Log.e("db", "Error parsing Equipment type");
            }
            holder.nameCreated.setText(inspection.getCreatedBy());
            holder.dateCreated.setText(sdf.format(inspection.getDateCreated()));
            holder.nameModified.setText(inspection.getModifiedBy());
            holder.dateModified.setText(sdf.format(inspection.getDateModified()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(MainActivity.this, InspectionActivity.class);
                    //TODO: Add Intent Extras needed for Inspection Activity
                    intent.putExtra("inspection", inspection.getName());
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }
        
        @Override
        public int getItemCount(){
            return inspectionList.size();
        }
        
        private class InspectionViewHolder extends RecyclerView.ViewHolder {
            private TextView inspectionName;
            private TextView equipmentType;
            private TextView nameCreated;
            private TextView dateCreated;
            private TextView nameModified;
            private TextView dateModified;
            private ImageView inspectionIcon;
            
            private InspectionViewHolder(View v){
                super(v);
                inspectionName = v.findViewById(R.id.inspectionName);
                equipmentType = v.findViewById(R.id.eqDescriptor);
                nameCreated = v.findViewById(R.id.nameCreated);
                dateCreated = v.findViewById(R.id.dateCreated);
                nameModified = v.findViewById(R.id.nameModified);
                dateModified = v.findViewById(R.id.dateModified);
                inspectionIcon = v.findViewById(R.id.inspectionIcon);
            }
        }
    }
}
