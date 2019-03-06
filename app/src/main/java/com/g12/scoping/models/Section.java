package com.g12.scoping.models;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Section extends RealmObject {
    private String name;
    private RealmList<Question> questions;
    
    public Section(String name){
        this.name = name;
    }
    public Section(){}
    
    public String getName(){
        return name;
    }
    
    public RealmList<Question> getQuestions(){
        return questions;
    }
    
    public void setQuestions(RealmList<Question> questions){
        this.questions = questions;
    }
}
