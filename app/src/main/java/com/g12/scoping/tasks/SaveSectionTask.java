package com.g12.scoping.tasks;

import android.os.AsyncTask;

import com.g12.scoping.models.Section;

import io.realm.Realm;

public class SaveSectionTask extends AsyncTask<Section, Void, Void> {
    
    
    @Override
    protected Void doInBackground(Section... sections){
        Realm realm = Realm.getDefaultInstance();
        
        return null;
    }
}
