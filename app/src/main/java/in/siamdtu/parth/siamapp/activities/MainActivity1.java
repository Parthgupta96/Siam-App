package in.siamdtu.parth.siamapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Map;

import in.siamdtu.parth.siamapp.CurrentQuestionService;
import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.UserObject;
import in.siamdtu.parth.siamapp.fragments.ContactUsFragment;
import in.siamdtu.parth.siamapp.fragments.ContestFragment;
import in.siamdtu.parth.siamapp.fragments.EventsFragment;
import in.siamdtu.parth.siamapp.fragments.FacebookFeed;
import in.siamdtu.parth.siamapp.fragments.SponsorsFragment;
import in.siamdtu.parth.siamapp.listeners.OnGetDataListener;

public class MainActivity1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    int lastSelectedID = -1;
    Fragment[] selectedFragment;
    UserObject mCurrentUser;
    NavigationView mNavigationView;
    DrawerLayout drawer;
    DatabaseReference DbRAppVersion;
    private String version;
    private String latestVersion;

    @Override
    protected void onResume() {
        super.onResume();
        if (latestVersion != null && !latestVersion.equals(version)) {
            showUpdateMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        DbRAppVersion = FirebaseDatabase.getInstance().getReference().child("appVersion");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Intent intent = getIntent();
        mCurrentUser = (UserObject) intent.getSerializableExtra("currentUser");

        Bundle bundle = new Bundle();
        bundle.putSerializable("currentUser", mCurrentUser);

        selectedFragment = new Fragment[]{new FacebookFeed(),
                new ContestFragment(),
                new SponsorsFragment(),
                new ContactUsFragment(),
                new EventsFragment(),
                null,
                null};

        for (Fragment fragment : selectedFragment) {
            if (fragment != null) {
                fragment.setArguments(bundle);
            }
        }
//        addAlarmForNotification();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_news_feed);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
//            System.out.print(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Fragment fragment = new FacebookFeed();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment).commit();
        try {
            getSupportActionBar().setTitle("News Feed");
        } catch (Exception e) {
            System.out.println("");
        }
        DbRAppVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latestVersion = dataSnapshot.getValue(String.class);
                if (!version.equals(latestVersion)) {
                    showUpdateMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getCredits(new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                setUpNavigationDrawerHeader();
            }
        });
    }

    private void showUpdateMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity1.this);
        builder.setCancelable(false);
        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }

        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                supportFinishAfterTransition();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Update");
        alertDialog.setMessage("There is a newer version of application available \nUpdate now");
        alertDialog.show();

    }

    private void getCredits(final OnGetDataListener onGetDataListener) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getEmail().replace('.', ','));
        userRef.addValueEventListener(new ValueEventListener() {
            Map<String, Long> map;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                map = (Map<String, Long>) dataSnapshot.getValue();
//                System.out.print("");
                if (map == null) {
                    mCurrentUser.setCredits(0);
                } else {
                    mCurrentUser.setCredits(map.get("credits"));
                }
                onGetDataListener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addAlarmForNotification() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean(getString(R.string.is_alarm_set), false)) {

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.is_alarm_set), true);
            editor.commit();

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent serviceIntent = new Intent(this, CurrentQuestionService.class);
            Calendar currentCalender = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 13);
            calendar.set(Calendar.MINUTE, 7);
            calendar.set(Calendar.SECOND, 0);
            if (currentCalender.getTimeInMillis() > calendar.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            long interval = 2 * AlarmManager.INTERVAL_DAY;
            PendingIntent pendingIntent = PendingIntent.getService(MainActivity1.this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent);
        }
    }

    private void setUpNavigationDrawerHeader() {
        View headerview = mNavigationView.getHeaderView(0);
        LinearLayout navHeader = (LinearLayout) headerview.findViewById(R.id.nav_header);
        ImageView profileImage = (ImageView) headerview.findViewById(R.id.profile_image_nav_header);
        TextView name = (TextView) headerview.findViewById(R.id.name_nav_header);
        TextView credits = (TextView) headerview.findViewById(R.id.credits_nav_header);

        if (mCurrentUser != null) {

            Picasso.with(this)
                    .load(mCurrentUser.getPhotoUrl())
                    .noFade()
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(profileImage);
            name.setText(mCurrentUser.getName());
            credits.setText(String.valueOf(mCurrentUser.getCredits()));

            navHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ProfileFragment fragmentS1 = ProfileFragment.newInstance(mCurrentUser);
                    Intent intent = new Intent(MainActivity1.this, ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("currentUser", mCurrentUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.content_main, fragmentS1)
//                            .addToBackStack("Profile_view")
//                            .commit();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == R.id.action_signOut) {
            signOut();
        } else if (id == R.id.action_rules) {
            Intent intent = new Intent(MainActivity1.this, Rules.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginActivity.mGoogleApiClient.connect();
        LoginActivity.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (LoginActivity.mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(LoginActivity.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                startActivity(new Intent(MainActivity1.this, LoginActivity.class));
                                finish();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int selected = 0;
        if (lastSelectedID != id) {
            if (id == R.id.nav_news_feed) {
                selected = 0;
                getSupportActionBar().setTitle("News Feed");
            } else if (id == R.id.nav_contest) {
                selected = 1;
                getSupportActionBar().setTitle("Contest");
            } else if (id == R.id.nav_sponsors) {
                selected = 2;
                getSupportActionBar().setTitle("Sponsors");
            } else if (id == R.id.nav_contact_us) {
                selected = 3;
                getSupportActionBar().setTitle("Contact us");
            } else if (id == R.id.nav_events) {
                selected = 4;
                getSupportActionBar().setTitle("Events");
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, selectedFragment[selected]).commit();

            lastSelectedID = id;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
