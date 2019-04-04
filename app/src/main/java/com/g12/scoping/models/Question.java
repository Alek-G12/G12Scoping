package com.g12.scoping.models;


import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * This Class has two constructors.
 * If the Question type is QuestionType.CHOICE 2nd constructor should be used providing the answers.
 * If it is any other type of question, type should be provided as an argument and the answer list
 * will be null.
 */

public class Question extends RealmObject {
    public static final int BOOL = 0, CHOICE = 1, INPUT_TEXT = 2, INPUT_NUMERIC = 3, PHOTO = 4, LOCATION = 5;
    
    @PrimaryKey
    private String id;
    private String name;
    private int type;
    private String text;
    private RealmList<Answer> answers;
    private RealmList<String> subQuestions;
    private int selected;
    private boolean isActive;
    
    public Question(){}
    
    public Question(String id, int type, String name, String text, RealmList<String> subQuestions){
        this.id = id;
        this.type = type;
        this.name = name;
        this.text = text;
        this.subQuestions = subQuestions;
        this.isActive = subQuestions != null;
        this.selected = 0;
    }
    
    
    public String getId(){
        return id;
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
    
    public int getSelected(){
        return selected;
    }
    
    public void setSelected(int selected){
        this.selected = selected;
    }
    
    public String getSelectedAnswerValue(){
        return answers.get(selected).getAnswer();
    }
    
    public static int parseType(String type) throws QuestionTypeException{
        switch(type){
            case "bool":
                return BOOL;
            case "choice":
                return CHOICE;
            case "input_text":
                return INPUT_TEXT;
            case "input_numeric":
                return INPUT_NUMERIC;
            case "photo":
                return PHOTO;
            default:
                throw new QuestionTypeException("Wrong Question Type");
        }
    }
    
    public RealmList<String> getSubQuestions(){
        return subQuestions;
    }
    
    public static class QuestionTypeException extends Exception {
        QuestionTypeException(String message){
            super(message);
        }
    }
    
    public boolean isActive(){
        return isActive;
    }
    
    public void setActive(boolean active){
        isActive = active;
    }
}