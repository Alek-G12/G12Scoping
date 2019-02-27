package com.g12.scoping.models;


import java.util.List;

/**
 * This Class has two constructors.
 * If the Question type is QuestionType.CHOICE 2nd constructor should be used providing the answers.
 * If it is any other type of question, type should be provided as an argument and the answer list
 * will be null.
 */

public class Question {
    public static final int BOOL = 0, CHOICE = 1, INPUT_TEXT = 2, INPUT_NUMERIC = 3;
    
    private int type;
    private String name;
    private String text;
    private List<String> answers;
    
    public Question(int type, String name, String text){
        this.type = type;
        this.name = name;
        this.text = text;
        this.answers = null;
    }
    
    public Question(String name, String text, List<String> answers){
        this.type = Question.CHOICE;
        this.name = name;
        this.text = text;
        this.answers = answers;
    }
    
    public int getType(){
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

