package com.g12.scoping.models;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Section extends RealmObject implements Serializable {
    @PrimaryKey
    private String id;
    private String name;
    private RealmList<Question> questions;
    
    public Section(String id, String name){
        this.id = id;
        this.name = name;
    }
    public Section(){}
    
    public String getId(){
        return id;
    }
    
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
