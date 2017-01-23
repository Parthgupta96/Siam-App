package com.example.parth.siamapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.parth.siamapp.AppController.mContext;

public class ContestFragment extends Fragment {

    private View mRoot;
    RecyclerView mContestRecyclerView;
    ContestRecyclerViewAdapter mAdapter;
    List<QuestionObject> mQuestions;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionsDatabaseRef;
    private DatabaseReference mUsersDatabaseRef;
    private DatabaseReference mRootRef;
    public UserObject mCurrentUser;
    private Long mTimeStamp;
    private Long mInitialTmeStamp;
    private SimpleDateFormat mDateFormat;
    private Date mCurrentDate;
    private Date mInitialDate;
    private Long mDiffInMs;
    private int mNumOfDays;
    private int mCurrentQuesNum;
    private QuestionObject mCurrentQuestion;
    private List<QuestionObject> mPastQuestions;
    private CardView mCurrentQuesCardView;
    private LinearLayout mAnswersExpandable;
    private DatabaseReference mQuestionsAttemptedByRef;
    private DatabaseReference mDatabaseUsersRef;
    private boolean isMcq = false;
    private boolean isQuesAttemptedByCurrentUser = false;
    private ProgressDialog mProgress;

    public ContestFragment() {
        // Required empty public constructor
    }

    public static ContestFragment newInstance(UserObject currentUser) {
        ContestFragment fragment = new ContestFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("currentUser", currentUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentUser = (UserObject) getArguments().getSerializable("currentUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRoot = inflater.inflate(R.layout.fragment_contest, container, false);
        if (getArguments() != null && mCurrentUser == null) {
            mCurrentUser = (UserObject) getArguments().getSerializable("currentUser");
        }
        mContestRecyclerView = (RecyclerView) mRoot.findViewById(R.id.contest_questions_recycler_view);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mQuestionsDatabaseRef = mFirebaseDatabase.getReference().child("questions");
        mUsersDatabaseRef = mFirebaseDatabase.getReference().child("Users").child("priyanshu96.goyal@gmail.com".replace('.', ','));
        mCurrentQuesCardView = (CardView) mRoot.findViewById(R.id.current_ques_cardview);
        mAnswersExpandable = (LinearLayout) mRoot.findViewById(R.id.answer_options_expandable_current_ques);
        mQuestionsAttemptedByRef = mFirebaseDatabase.getReference().child("Questions");
        if (mCurrentUser != null) {
            mDatabaseUsersRef = mFirebaseDatabase.getReference().child("Users").child(mCurrentUser.getEmail().replace(".", ","));
        }
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Loading...");
        mProgress.show();


        mCurrentQuesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(mCurrentQuesCardView);
                mAnswersExpandable.setVisibility(View.VISIBLE);
            }
        });

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mInitialTmeStamp = (Long) dataSnapshot.child("InitialTime").getValue();
                mInitialDate = new Date(mInitialTmeStamp);
                GetCurrentTimeFromFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPastQuestions = new ArrayList<>();

        mContestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mRoot;
    }

    private void GetCurrentTimeFromFirebase() {
        mUsersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTimeStamp = (Long) dataSnapshot.child("Time").getValue();
                mDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                mCurrentDate = new Date(mTimeStamp);
                mDiffInMs = mCurrentDate.getTime() - mInitialDate.getTime();
//                mCurrentDate = mDateFormat.format(new Date(mTimeStamp));
                mNumOfDays = (int) (mDiffInMs / 1000 / 3600 / 24);
                mCurrentQuesNum = (mNumOfDays / 2);
                updateUI(new OnGetDataListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        if (mContestRecyclerView.getAdapter() == null) {
                            mAdapter = new ContestRecyclerViewAdapter(mPastQuestions, getContext(), mCurrentUser);
                            mContestRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUsersDatabaseRef.child("Time").setValue(ServerValue.TIMESTAMP);

    }

    private void updateUI(final OnGetDataListener onGetDataListener) {
        mQuestionsDatabaseRef.limitToFirst(mCurrentQuesNum + 1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPastQuestions = new ArrayList<QuestionObject>();
                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    String key = questionsSnapshot.getKey();
                    if (Integer.valueOf(key) == mCurrentQuesNum) {
                        mCurrentQuestion = questionsSnapshot.getValue(QuestionObject.class);
                        populateCurrentQues();

                    } else {
                        QuestionObject questionObject = questionsSnapshot.getValue(QuestionObject.class);
                        mPastQuestions.add(questionObject);
                    }
                    mProgress.dismiss();
                }
                onGetDataListener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateCurrentQues() {
        TextView questionNum;
        TextView date;
        TextView question;
        ImageView questionImage;
        final RadioGroup answerOptionsRadioGroup;
        final RadioButton option1, option2, option3, option4;
        final EditText subjectiveAnswer;
        Button submitBt;
        questionNum = (TextView) mRoot.findViewById(R.id.question_no);
        date = (TextView) mRoot.findViewById(R.id.date);
        question = (TextView) mRoot.findViewById(R.id.question);
        questionImage = (ImageView) mRoot.findViewById(R.id.question_image);
        answerOptionsRadioGroup = (RadioGroup) mRoot.findViewById(R.id.options_radio_group);
        option1 = (RadioButton) mRoot.findViewById(R.id.option1);
        option2 = (RadioButton) mRoot.findViewById(R.id.option2);
        option3 = (RadioButton) mRoot.findViewById(R.id.option3);
        option4 = (RadioButton) mRoot.findViewById(R.id.option4);
        subjectiveAnswer = (EditText) mRoot.findViewById(R.id.subjective_answer);
        submitBt = (Button) mRoot.findViewById(R.id.submit_bt);

        if (mCurrentQuestion != null) {

            date.setText(mCurrentQuestion.getDate());
            question.setText(mCurrentQuestion.getQuestion());
            if (mCurrentQuestion.getImageUrl() != null) {                 //image provided
                Picasso.with(mContext).load(mCurrentQuestion.getImageUrl())
                        .placeholder(R.drawable.avatar).into(questionImage);
            }
            if ((mCurrentQuestion.getAnswerType()).equals("0")) { // mcq question
                answerOptionsRadioGroup.setVisibility(View.VISIBLE);
                subjectiveAnswer.setVisibility(View.INVISIBLE);
                List<String> options = mCurrentQuestion.getOptions();
                option1.setText(options.get(0));
                option2.setText(options.get(1));
                option3.setText(options.get(2));
                option4.setText(options.get(3));
            } else {                                                              // subjective question
                subjectiveAnswer.setVisibility(View.VISIBLE);
                answerOptionsRadioGroup.setVisibility(View.INVISIBLE);
            }

            for (int i = 0; i < mCurrentQuestion.getNumberOfAttempts(); i++) {
                AttemptedByUserObject mTempObject = mCurrentQuestion.getAttemptedByUserObject().get(i);
                if (mTempObject.getEmail().equals(mCurrentUser.getEmail())) {
                    isQuesAttemptedByCurrentUser = true;
                    break;
                }
            }
        }
        if (isQuesAttemptedByCurrentUser) {
            submitBt.setClickable(false);
        } else {
            submitBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProgress.setMessage("Submitting...");
                    mProgress.show();

                    //TODO implement submit button. change DB for USERS as well as question.
                    mQuestionsAttemptedByRef = mQuestionsAttemptedByRef.child(mCurrentQuesNum + "").child("attemptedBy").child(mCurrentQuestion.getNumberOfAttempts() + "");
                    mDatabaseUsersRef = mDatabaseUsersRef.child("attemptedQuestions").child(mCurrentQuesNum + "");
                    mQuestionsDatabaseRef.child("email").setValue(mCurrentUser.getEmail().replace(".", ","));
                    String answer = "";
                    if (isMcq == true) {
                        int selsectedButtonId = answerOptionsRadioGroup.getCheckedRadioButtonId();
                        switch (selsectedButtonId) {
                            case R.id.option1:
                                answer = option1.getText().toString();
                                break;
                            case R.id.option2:
                                answer = option2.getText().toString();
                                break;
                            case R.id.option3:
                                answer = option3.getText().toString();
                                break;
                            case R.id.option4:
                                answer = option4.getText().toString();
                                break;
                        }
                        mQuestionsAttemptedByRef.child("answer").setValue(answer);
                        mDatabaseUsersRef.child("answer").setValue(answer);
                    } else {
                        mQuestionsAttemptedByRef.child("answer").setValue(subjectiveAnswer.getText().toString());
                        mDatabaseUsersRef.child("answer").setValue(subjectiveAnswer.getText().toString());
                    }
                    mQuestionsAttemptedByRef.child("submittionTime").setValue(ServerValue.TIMESTAMP);
                    mDatabaseUsersRef.child("submittionTime").setValue(ServerValue.TIMESTAMP);
                    if (answer == mCurrentQuestion.getAnsweer()) {
                        mQuestionsAttemptedByRef.child("status").setValue("correct");
                        mDatabaseUsersRef.child("status").setValue("correct");
                    } else {
                        mQuestionsAttemptedByRef.child("status").setValue("wrong");
                        mDatabaseUsersRef.child("status").setValue("wrong");
                    }
                    mProgress.dismiss();
                }
            });
        }

    }

    public interface OnGetDataListener {
        public void onStart();

        public void onSuccess();
    }


}