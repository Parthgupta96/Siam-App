package in.siamdtu.parth.siamapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.siamdtu.parth.siamapp.AttemptedByUserObject;
import in.siamdtu.parth.siamapp.ContestRecyclerViewAdapter;
import in.siamdtu.parth.siamapp.QuestionDisplayObject;
import in.siamdtu.parth.siamapp.QuestionObject;
import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.UserObject;
import in.siamdtu.parth.siamapp.activities.LeaderBoard;
import in.siamdtu.parth.siamapp.utils.Utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ContestFragment extends Fragment {
    private boolean isCurrentQuestionExpended = false;
    private View mRoot;
    RecyclerView mContestRecyclerView;
    ContestRecyclerViewAdapter mAdapter;
    List<QuestionObject> mQuestions;
    int winnersOfCurrentQuestion;
    int currentQuestionNumber;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionsDatabaseRef;
    private DatabaseReference mPastQuestionDatabaseRef;
    private DatabaseReference mUsersDatabaseRef;
    private DatabaseReference mRootRef;
    private DatabaseReference mIsActive;
    public UserObject mCurrentUser;
    private Context mContext;
    private int mCurrentQuesNum;
    private QuestionObject mCurrentQuestion;
    private List<QuestionDisplayObject> mPastQuestions;
    private CardView mCurrentQuesCardViewWrapper;
    private CardView mCurrentQuesCardView;
    private LinearLayout mAnswersExpandable;
    private DatabaseReference mQuestionsAttemptedByRef;
    private DatabaseReference mQuestionsWinnerRef;
    private DatabaseReference mDatabaseUsersRef;
    private DatabaseReference sponsorRef;
    private boolean isMcq = false;
    private boolean isQuesAttemptedByCurrentUser = false;
    private ProgressDialog mProgress;
    private TextView tvSponsorName;
    TextView noPastQuestions;
    TextView questionNum;
    TextView date;
    TextView question;
    ImageView questionImage;
    TextView attempteText;
    RadioGroup answerOptionsRadioGroup;
    RadioButton option1, option2, option3, option4;
    EditText subjectiveAnswer;
    Button submitBt;
    LinearLayout questionView;
    ImageView noInternet;
    private boolean isContestActive;
    private TextView notActive;
    private FloatingActionButton btLeaderBoard;

    public ContestFragment() {
        // Required empty public constructor
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
        questionView = (LinearLayout) mRoot.findViewById(R.id.contest_Questions);
        noInternet = (ImageView) mRoot.findViewById(R.id.contest_no_internet);
        if (!Utils.isInternetConnected(mContext)) {
            questionView.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
        } else {//internet Connected
            init();
            final OnGetDataListener updateAfterQuesNumber = new OnGetDataListener() {
                @Override
                public void onStart() {
                    mProgress.setMessage("Loading...");
                    mProgress.show();
                }

                @Override
                public void onSuccess() {
                    mProgress.dismiss();
                    updateUI(new OnGetDataListener() {//get all Questions
                        @Override
                        public void onStart() {
                            mProgress.setMessage("Loading...");
                            mProgress.setCancelable(false);
                            mProgress.show();

                        }

                        @Override
                        public void onSuccess() {
                            if (mContestRecyclerView.getAdapter() == null) {
                                mAdapter = new ContestRecyclerViewAdapter(mPastQuestions, getContext(), mCurrentUser);
                                mContestRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                if (mPastQuestions.size() == 0) {
                                    noPastQuestions.setVisibility(View.VISIBLE);
                                }
                            } else {
                                noPastQuestions.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                            }
                            populateCurrentQues();
                            mProgress.dismiss();
                        }
                    });
                }
            };

            OnGetDataListener getDataAfterISActive = new OnGetDataListener() {
                @Override
                public void onStart() {
                    mProgress.setMessage("Loading...");
                    mProgress.show();
                    mProgress.setCancelable(false);

                }

                @Override
                public void onSuccess() {
                    if (isContestActive) {
                        getCurrentQuestionNumber(updateAfterQuesNumber);//function call starts here
                        getSponsorName();
                        notActive.setVisibility(View.GONE);
                        questionView.setVisibility(View.VISIBLE);
                        mProgress.dismiss();
                    } else {
                        notActive.setVisibility(View.VISIBLE);
                        questionView.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Contest not available right now !!", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }
            };
            getIsActive(getDataAfterISActive);
            mPastQuestions = new ArrayList<>();

            mContestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        }
//        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mInitialTmeStamp = (Long) dataSnapshot.child("InitialTime").getValue();
//                mInitialDate = new Date(mInitialTmeStamp);
//                GetCurrentTimeFromFirebase();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        return mRoot;
    }

    private void getIsActive(final OnGetDataListener getDataAfterISActive) {
        getDataAfterISActive.onStart();
        mIsActive = mRootRef.child("isActive");
        mIsActive.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    isContestActive = dataSnapshot.getValue(Long.class) == 1;
                } catch (Exception e) {
                    Utils.makeToast("isActive has Wrong Data Type", mContext);
                } finally {
                    getDataAfterISActive.onSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getDataAfterISActive.onSuccess();
            }
        });
    }

    private void getSponsorName() {
        sponsorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String sponsor = dataSnapshot.getValue(String.class);
                    tvSponsorName.setText(sponsor);
                } catch (Exception e) {
                    Utils.makeToast("Sponsor Name Not String", mContext);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.makeToast("Try Again", mContext);
            }
        });
    }

    private void getCurrentQuestionNumber(final OnGetDataListener onGetDataListener) {
        onGetDataListener.onStart();
        DatabaseReference currentQuestionRef = FirebaseDatabase.getInstance().getReference().child("current");
        currentQuestionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    currentQuestionNumber = dataSnapshot.getValue(Integer.class);
//                    if (mCurrentUser.getAttemptedQuestions().size() == currentQuestionNumber + 1) {
//                        //current question is attempted
//                        mCurrentUser.attemptedQuestions.remove(currentQuestionNumber);
//                    }
                } catch (Exception e) {
                    Utils.makeToast("Current Question no. has Problem", mContext);
                } finally {
                    onGetDataListener.onSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        mContestRecyclerView = (RecyclerView) mRoot.findViewById(R.id.contest_questions_recycler_view);
        tvSponsorName = (TextView) mRoot.findViewById(R.id.contest_sponsor_name);
        notActive = (TextView) mRoot.findViewById(R.id.not_active);
        btLeaderBoard = (FloatingActionButton) mRoot.findViewById(R.id.leaderBoard);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mQuestionsDatabaseRef = mFirebaseDatabase.getReference().child("questions");
        mCurrentQuesCardView = (CardView) mRoot.findViewById(R.id.current_ques_cardview);
        mCurrentQuesCardViewWrapper = (CardView) mRoot.findViewById(R.id.current_ques_cardview_wrapper);
        mAnswersExpandable = (LinearLayout) mRoot.findViewById(R.id.answer_options_expandable_current_ques);
        mQuestionsAttemptedByRef = mFirebaseDatabase.getReference().child("questions");
        if (mCurrentUser != null) {
            mDatabaseUsersRef = mFirebaseDatabase.getReference().child("Users").child(mCurrentUser.getEmail().replace(".", ","));
        }
        sponsorRef = mRootRef.child("sponsor");
        noPastQuestions = (TextView) mRoot.findViewById(R.id.tv_no_past_questions);
        mProgress = new ProgressDialog(getActivity());

        mProgress.setMessage("Loading...");
        mProgress.show();

        questionNum = (TextView) mRoot.findViewById(R.id.question_no);
        date = (TextView) mRoot.findViewById(R.id.date);
        question = (TextView) mRoot.findViewById(R.id.question);
        attempteText = (TextView) mRoot.findViewById(R.id.tv_contest_current_question_attempted);
        questionImage = (ImageView) mRoot.findViewById(R.id.question_image);
        answerOptionsRadioGroup = (RadioGroup) mRoot.findViewById(R.id.options_radio_group);
        option1 = (RadioButton) mRoot.findViewById(R.id.option1);
        option2 = (RadioButton) mRoot.findViewById(R.id.option2);
        option3 = (RadioButton) mRoot.findViewById(R.id.option3);
        option4 = (RadioButton) mRoot.findViewById(R.id.option4);
        subjectiveAnswer = (EditText) mRoot.findViewById(R.id.subjective_answer);
        submitBt = (Button) mRoot.findViewById(R.id.submit_bt);

        btLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeaderBoard.class);
                startActivity(intent);
            }
        });

        mCurrentQuesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswersExpandable.setVisibility(isCurrentQuestionExpended ? View.GONE : View.VISIBLE);
                if (!isCurrentQuestionExpended) {
                    TransitionManager.beginDelayedTransition(mCurrentQuesCardView);
                }
                isCurrentQuestionExpended = !isCurrentQuestionExpended;
            }
        });

    }


    //Getting all Questions Till Current Question
    private void updateUI(final OnGetDataListener onGetDataListener) {
        onGetDataListener.onStart();

        final OnGetDataListener currentQuestionCompleted = new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                onGetDataListener.onSuccess();
            }
        };

        OnGetDataListener pastQuestionsCompleted = new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                getCurrentQuestion(currentQuestionCompleted);
            }
        };

        getPastQuestions(pastQuestionsCompleted);

    }

    private void getPastQuestions(final OnGetDataListener pastQuestionsCompleted) {
        mPastQuestionDatabaseRef = mRootRef.child("questiondisplay");
        mPastQuestionDatabaseRef.limitToFirst(currentQuestionNumber)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mPastQuestions.clear();
                        try {
                            for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                                QuestionDisplayObject questionObject = questionsSnapshot
                                        .getValue(QuestionDisplayObject.class);
                                mPastQuestions.add(questionObject);
                            }

                            mCurrentQuesNum = currentQuestionNumber;//what is this
                            Collections.reverse(mPastQuestions);
                        } catch (Exception e) {
                            Utils.makeToast("Problem in Previous Questions", mContext);

                        } finally {
                            pastQuestionsCompleted.onSuccess();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgress.dismiss();
                    }
                });

    }

    private void getCurrentQuestion(final OnGetDataListener currentQuestionCompleted) {
        mQuestionsDatabaseRef.child(String.valueOf(currentQuestionNumber))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            QuestionObject questionObject = dataSnapshot.getValue(QuestionObject.class);
                            mCurrentQuestion = questionObject;
//                mCurrentQuestion = mPastQuestions.remove(size - 1);
                        } catch (Exception e) {
                            Utils.makeToast("Problem in Current Question", mContext);
                        } finally {
                            currentQuestionCompleted.onSuccess();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //Getting Today's Question
    private void populateCurrentQues() {

        if (mCurrentQuestion != null) {

            date.setText(mCurrentQuestion.getDate());
            question.setText(mCurrentQuestion.getQuestion());
            if (!mCurrentQuestion.getImageUrl().equalsIgnoreCase("null")) {                 //image provided
                Picasso.with(mContext)
                        .load(mCurrentQuestion.getImageUrl())
                        .placeholder(R.drawable.siam1)
                        .into(questionImage);
                questionImage.setVisibility(View.VISIBLE);
            } else {
                questionImage.setVisibility(View.GONE);

            }
            isMcq = (mCurrentQuestion.getAnswerType()).equals(Integer.toString(QuestionObject.MCQ));
            if (isMcq) { // mcq question
                answerOptionsRadioGroup.setVisibility(View.VISIBLE);
                subjectiveAnswer.setVisibility(View.GONE);
                List<String> options = mCurrentQuestion.getOptions();
                option1.setText(options.get(0));
                option2.setText(options.get(1));
                option3.setText(options.get(2));
                option4.setText(options.get(3));
            } else {                                                              // subjective question
                subjectiveAnswer.setVisibility(View.VISIBLE);
                answerOptionsRadioGroup.setVisibility(View.GONE);
            }

            for (int i = 0; i < mCurrentQuestion.getNumberOfAttempts(); i++) {
                AttemptedByUserObject mTempObject = mCurrentQuestion.getAttemptedBy().get(i);
                if (mTempObject != null) {
                    if (mTempObject.getEmail().equals(mCurrentUser.getEmail())) {
                        isQuesAttemptedByCurrentUser = true;
                        break;
                    }
                }
            }
        }
        if (isQuesAttemptedByCurrentUser) {
            submitBt.setEnabled(false);
            subjectiveAnswer.setFocusable(false);
            submitBt.setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_bg_submitted));
            attempteText.setVisibility(View.VISIBLE);
        } else {
            submitBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    attempteText.setVisibility(View.VISIBLE);
                    final ProgressDialog submitProgress = new ProgressDialog(getContext());
                    submitProgress.setMessage("Submitting...");
                    submitProgress.show();
                    submitProgress.setCancelable(false);
                    Toast.makeText(mContext, "Response Submitted", Toast.LENGTH_SHORT).show();

//                    mQuestionsWinnerRef = mQuestionsAttemptedByRef.child(mCurrentQuesNum + "").child("winnerEmail");
                    mQuestionsAttemptedByRef = mQuestionsAttemptedByRef.child(String.valueOf(mCurrentQuesNum))
                            .child("attemptedBy").child(String.valueOf(mCurrentQuestion.getNumberOfAttempts()));
                    DatabaseReference userAttemptedQuestions = mDatabaseUsersRef.child("attemptedQuestions")
                            .child(String.valueOf(mCurrentQuesNum));
                    String answer = "";
                    if (isMcq) {
                        int selectedButtonId = answerOptionsRadioGroup.getCheckedRadioButtonId();

                        switch (selectedButtonId) {
                            case R.id.option1:
                                answer = mCurrentQuestion.getOptions().get(0);
                                break;
                            case R.id.option2:
                                answer = mCurrentQuestion.getOptions().get(1);
                                break;
                            case R.id.option3:
                                answer = mCurrentQuestion.getOptions().get(2);
                                break;
                            case R.id.option4:
                                answer = mCurrentQuestion.getOptions().get(3);
                                break;
                        }
                        answer = answer.substring(3);
                    } else {
                        answer = subjectiveAnswer.getText().toString();
                    }
                    //writing in Question DB
                    mQuestionsAttemptedByRef.child("email").setValue(mCurrentUser.getEmail());
                    mQuestionsAttemptedByRef.child("answer").setValue(answer);
//                    mQuestionsAttemptedByRef.child("submissionTime").setValue(ServerValue.TIMESTAMP);

                    //writing in User DB
                    userAttemptedQuestions.child("answer").setValue(answer);
                    userAttemptedQuestions.child("submissionTime").setValue(ServerValue.TIMESTAMP);
//
//                    boolean isCorrect = false;
//                    if (isMcq) {
//                        isCorrect = answer.equalsIgnoreCase((mCurrentQuestion.getOptions()
//                                .get(Integer.parseInt(mCurrentQuestion.getAnswer()) - 1)).substring(3));
//                    } else {
//                        isCorrect = answer.equalsIgnoreCase(mCurrentQuestion.getAnswer());
//                    }
//                    if (isCorrect) {
//                        mQuestionsAttemptedByRef.child("status").setValue("correct");
//                        userAttemptedQuestions.child("status").setValue("correct");
//                        addDataToWinner(new OnGetDataListener() {
//                            @Override
//                            public void onStart() {
//
//                            }
//
//                            //  after getting count of winners till now
//                            @Override
//                            public void onSuccess() {
//
//                                mQuestionsWinnerRef.child(String.valueOf(winnersOfCurrentQuestion))
//                                        .child("email").setValue(mCurrentUser.getEmail());
////                                    int credits = Integer.parseInt(mCurrentUser.getCredits());
//                                if (winnersOfCurrentQuestion < 20) {
//                                    mDatabaseUsersRef.child("todays credits")
//                                            .setValue(mCurrentQuestion.getCredits());
//                                } else {
//                                    mDatabaseUsersRef.child("todays credits").setValue(0);
//                                }
//                                submitProgress.dismiss();
//                            }
//                        });
//                    } else {// if answer is wrong
//                        mQuestionsAttemptedByRef.child("status").setValue("wrong");
//                        userAttemptedQuestions.child("status").setValue("wrong");
//                        submitProgress.dismiss();
//                    }
                    subjectiveAnswer.setFocusable(false);
                    submitProgress.dismiss();
                    submitBt.setEnabled(false);
                    try  {
                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(submitBt.getWindowToken(), 0);
                    } catch (Exception e) {
                        //unable to hide keyBoard
                    }
                }
            });
        }

    }
//
//    private void addDataToWinner(final OnGetDataListener onGetDataListener) {
////        winnersOfCurrentQuestion;
//        mQuestionsWinnerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                winnersOfCurrentQuestion = (int) dataSnapshot.getChildrenCount();
//
//                onGetDataListener.onSuccess();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public interface OnGetDataListener {
        public void onStart();

        public void onSuccess();
    }
}