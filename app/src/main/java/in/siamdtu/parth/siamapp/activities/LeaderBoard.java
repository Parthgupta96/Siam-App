package in.siamdtu.parth.siamapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import in.siamdtu.parth.siamapp.LeaderBoardObject;
import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.listeners.OnGetDataListener;

public class LeaderBoard extends AppCompatActivity {
    DatabaseReference DbRLeaderBoard;
    DatabaseReference DbRMonthlyLeaderBoard;
    RecyclerView recyclerView;
    ArrayList<LeaderBoardObject> leaderBoardArray;
    LeaderBoardRecyclerViewAdapter adapter;
    private OnGetDataListener afterLeaderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        init();
        afterLeaderData = new OnGetDataListener() {
            ProgressDialog progressDialog = new ProgressDialog(LeaderBoard.this);

            @Override
            public void onStart() {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            public void onSuccess() {
                if (recyclerView.getAdapter() == null) {
                    adapter = new LeaderBoardRecyclerViewAdapter(leaderBoardArray);
                    recyclerView.setAdapter(adapter);

                } else {
                    adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }
        };
        getLeaderBoardData(DbRLeaderBoard, afterLeaderData);
        getSupportActionBar().setTitle("Leaderboard");
    }

    private void getLeaderBoardData(DatabaseReference mDbRLeaderBoard, final OnGetDataListener onGetDataListener) {
        onGetDataListener.onStart();
        mDbRLeaderBoard.orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaderBoardArray.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String email = child.getKey().replace(',', '.');
                    LeaderBoardObject temp = child.getValue(LeaderBoardObject.class);
                    leaderBoardArray.add(new LeaderBoardObject(temp.getName(), temp.getScore(), email));
                }
                Collections.reverse(leaderBoardArray);
                onGetDataListener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        FirebaseDatabase  firebaseDatabase =FirebaseDatabase.getInstance();
        DbRLeaderBoard = firebaseDatabase.getReference().child("leaderBoard");
        DbRMonthlyLeaderBoard = firebaseDatabase.getReference().child("monthlyLeaderBoard");
        leaderBoardArray = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.leaderBoard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(LeaderBoard.this));
    }

    private class LeaderBoardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderBoardRecyclerViewAdapter.ViewHolder> {
        ArrayList<LeaderBoardObject> leaderBoardObjects;

        public LeaderBoardRecyclerViewAdapter(ArrayList<LeaderBoardObject> leaderBoardObjects) {
            this.leaderBoardObjects = leaderBoardObjects;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvRank;
            TextView tvName;
            TextView tvPoints;
            TextView tvEmail;

            public ViewHolder(View itemView) {
                super(itemView);
                tvRank = (TextView) itemView.findViewById(R.id.leaderBoard_rank);
                tvName = (TextView) itemView.findViewById(R.id.leaderBoard_name);
                tvPoints = (TextView) itemView.findViewById(R.id.leaderBoard_points);
                tvEmail = (TextView) itemView.findViewById(R.id.leaderBoard_email);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.leader_board_row, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvRank.setText(String.valueOf(position + 1));
            holder.tvPoints.setText(String.valueOf(leaderBoardObjects.get(position).getScore()));
            holder.tvName.setText(leaderBoardObjects.get(position).getName());
            holder.tvEmail.setText(leaderBoardObjects.get(position).getEmail());
        }

        @Override
        public int getItemCount() {
            return leaderBoardObjects.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leaderboard_menu_spinner, menu);
        MenuItem item = menu.findItem(R.id.leaderBoard_menu);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.leaderboard_menu_item, R.layout.leaderboard_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        getLeaderBoardData(DbRLeaderBoard,afterLeaderData);
                        break;
                    case 1:
                        getLeaderBoardData(DbRMonthlyLeaderBoard,afterLeaderData);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
