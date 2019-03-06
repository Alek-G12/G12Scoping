package com.g12.scoping;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.g12.scoping.models.Inspection;
import com.g12.scoping.models.Section;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

public class CreateInspectionTask extends AsyncTask<String, Void, Void> {
    
    public CreateInspectionTask(){
    }
    
    @Override
    protected Void doInBackground(String... strings){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        
        
        String type = strings[0], name = strings[1], createdBy = strings[2];
        File configFile = new File(strings[3]);
        Inspection inspection = new Inspection(type, name, new Date(), createdBy);
        
        RealmList<Section> sections = new RealmList<>();
        //TODO: Parse Sections and add Questions to each, add them to Inspection
        try(FileInputStream inputStream = new FileInputStream(configFile)){
        
        
        } catch(IOException e){
            Log.e("Parsing", "File Input Error");
        }
        XmlPullParser parser = Xml.newPullParser();
        
        realm.insert(inspection);
        
        realm.commitTransaction();
        realm.close();
        return null;
    }
}
