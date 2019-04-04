package com.g12.scoping.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.g12.scoping.R;
import com.g12.scoping.models.Question;
import com.g12.scoping.models.Section;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSectionChangeListener}
 * interface.
 */
public class SectionFragment extends Fragment {
    
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String SECTION = "mSection";
    private int mColumnCount = 1;
    private OnSectionChangeListener mListener;
    private Section mSection;
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SectionFragment(){
    }
    
    public static SectionFragment newInstance(int columnCount, Section section){
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putSerializable(SECTION, section);
        Log.d("Section", section.getName());
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSection = (Section) getArguments().getSerializable(SECTION);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_question_list, container, false);
        
        // Set the adapter
        if(view instanceof RecyclerView){
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if(mColumnCount <= 1){
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new QuestionRecyclerViewAdapter(mSection, mListener));
        }
        return view;
    }
    
    @Override
    public void onPause(){
        //Maybe Async this?
        RecyclerView view = (RecyclerView) this.getView();
        for(int i = 0; i < mSection.getQuestions().size(); i++){
            Question question = mSection.getQuestions().get(i);
            if(question != null && view != null){
                switch(question.getType()){
                    case Question.BOOL:
                    case Question.CHOICE:
                        AppCompatSpinner spinner = view.getChildAt(i).findViewById(R.id.qSpinner);
                        question.setSelected(spinner.getSelectedItemPosition());
                        break;
                    case Question.INPUT_NUMERIC:
                    case Question.INPUT_TEXT:
                        EditText editText = view.getChildAt(i).findViewById(R.id.qAnswer);
                        question.getAnswers().get(question.getSelected()).setAnswer(
                                editText.getText().toString());
                        break;
                }
            }
        }
        mListener.onSectionChange(mSection);
        super.onPause();
    }
    
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OnSectionChangeListener){
            mListener = (OnSectionChangeListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }
    
    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }
    
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSectionChangeListener {
        void onSectionChange(Section section);
    }
}
