package com.g12.scoping.models;

import java.util.List;

public class Question {
    private QuestionType type;
    private String name;
    private String text;
    private List<String> answers;
    
    public Question(QuestionType type, String name, String text){
        this.type = type;
        this.name = name;
        this.text = text;
        this.answers = null;
    }
    
    public Question(String name, String text, List<String> answers){
        this.type = QuestionType.CHOICE;
        this.name = name;
        this.text = text;
        this.answers = answers;
    }
    
    public QuestionType getType(){
        return type;
    }
    
    public String getName(){
        return name;
    }
    
    public String getText(){
        return text;
    }
    
    public List<String> getAnswers(){
        return answers;
    }
}

enum QuestionType{
    BOOL, CHOICE, INPUT_TEXT, INPUT_NUMERIC
}