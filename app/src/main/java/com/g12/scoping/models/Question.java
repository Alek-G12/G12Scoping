package com.g12.scoping.models;


import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * This Class has two constructors.
 * If the Question type is QuestionType.CHOICE 2nd constructor should be used providing the answers.
 * If it is any other type of question, type should be provided as an argument and the answer list
 * will be null.
 */

public class Question extends RealmObject {
    public static final int BOOL = 0, CHOICE = 1, INPUT_TEXT = 2, INPUT_NUMERIC = 3;
    
    private int type;
    private String name;
    private String text;
    private RealmList<Answer> answers;
    private Integer selectedAnswer;
    
    public Question(){}
    
    public Question(int type, String name, String text){
        this.type = type;
        this.name = name;
        this.text = text;
        switch(type){
            case BOOL:
                answers = new RealmList<>();
                answers.add(new Answer("yes"));
                answers.add(new Answer("No"));
                selectedAnswer = null;
                break;
            default:
                answers = null;
                selectedAnswer = null;
                break;
        }
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
    
    public List<Answer> getAnswers(){
        return answers;
    }
    
    public void setAnswers(RealmList<Answer> answers){
        this.answers = answers;
    }
    
    public int getSelectedAnswer(){
        return selectedAnswer;
    }
    
    public void setSelectedAnswer(int selectedAnswer){
        this.selectedAnswer = selectedAnswer;
    }
    
    public static int parseType(String type) throws Exception{
        switch(type){
            case "bool":
                return BOOL;
            case "choice":
                return CHOICE;
            case "input_text":
                return INPUT_TEXT;
            case "input_numeric":
                return INPUT_NUMERIC;
            default:
                throw new Exception("Wrong Question Type");
        }
    }
}

