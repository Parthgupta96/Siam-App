package com.example.parth.siamapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Priyanshu on 08-01-2017.
 */

public class ContestRecyclerViewAdapter extends RecyclerView.Adapter<ContestRecyclerViewAdapter.ViewHolder> {

    View mView;
    List<QuestionObject> mQuestions;
    Context mContext;
    int mExpandedPosition = -1;
    ViewGroup mParent;

    public ContestRecyclerViewAdapter(List<QuestionObject> questions, Context context){
        mQuestions = questions;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mParent = parent;
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_recyclerview_row,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.answerExpandable.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(mParent);
                notifyDataSetChanged();
            }
        });
        holder.date.setText(mQuestions.get(position).getDate());
        holder.question.setText(mQuestions.get(position).getQuestion());
        if(mQuestions.get(position).getImageUrl() != null){                 //image provided
            Picasso.with(mContext).load(mQuestions.get(position).getImageUrl())
                    .placeholder(R.drawable.avatar).into(holder.questionImage);
        }
        if((mQuestions.get(position).getAnswerType()).equals("0")){ // mcq question
            holder.answerOptionsRadioGroup.setVisibility(View.VISIBLE);
            holder.subjectiveAnswer.setVisibility(View.INVISIBLE);
            List<String> options = mQuestions.get(position).getOptions();
            holder.option1.setText(options.get(0));
            holder.option2.setText(options.get(1));
            holder.option3.setText(options.get(2));
            holder.option4.setText(options.get(3));
        }else{                                                              // subjective question
            holder.subjectiveAnswer.setVisibility(View.VISIBLE);
            holder.answerOptionsRadioGroup.setVisibility(View.INVISIBLE);
        }
        holder.submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement submit button. change DB for USERS as well as question.
            }
        });


    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionNum;
        TextView date;
        TextView question;
        ImageView questionImage;
        RadioGroup answerOptionsRadioGroup;
        RadioButton option1, option2, option3, option4;
        EditText subjectiveAnswer;
        Button submitBt;
        LinearLayout answerExpandable;
        public ViewHolder(View itemView) {
            super(itemView);
            questionNum = (TextView)itemView.findViewById(R.id.question_no);
            date = (TextView)itemView.findViewById(R.id.date);
            question = (TextView)itemView.findViewById(R.id.question);
            questionImage = (ImageView)itemView.findViewById(R.id.question_image);
            answerOptionsRadioGroup = (RadioGroup)itemView.findViewById(R.id.options_radio_group);
            option1 = (RadioButton)itemView.findViewById(R.id.option1);
            option2 = (RadioButton)itemView.findViewById(R.id.option2);
            option3 = (RadioButton)itemView.findViewById(R.id.option3);
            option4 = (RadioButton)itemView.findViewById(R.id.option4);
            subjectiveAnswer = (EditText)itemView.findViewById(R.id.subjective_answer);
            submitBt = (Button)itemView.findViewById(R.id.submit_bt);
            answerExpandable = (LinearLayout)itemView.findViewById(R.id.answer_options_expandable);
        }
    }
}
