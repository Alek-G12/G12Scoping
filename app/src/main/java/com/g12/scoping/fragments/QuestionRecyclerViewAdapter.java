package com.g12.scoping.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.g12.scoping.R;
import com.g12.scoping.fragments.SectionFragment.OnSectionChangeListener;
import com.g12.scoping.models.Answer;
import com.g12.scoping.models.Question;

import java.util.ArrayList;
import java.util.List;


public class QuestionRecyclerViewAdapter
        extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.GenericQuestionViewHolder> {
    
    private List<Question> questions;
    private final SectionFragment.OnSectionChangeListener mListener;
    
    QuestionRecyclerViewAdapter(List<Question> questions, OnSectionChangeListener listener){
        this.questions = questions;
        mListener = listener;
    }
    
    @Override
    public int getItemViewType(int position){
        return questions.get(position).getType();
    }
    
    @NonNull
    @Override
    public GenericQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view;
        switch(viewType){
            case Question.BOOL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_question_bool,
                                                                        parent, false);
                return new BooleanQuestionViewHolder(view);
            case Question.CHOICE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_question_choice, parent, false);
                return new ChoiceQuestionViewHolder(view);
            case Question.INPUT_TEXT:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_question_text,
                                                                        parent, false);
                return new TextQuestionViewHolder(view);
            case Question.INPUT_NUMERIC:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_question_numeric, parent, false);
                return new NumericQuestionViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull final GenericQuestionViewHolder holder, int position){
        holder.qText.setText(questions.get(position).getText());
        List<String> stringAnswers = new ArrayList<>();
        ArrayAdapter<String> adapter;
        Question question = questions.get(position);
        boolean active = true;
//        if(question.getDependsOn() != null){
//            TODO: add logic to check for boolean dependency and disable question
//            for(String depends: question.getDependsOn()){
//                RealmQuery query = mSection.getRealm().where(Question.class).equalTo(
//                        "name", depends);
//                Question parent = (Question) query.findFirst();
//                if(parent.getAnswers().get(parent.getSelectedAnswer()).getAnswer().equals("No")){
//                    active = false;
//                }
//            }
//        }
        switch(question.getType()){
            case Question.BOOL:
                BooleanQuestionViewHolder booleanHolder = (BooleanQuestionViewHolder) holder;
                booleanHolder.qSpinner.setEnabled(active);
                for(Answer answer : questions.get(position).getAnswers()){
                    stringAnswers.add(answer.getAnswer());
                }
                adapter = new ArrayAdapter<>(holder.mView.getContext(),
                                             android.R.layout.simple_spinner_item, stringAnswers);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                booleanHolder.qSpinner.setAdapter(adapter);
                booleanHolder.qSpinner.setSelection(question.getSelectedAnswer());
                break;
            
            case Question.CHOICE:
                ChoiceQuestionViewHolder choiceHolder = (ChoiceQuestionViewHolder) holder;
                choiceHolder.qSpinner.setEnabled(active);
                stringAnswers = new ArrayList<>();
                for(Answer answer : questions.get(position).getAnswers()){
                    stringAnswers.add(answer.getAnswer());
                }
                adapter = new ArrayAdapter<>(holder.mView.getContext(),
                                             android.R.layout.simple_spinner_item, stringAnswers);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choiceHolder.qSpinner.setAdapter(adapter);
                choiceHolder.qSpinner.setSelection(question.getSelectedAnswer());
                break;
            
            case Question.INPUT_TEXT:
                TextQuestionViewHolder textHolder = (TextQuestionViewHolder) holder;
                textHolder.qAnswer.setEnabled(active);
                textHolder.qAnswer.setText(question.getAnswers().get(0).getAnswer());
                break;
            
            case Question.INPUT_NUMERIC:
                NumericQuestionViewHolder numericHolder = (NumericQuestionViewHolder) holder;
                numericHolder.qAnswer.setEnabled(active);
                numericHolder.qAnswer.setText(question.getAnswers().get(0).getAnswer());
                break;
        }
        
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(null != mListener){
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }
    
    @Override
    public int getItemCount(){
        return questions.size();
    }
    
    static class GenericQuestionViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView qText;
        
        GenericQuestionViewHolder(View view){
            super(view);
            mView = view;
            qText = mView.findViewById(R.id.qText);
        }
        
    }
    
    public static class BooleanQuestionViewHolder extends GenericQuestionViewHolder {
        final AppCompatSpinner qSpinner;
        
        BooleanQuestionViewHolder(View view){
            super(view);
            qSpinner = view.findViewById(R.id.qSpinner);
        }
    }
    
    public static class ChoiceQuestionViewHolder extends GenericQuestionViewHolder {
        final AppCompatSpinner qSpinner;
        
        ChoiceQuestionViewHolder(View view){
            super(view);
            qSpinner = view.findViewById(R.id.qSpinner);
        }
    }
    
    public static class TextQuestionViewHolder extends GenericQuestionViewHolder {
        final EditText qAnswer;
        
        TextQuestionViewHolder(View view){
            super(view);
            qAnswer = view.findViewById(R.id.qAnswer);
        }
    }
    
    public static class NumericQuestionViewHolder extends GenericQuestionViewHolder {
        final EditText qAnswer;
        
        NumericQuestionViewHolder(View view){
            super(view);
            qAnswer = view.findViewById(R.id.qAnswer);
        }
    }
    
    
}
