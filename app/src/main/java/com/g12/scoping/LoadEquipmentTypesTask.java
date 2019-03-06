package com.g12.scoping;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.g12.scoping.models.CustomXMLParser;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class LoadEquipmentTypesTask extends AsyncTask<String, Void, List<String>> {
    private WeakReference<Context> contextWeakReference;
    
    LoadEquipmentTypesTask(Context context){
        contextWeakReference = new WeakReference<>(context);
    }
    
    @Override
    protected List<String> doInBackground(String... strings){
        List<String> types = new ArrayList<>();
        File configFile = new File(strings[0]);
        
        try{
            InputStream inputStream = new FileInputStream(configFile);
            
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            Log.d("Parser", "Parsing XML...");
            //Start Parsing
            parser.require(XmlPullParser.START_TAG, null, "Inspection");
            
            //Go over Every start Tag
            while(parser.next() != parser.END_DOCUMENT){
                if(parser.getEventType() == parser.START_TAG){
                    String tagName = parser.getName();
                    if(tagName.equals("Equipment")){
                        types.add(parser.getAttributeValue(null, "type"));
                        Log.d("Parser",
                              "Equipment Found! " + parser.getAttributeValue(null, "type"));
                    } else {
                        //Should Skip All Tags not in the same level
                        Log.d("Parser", "Skipping Tag");
                        CustomXMLParser.skip(parser);
                    }
                }
            }
        } catch(Exception e){
            Log.e("Parser", "Error Parsing XML");
        }
        return types;
    }
    
    @Override
    protected void onPostExecute(List<String> strings){
        Context context = contextWeakReference.get();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preferencesFileKey), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            HashSet<String> types = new HashSet<>(strings);
            editor.putStringSet("types", types);
            editor.apply();
        }
        Log.d("Parser", "XML Parsing successful");
    }
}
