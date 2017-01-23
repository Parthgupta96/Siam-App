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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionsAttemptedByRef;
    private DatabaseReference mDatabaseUsersRef;
    private boolean isQuesAttemptedByCurrentUser = false;
    private AttemptedByUserObject mTempObject;
    UserObject mCurrentUser;

    public ContestRecyclerViewAdapter(List<QuestionObject> questions, Context context, UserObject mCurrentUser) {
        mQuestions = questions;
        mContext = context;
        this.mCurrentUser = mCurrentUser;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuestionsAttemptedByRef = mFirebaseDatabase.getReference().child("Questions");
        mDatabaseUsersRef = mFirebaseDatabase.getReference().child("Users").child(mCurrentUser.getEmail().replace(".", ","));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mParent = parent;
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_recyclerview_row, parent, false);
        return new ViewHolder(mView);
    }

    boolean isMcq;

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        isMcq = false;
        final boolean isExpanded = position == mExpandedPosition;
        holder.answerExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                TransitionManager.beginDelayedTransition(mParent);
//                notifyDataSetChanged();
            }
        });
        holder.date.setText(mQuestions.get(position).getDate());
        holder.question.setText(mQuestions.get(position).getQuestion());
        if (!mQuestions.get(position).getImageUrl().equalsIgnoreCase("null")) {                 //image provided
            Picasso.with(mContext).load(mQuestions.get(position).getImageUrl())
                    .placeholder(R.drawable.avatar).into(holder.questionImage);
        }
        if ((mQuestions.get(position).getAnswerType()).equals("0")) { // mcq question
            isMcq = true;
            holder.answerOptionsRadioGroup.setVisibility(View.VISIBLE);
            holder.subjectiveAnswer.setVisibility(View.INVISIBLE);
            List<String> options = mQuestions.get(position).getOptions();
            holder.option1.setText(options.get(0));
            holder.option2.setText(options.get(1));
            holder.option3.setText(options.get(2));
            holder.option4.setText(options.get(3));
        } else {                                                              // subjective question
            isMcq = false;
            holder.subjectiveAnswer.setVisibility(View.VISIBLE);
            holder.answerOptionsRadioGroup.setVisibility(View.INVISIBLE);
        }
        holder.submitBt.setVisibility(View.GONE);
        holder.correctAnswer.setText(mQuestions.get(position).getAnsweer());
        for (int i = 0; i < mQuestions.get(position).getNumberOfAttempts(); i++) {
            mTempObject = mQuestions.get(position).getAttemptedByUserObject().get(i);
            if (mTempObject.getEmail().equals(mCurrentUser.getEmail())) {
                isQuesAttemptedByCurrentUser = true;
                holder.userAnswer.setText(mTempObject.getAnswer());
                break;
            } else {
                holder.userAnswer.setText("Not Attempted");
            }
        }
    }
//        holder.submitBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //TODO implement submit button. change DB for USERS as well as question.
//                mQuestionsAttemptedByRef = mQuestionsAttemptedByRef.child(mQuestions.get(position) + "").child("attemptedBy").child(ContestFragment.mCurrentUser.getEmail().replace(".", ","));
//                mDatabaseUsersRef = mDatabaseUsersRef.child("attemptedQuestions").child(mQuestions.get(position) + "");
//                String answer = "";
//                if(isMcq == true) {
//                    int selsectedButtonId = holder.answerOptionsRadioGroup.getCheckedRadioButtonId();
//                    switch (selsectedButtonId){
//                        case R.id.option1: answer = holder.option1.getText().toString();
//                            break;
//                        case R.id.option2: answer = holder.option2.getText().toString();
//                            break;
//                        case R.id.option3: answer = holder.option3.getText().toString();
//                            break;
//                        case R.id.option4: answer = holder.option4.getText().toString();
//                            break;
//                    }
//                    mQuestionsAttemptedByRef.child("answer").setValue(answer);
//                    mDatabaseUsersRef.child("answer").setValue(answer);
//                }else{
//                    mQuestionsAttemptedByRef.child("answer").setValue(holder.subjectiveAnswer.getText().toString());
//                    mDatabaseUsersRef.child("answer").setValue(holder.subjectiveAnswer.getText().toString());
//                }
//                mQuestionsAttemptedByRef.child("submittionTime").setValue(ServerValue.TIMESTAMP);
//                mDatabaseUsersRef.child("submittionTime").setValue(ServerValue.TIMESTAMP);
//                if(answer == mQuestions.get(position).getAnsweer()){
//                    mQuestionsAttemptedByRef.child("status").setValue("correct");
//                    mDatabaseUsersRef.child("status").setValue("correct");
//                }else{
//                    mQuestionsAttemptedByRef.child("status").setValue("wrong");
//                    mDatabaseUsersRef.child("status").setValue("wrong");
//                }
//            }
//        });
//
//}

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
        TextView userAnswer, correctAnswer;

        public ViewHolder(View itemView) {
            super(itemView);
            questionNum = (TextView) itemView.findViewById(R.id.question_no);
            date = (TextView) itemView.findViewById(R.id.date);
            question = (TextView) itemView.findViewById(R.id.question);
            userAnswer = (TextView) itemView.findViewById(R.id.user_answer);
            correctAnswer = (TextView) itemView.findViewById(R.id.correct_answer);
            questionImage = (ImageView) itemView.findViewById(R.id.question_image);
            answerOptionsRadioGroup = (RadioGroup) itemView.findViewById(R.id.options_radio_group);
            option1 = (RadioButton) itemView.findViewById(R.id.option1);
            option2 = (RadioButton) itemView.findViewById(R.id.option2);
            option3 = (RadioButton) itemView.findViewById(R.id.option3);
            option4 = (RadioButton) itemView.findViewById(R.id.option4);
            subjectiveAnswer = (EditText) itemView.findViewById(R.id.subjective_answer);
            submitBt = (Button) itemView.findViewById(R.id.submit_bt);
            answerExpandable = (LinearLayout) itemView.findViewById(R.id.answer_options_expandable);
        }
    }
}
