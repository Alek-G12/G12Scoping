package com.g12.scoping;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    
    //UI references
    private FloatingActionButton mFab;
    private EditText mEquipmentName;
    private Spinner mEquipmentTypeSpinner;
    private RecyclerView mInspectionRecyclerView;
    private Realm realm;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        //Initialize Realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    
        //Get Shared References
        sharedPreferences = this.getSharedPreferences(getString(R.string.preferencesFileKey),
                                                      Context.MODE_PRIVATE);
    
        //Kick off the Login Activity if no active session
        //TODO: implement the Logging out process
        if(!sharedPreferences.getBoolean("active", false)){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
        
        //Get the UI Instances
        initRecycleView();
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(fabOnClickListener);
    }
    
    private void initRecycleView(){
        mInspectionRecyclerView = findViewById(R.id.inspectionRecyclerView);
        mInspectionRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mInspectionRecyclerView.setLayoutManager(layoutManager);
        RealmQuery<Inspection> query = realm.where(Inspection.class);
        RealmResults<Inspection> inspectionList = query.findAll();
        RecyclerView.Adapter adapter = new InspectionAdapter(inspectionList);
        mInspectionRecyclerView.setAdapter(adapter);
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
        String createdBy = sharedPreferences.getString("user", null);
        //Save the Inspection to the Database
        realm.beginTransaction();
        try{
            Inspection inspection = new Inspection(Inspection.parseEquipmentType(type), name,
                                                   dateCreated, createdBy);
            realm.insert(inspection);
            realm.commitTransaction();
            Log.d("DB", "Inspection Entry created");
        } catch(Exception e){
            Log.e("DB", "Wrong Equipment Type. Realm Transaction Canceled");
            realm.cancelTransaction();
        }
        
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
            InspectionViewHolder viewHolder = new InspectionViewHolder(view);
            return viewHolder;
        }
        
        @Override
        public void onBindViewHolder(@NonNull InspectionViewHolder holder, int position){
            Inspection inspection = inspectionList.get(position);
            holder.inspectionName.setText(inspection.getName());
            try{
                holder.equipmentType.setText(
                        Inspection.parseEquipmentType(inspection.getEquipmentType()));
            } catch(Exception e){
                Log.e("db", "Error parsing Equipment type");
            }
            holder.nameCreated.setText(inspection.getCreatedBy());
            holder.dateCreated.setText(inspection.getDateCreated());
            holder.nameModified.setText(inspection.getModifiedBy());
            holder.dateModified.setText(inspection.getDateModified());
            //TODO: Add Logic to display correct icon
            switch(inspection.getEquipmentType()){
                case Inspection.FILTER:
                    break;
                case Inspection.EXCHANGER:
                    break;
                case Inspection.AIR_RECEIVER:
                    holder.inspectionIcon.setImageResource(R.mipmap.icon_airreceiver);
                    break;
                case Inspection.VESSEL:
                    break;
                case Inspection.TANK:
                    holder.inspectionIcon.setImageResource(R.mipmap.icon_tank);
                    break;
                case Inspection.PIPE:
                    break;
            }
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
