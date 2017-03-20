package in.siamdtu.parth.siamapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import in.siamdtu.parth.siamapp.LeaderBoardObject;
import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.UserObject;
import in.siamdtu.parth.siamapp.listeners.OnGetDataListener;
import in.siamdtu.parth.siamapp.utils.Utils;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;
    private SignInButton login;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRefUsers;
    private DatabaseReference root;
    private FirebaseUser mUser;
    private UserObject mCurrentUser;
    private ProgressDialog mProgreeDialog;
    private boolean needToSignOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.google_auth_key_siam))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefUsers = mFirebaseDatabase.getReference().child("Users");
        root = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in

                    OnGetDataListener afterInitialize = new OnGetDataListener() {
                        @Override
                        public void onStart() {
                            mProgreeDialog = new ProgressDialog(LoginActivity.this);
                            mProgreeDialog.setMessage("Loading...");
                            mProgreeDialog.show();
                            mProgreeDialog.setCancelable(false);
                        }

                        @Override
                        public void onSuccess() {
                            if (!needToSignOut) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                                intent.putExtra("currentUser", mCurrentUser);
//                                supportFinishAfterTransition();

                                finish();
                                startActivity(intent);//only code to reach next activity
                            }
                        }
                    };
                    initializeSignIn(afterInitialize);

                } else {
                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    private void initializeSignIn(final OnGetDataListener onGetDataListener) {
        onGetDataListener.onStart();
        mCurrentUser = new UserObject();
        mCurrentUser.setEmail(mUser.getEmail());
        mCurrentUser.setName(mUser.getDisplayName());
        mCurrentUser.setPhotoUrl(mUser.getPhotoUrl().toString());
        mCurrentUser.setUid(mUser.getUid());
//        Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
//        Toast.makeText(getApplicationContext(), "signed in successfully", Toast.LENGTH_SHORT).show();
        OnGetDataListener getDataFromFireBase = new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                onGetDataListener.onSuccess();
            }
        };
        checkFirebaseDatabase(getDataFromFireBase);

    }

    private void checkFirebaseDatabase(final OnGetDataListener dataFromFirebase) {
//        mProgreeDialog.setMessage("Please Wait...");
//        mProgreeDialog.show();
//        mRefUsers.child()
        mRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mCurrentUser.getEmail() != null) {
                    String email = mCurrentUser.getEmail().replace('.', ',');
                    if (!dataSnapshot.hasChild(email)) {
//                    mProgreeDialog.dismiss();
                        mCurrentUser.setCredits(0);
                        mRefUsers.child(email).setValue(mCurrentUser);
                        LeaderBoardObject leaderBoard = new LeaderBoardObject(mCurrentUser.getName(), 0);
                        root.child("leaderBoard").child(mCurrentUser.getEmail()
                                .replace('.', ',')).setValue(leaderBoard);
                        root.child("monthlyLeaderBoard").child(mCurrentUser.getEmail()
                                .replace('.', ',')).setValue(leaderBoard);
                        dataFromFirebase.onSuccess();
                    } else {
//                    mProgreeDialog.dismiss();
                        Iterable<DataSnapshot> allChildren = dataSnapshot.getChildren();
                        for (DataSnapshot child : allChildren) {
//                            System.out.println("");
                            if (child.getKey().equals(email)) {
                                try {
                                    mCurrentUser = child.getValue(UserObject.class);

                                } catch (Exception e) {
                                    Utils.makeToast("Some Error Occurred ",LoginActivity.this);
                                }
                                break;
                            }
                        }
                        dataFromFirebase.onSuccess();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //fail the signin and signout the user
                signOut();
                needToSignOut = true;
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(getApplicationContext(), "Some error occured. Please try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        login = (SignInButton) findViewById(R.id.sign_in_button);
        login.setSize(SignInButton.SIZE_STANDARD);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            supportFinishAfterTransition();
//            startActivity(intent);

            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Sign in failed by google", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            supportFinishAfterTransition();
//            startActivity(intent);

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if (mCurrentUser == null) {
            System.out.println("  \n\n");
        }
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
//
//                        Toast.makeText(getApplicationContext(), "signed in successfully 2", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                        supportFinishAfterTransition();
//                        startActivity(intent);
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed. Please Retry", Toast.LENGTH_SHORT).show();
    }
}
