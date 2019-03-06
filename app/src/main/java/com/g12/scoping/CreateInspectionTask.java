package com.g12.scoping;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.g12.scoping.models.Answer;
import com.g12.scoping.models.CustomXMLParser;
import com.g12.scoping.models.Inspection;
import com.g12.scoping.models.Question;
import com.g12.scoping.models.Section;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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
        
        Inspection inspection = new Inspection(strings[0], strings[1], new Date(), strings[2]);
        File configFile = new File(strings[3]);
        
        try(FileInputStream inputStream = new FileInputStream(configFile)){
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag(); //<Inspection>
            parser.require(XmlPullParser.START_TAG, null, "Inspection");
            
            while(parser.next() != XmlPullParser.END_DOCUMENT){ //<Equipment>
                if(parser.getEventType() != XmlPullParser.START_TAG){
                    continue;
                }
                //Check for the right equipment type and skip if not found
                if(parser.getName().equals("Equipment") && parser.getAttributeValue(null, "type").equals(inspection.getEquipmentType())){
                    Log.d("Parser", "Found Equipment");
                    RealmList<Section> sections = new RealmList<>();
                    while(!(parser.nextTag() == parser.END_TAG && parser.getName().equals(
                            "Equipment"))){ //<Section> Loop ends on </Equipment>
                        Section section = new Section(parser.getAttributeValue(null, "name"));
                        Log.d("Parser", "Found Section " + section.getName());
                        RealmList<Question> questions = new RealmList<>();
                        while(!(parser.nextTag() == parser.END_TAG && parser.getName().equals(
                                "Section"))){ //<Question> Loop ens on </Section>
                            Question question;
                            try{
                                int questionType = Question.parseType(
                                        parser.getAttributeValue(null, "type"));
                                question = new Question(questionType,
                                                        parser.getAttributeValue(null, "name"),
                                                        parser.getAttributeValue(null, "text"));
                                Log.d("Parser", "Found Question " + question.getName());
                                RealmList<Answer> answers = new RealmList<>();
                                while(!(parser.nextTag() == parser.END_TAG && parser.getName()
                                                                                      .equals(
                                                                                              "Question"))){
                                    //<Answer> Loop ends on  </Question>
                                    answers.add(new Answer(parser.getAttributeValue(null, "text")));
                                    Log.d("Parser", "Found Answer ");
                                }
                                question.setAnswers(answers);
                                questions.add(question);
                            } catch(Exception e){
                                Log.e("Parser", "Wrong attribute question type in XML file");
                                Log.e("Parser", e.getMessage());
                            }
                        }
                        section.setQuestions(questions);
                        sections.add(section);
                    }
                    inspection.setSections(sections);
                    break;
                } else {
                    //Skip Other Equipment Types
                    //CustomXMLParser.skip(parser);
                }
            }
            realm.beginTransaction();
            realm.insert(inspection);
            realm.commitTransaction();
        } catch(IOException e){
            Log.e("Parser", "File Input Error");
        } catch(XmlPullParserException e){
            Log.e("Parser", "Parser Error " + e.getMessage());
        } finally {
            realm.close();
        }
        return null;
    }
}
