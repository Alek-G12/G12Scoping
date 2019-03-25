package com.g12.scoping.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.g12.scoping.R;
import com.g12.scoping.fragments.SectionFragment.OnSectionChangeListener;
import com.g12.scoping.models.Answer;
import com.g12.scoping.models.Question;
import com.g12.scoping.models.Section;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class QuestionRecyclerViewAdapter
        extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.GenericQuestionViewHolder> {
    
    private Section section;
    private List<Question> questions;
    private final SectionFragment.OnSectionChangeListener mListener;
    
    QuestionRecyclerViewAdapter(Section section, OnSectionChangeListener listener){
        this.section = section;
        this.questions = section.getQuestions();
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
            case Question.PHOTO:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_question_photo, parent, false);
                return new PhotoQuestionViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull final GenericQuestionViewHolder holder, int position){
        //Current Question
        Question question = questions.get(position);
        holder.qText.setText(question.getText());
        //Common text field for all question types
        boolean active = isQuestionActive(question);
        switch(question.getType()){
            case Question.BOOL:
                BooleanQuestionViewHolder booleanHolder = (BooleanQuestionViewHolder) holder;
                setSpinnerProperties(booleanHolder.qSpinner, active, question);
                break;
            
            case Question.CHOICE:
                ChoiceQuestionViewHolder choiceHolder = (ChoiceQuestionViewHolder) holder;
                choiceHolder.qSpinner.setEnabled(active);
                setSpinnerProperties(choiceHolder.qSpinner, active, question);
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
            case Question.PHOTO:
                PhotoQuestionViewHolder photoHolder = (PhotoQuestionViewHolder) holder;
                photoHolder.photoButton.setEnabled(active);
                photoHolder.photoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        //TODO: Add logic to launch camera activity and take photos
                    }
                });
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
    
    private boolean isQuestionActive(Question question){
        if(question.getDependsOn() == null) return true;
        
        Realm realm = Realm.getDefaultInstance();
        for(String depends : question.getDependsOn()){
            try{
                String[] qa = depends.split(":", 2);
                Section realmSection = realm.where(Section.class).equalTo("id", section.getId())
                                            .findFirst();
                Question parent = realmSection.getQuestions().where().equalTo("name", qa[0].trim())
                                              .findFirst();
                if(parent != null){
                    //TODO: Watch this behavior
                    if(!qa[1].toLowerCase().contains(
                            parent.getSelectedAnswerValue().toLowerCase())){
                        return false;
                    }
                }
            } catch(IndexOutOfBoundsException e){
                Log.e("Dependency",
                      "Wrong dependency argument format in question " + question.getName());
            } finally {
                realm.close();
            }
        }
        return true;
    }
    
    private void setSpinnerProperties(AppCompatSpinner spinner, boolean isActive,
                                      Question question){
        List<String> stringAnswers = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(),
                                                          android.R.layout.simple_spinner_item,
                                                          stringAnswers);
        
        spinner.setEnabled(isActive);
        for(Answer answer : question.getAnswers()){
            stringAnswers.add(answer.getAnswer());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(question.getSelectedAnswerId());
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
    
    public static class PhotoQuestionViewHolder extends GenericQuestionViewHolder {
        final Button photoButton;
        
        PhotoQuestionViewHolder(View view){
            super(view);
            photoButton = view.findViewById(R.id.photo_button);
        }
    }
    
    
}
