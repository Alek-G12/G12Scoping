package com.g12.scoping.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Answer extends RealmObject {
    @PrimaryKey
    private String id;
    private String answer;
    
    public Answer(){
    }
    
    public Answer(String id, String answer){
        this.id = id;
        this.answer = answer;
    }
    
    public String getAnswer(){
        return answer;
    }
    
    public void setAnswer(String answer){
        this.answer = answer;
    }
}
