package com.g12.scoping;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.g12.scoping.models.Answer;
import com.g12.scoping.models.Inspection;
import com.g12.scoping.models.Question;
import com.g12.scoping.models.Question.QuestionTypeException;
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
    
    @Override
    protected Void doInBackground(String... strings){
        Realm realm = Realm.getDefaultInstance();
        
        Inspection inspection = new Inspection(strings[0], strings[1], new Date(), strings[2]);
        File configFile = new File(strings[3]);
        
        try(FileInputStream inputStream = new FileInputStream(configFile)){
            //Set up XmlPullParser
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
    
            inspection.setSections(parseEquipment(parser));
    
            realm.beginTransaction();
            realm.insert(inspection);
            realm.commitTransaction();
        } catch(IOException e){
            Log.e("Parser", "File Input/Output Error " + e.getMessage());
        } catch(XmlPullParserException e){
            Log.e("Parser", "XMLParser Error " + e.getMessage());
        } catch(QuestionTypeException e){
            Log.e("Parser", "XML Question Type Error" + e.getMessage());
        } finally {
            realm.close();
        }
        return null;
    }
    
    private RealmList<Section> parseEquipment(XmlPullParser parser)
            throws XmlPullParserException, IOException, QuestionTypeException{
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "Inspection");
        RealmList<Section> sections = new RealmList<>();
        while(parser.next() != XmlPullParser.END_DOCUMENT){
            if(parser.getEventType() != XmlPullParser.START_TAG) continue;
            if(!parser.getName().equals("Equipment")) continue;
            Log.d("Parser", "Requested Equipment Found in XML");
            while(!(parser.nextTag() == XmlPullParser.END_TAG && parser.getName().equals(
                    "Equipment"))){
                Section section = parseSection(parser);
                sections.add(section);
            }
            break; // No need to parse other equipments.
        }
        return sections;
    }
    
    private Section parseSection(XmlPullParser parser)
            throws XmlPullParserException, IOException, QuestionTypeException{
        parser.require(XmlPullParser.START_TAG, null, "Section");
        if(parser.isEmptyElementTag()) parser.nextTag();
        Section section = new Section(parser.getAttributeValue(null, "name"));
        Log.d("Parser", "Found Section " + section.getName());
        RealmList<Question> questions = new RealmList<>();
        while(!(parser.nextTag() == XmlPullParser.END_TAG && parser.getName().equals("Section"))){
            Question question = parseQuestion(parser);
            questions.add(question);
        }
        section.setQuestions(questions);
        return section;
    }
    
    private Question parseQuestion(XmlPullParser parser)
            throws XmlPullParserException, IOException, QuestionTypeException{
        parser.require(XmlPullParser.START_TAG, null, "Question");
        if(parser.isEmptyElementTag()) parser.nextTag();
        Question question = new Question(Question.parseType(parser.getAttributeValue(null, "type")),
                                         parser.getAttributeValue(null, "name"),
                                         parser.getAttributeValue(null, "text"));
        Log.d("Parser", "Found Question " + question.getName());
        RealmList<Answer> answers;
        switch(question.getType()){
            case Question.BOOL:
                answers = new RealmList<>(new Answer("Yes"), new Answer("No"));
                break;
            case Question.CHOICE:
                answers = new RealmList<>();
                while(!(parser.nextTag() == XmlPullParser.END_TAG && parser.getName().equals(
                        "Question"))){
                    Answer answer = parseAnswer(parser);
                    answers.add(answer);
                }
                break;
            default:
                answers = null;
                break;
        }
        question.setAnswers(answers);
        return question;
    }
    
    private Answer parseAnswer(XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG, null, "Answer");
        if(parser.isEmptyElementTag()) parser.nextTag();
        Answer answer = new Answer(parser.getAttributeValue(null, "answer"));
        Log.d("Parser", "Found Answer " + answer.getAnswer());
        return answer;
    }
}
