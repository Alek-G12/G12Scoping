package com.g12.scoping.models;

import io.realm.RealmObject;

public class Answer extends RealmObject {
    private String answer;
    
    public Answer(){
    }
    
    public Answer(String answer){
        this.answer = answer;
    }
    
    public String getAnswer(){
        return answer;
    }
    
    public void setAnswer(String answer){
        this.answer = answer;
    }
}
