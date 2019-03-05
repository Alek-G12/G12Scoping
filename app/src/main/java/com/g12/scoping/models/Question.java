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
    private int selectedAnswer;
    
    public Question(){}
    
    public Question(int type, String name, String text, RealmList<Answer> answers){
        this.type = type;
        this.name = name;
        this.text = text;
        this.selectedAnswer = 0;
    }
    
    public Question(String name, String text, RealmList<Answer> answers, int selected){
        this.type = Question.CHOICE;
        this.name = name;
        this.text = text;
        this.answers = answers;
        this.selectedAnswer = selected;
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
}

