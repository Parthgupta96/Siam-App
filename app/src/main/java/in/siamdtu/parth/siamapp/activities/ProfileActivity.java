package in.siamdtu.parth.siamapp.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.UserObject;

public class ProfileActivity extends AppCompatActivity {

    UserObject mCurrentUser;
    ImageView mProfileImage;
    TextView mName;
    TextView mEmail;
    TextView credits;
    Button btRedeem;


    DatabaseReference redeem;
    DatabaseReference userCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            getSupportActionBar().setTitle("Profile");

        }catch (Exception e){
            //wohooo catched
        }
        setContentView(R.layout.fragment_profile);
        Bundle bundle = getIntent().getExtras();
        mCurrentUser = (UserObject) bundle.get("currentUser");
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        credits = (TextView) findViewById(R.id.credits);
        btRedeem = (Button) findViewById(R.id.bt_redeem);
        redeem = FirebaseDatabase.getInstance().getReference().child("redeem");
        userCredits = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mCurrentUser.getEmail().replace('.', ',')).child("credits");

        btRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                View view = getLayoutInflater().inflate(R.layout.profile_redeem_dialog, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                Button dialogRedeem = (Button) view.findViewById(R.id.dialog_redeem);
                TextView availableCredits = (TextView) view.findViewById(R.id.available_credits);
                availableCredits.setText(String.valueOf(mCurrentUser.getCredits()));
                TextView TvCouponsBought = (TextView) view.findViewById(R.id.coupans_bought);
                long couponsBought = mCurrentUser.getCredits() / 20;
                TvCouponsBought.setText(String.valueOf(couponsBought));
                TextView creditsUsed = (TextView) view.findViewById(R.id.credits_to_be_used);
                final long creditsUsedInt = couponsBought * 20;
                final long creditsLeft = mCurrentUser.getCredits() - creditsUsedInt;
                creditsUsed.setText(String.valueOf(creditsUsedInt));

                dialogRedeem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        redeem.push();
                        redeem = redeem.push().child(mCurrentUser.getEmail().replace('.', ','));

                        redeem.child("value").setValue(creditsUsedInt);
                        userCredits.setValue(creditsLeft);
                        credits.setText(String.valueOf(creditsLeft));
                        Toast.makeText(ProfileActivity.this, "Coupons will be mailed tomorrow", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

                if (couponsBought < 1) {
                    Toast.makeText(ProfileActivity.this, "Minimum 20 Credits Required", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.show();
                }

            }
        });

        Picasso.with(ProfileActivity.this)
                .load(mCurrentUser.getPhotoUrl())
                .noFade()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(mProfileImage);

        mName.setText(mCurrentUser.getName());
        mEmail.setText(mCurrentUser.getEmail());
        credits.setText(String.valueOf(mCurrentUser.getCredits()));

    }
}
