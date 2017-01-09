package com.example.parth.siamapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    UserObject mCurrentUser;
    ImageView mProfileImage;
    TextView mName;
    TextView mEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(UserObject currentUser) {
        ProfileFragment fragment = new ProfileFragment();
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
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileImage = (ImageView)root.findViewById(R.id.profile_image);
        mName = (TextView)root.findViewById(R.id.name);
        mEmail = (TextView)root.findViewById(R.id.email);

        Picasso.with(getContext()).load(mCurrentUser.getPhotoUrl()).noFade().into(mProfileImage);
        mName.setText(mCurrentUser.getName());
        mEmail.setText(mCurrentUser.getEmail());

        return root;
    }

}
