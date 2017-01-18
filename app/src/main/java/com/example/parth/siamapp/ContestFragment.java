package com.example.parth.siamapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContestFragment extends Fragment {

    RecyclerView mContestRecyclerView;
    ContestRecyclerViewAdapter mAdapter;
    List<QuestionObject> mQuestions;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionsDatabaseRef;
    private DatabaseReference mUsersDatabaseRef;
    private DatabaseReference mRootRef;
    private UserObject mCurrentUser;
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
        View root = inflater.inflate(R.layout.fragment_contest, container, false);

        mContestRecyclerView = (RecyclerView)root.findViewById(R.id.contest_questions_recycler_view);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mQuestionsDatabaseRef = mFirebaseDatabase.getReference().child("questions");
        mUsersDatabaseRef = mFirebaseDatabase.getReference().child("Users").child("priyanshu96.goyal@gmail.com".replace('.', ','));

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mInitialTmeStamp = (Long)dataSnapshot.child("InitialTime").getValue();
                mInitialDate = new Date(mInitialTmeStamp);
                GetCurrentTimeFromFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        QuestionObject obj1 = new QuestionObject();
//        obj1.name = "abc";
        mQuestions = new ArrayList<>();
        mQuestions.add(obj1);
        mQuestions.add(obj1);

        mAdapter = new ContestRecyclerViewAdapter(mQuestions, getContext());
        mContestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mContestRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return root;
    }

    private void GetCurrentTimeFromFirebase() {
        mUsersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTimeStamp = (Long)dataSnapshot.child("Time").getValue();
                mDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                mCurrentDate = new Date(mTimeStamp);
                mDiffInMs = mCurrentDate.getTime() - mInitialDate.getTime();
//                mCurrentDate = mDateFormat.format(new Date(mTimeStamp));
                mNumOfDays = (int) (mDiffInMs/1000/3600/24);
                mCurrentQuesNum = (mNumOfDays/2);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUsersDatabaseRef.child("Time").setValue(ServerValue.TIMESTAMP);

    }

    private void updateUI() {
        mQuestionsDatabaseRef.limitToFirst(mCurrentQuesNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot questionsSnapshot: dataSnapshot.getChildren()){
                    String key = questionsSnapshot.getKey();
                    if(Integer.valueOf(key) == mCurrentQuesNum){
                        mCurrentQuestion = (QuestionObject)questionsSnapshot.getValue();

                    }else{
                        QuestionObject questionObject = (QuestionObject)questionsSnapshot.getValue();
                        mPastQuestions.add(questionObject);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}