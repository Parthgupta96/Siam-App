package com.example.parth.siamapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.squareup.picasso.Picasso;

import static com.example.parth.siamapp.LoginActivity.mGoogleApiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int lastSelectedID = R.id.nav_news_feed;

    UserObject mCurrentUser;
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        mCurrentUser = (UserObject)intent.getSerializableExtra("currentUser");

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_news_feed);
        Fragment fragment = new FacebookFeed();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment).commit();
        setUpNavigationDrawerHeader();
    }

    private void setUpNavigationDrawerHeader() {
        View headerview = mNavigationView.getHeaderView(0);
        LinearLayout navHeader = (LinearLayout) headerview.findViewById(R.id.nav_header);
        ImageView profileImage = (ImageView)headerview.findViewById(R.id.profile_image_nav_header);
        TextView name = (TextView)headerview.findViewById(R.id.name_nav_header);
        Picasso.with(this).load(mCurrentUser.getPhotoUrl()).noFade().placeholder(R.drawable.avatar)
                .error(R.drawable.avatar).into(profileImage);
        name.setText(mCurrentUser.getName());
        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment fragmentS1 = ProfileFragment.newInstance(mCurrentUser);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentS1).commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_signOut) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
        Fragment fragment= new FacebookFeed();;
        if(lastSelectedID!=id) {
            if (id == R.id.nav_news_feed) {
                fragment = new FacebookFeed();
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_main, fragment).commit();

            } else if (id == R.id.nav_contest) {
                fragment = new ContestFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentS1).commit();

            } else if (id == R.id.nav_sponsors) {

            } else if (id == R.id.nav_contact_us) {

            } else if (id == R.id.nav_settings) {

            } else if (id == R.id.nav_rate_us) {

            } else if (id == R.id.nav_events) {
                 fragment = new EventsFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentS1).commit();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

            lastSelectedID = id;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
