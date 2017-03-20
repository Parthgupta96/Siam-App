package in.siamdtu.parth.siamapp.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.siamdtu.parth.siamapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    RecyclerView recyclerView;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        ArrayList<MemberDetails> memberDetails = new ArrayList<>();

        memberDetails.add(new MemberDetails("Pulkit Gaur", "pgaur2008@gmail.com", getString(R.string.pulkit_image),"President"));
        memberDetails.add(new MemberDetails("Priyanshu Goyal", "priyanshu96.goyal@gmail.com", getString(R.string.priyanshu_image),"General Secretary"));
        memberDetails.add(new MemberDetails("Shreya Gupta", "shreyagupta.june96@gmail.com", getString(R.string.shreya_image),"Vise President"));
        memberDetails.add(new MemberDetails("Simran Ahuja", "simran.ahujaa@gmail.com",getString(R.string.simran_image),"Treasurer"));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

        Adapter adapter = new Adapter(memberDetails);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public class MemberDetails {
        public String name;
        public String email;
        public String image;
        public String designation;
        public MemberDetails(String name, String email, String image,String designation) {
            this.email = email;
            this.name = name;
            this.image = image;
            this.designation = designation;
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        ArrayList<MemberDetails> memberDetails;

        public Adapter(ArrayList<MemberDetails> array) {
            memberDetails = array;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView image;
            TextView name;
            TextView email;
            TextView designation;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (CircleImageView) itemView.findViewById(R.id.contact_avatar);
                name = (TextView) itemView.findViewById(R.id.contact_name);
                email = (TextView) itemView.findViewById(R.id.contact_email);
                designation = (TextView)itemView.findViewById(R.id.contact_designation);
            }
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
           final String email = memberDetails.get(position).email;
            holder.name.setText(memberDetails.get(position).name);
            holder.email.setText(memberDetails.get(position).email);
            holder.designation.setText(memberDetails.get(position).designation);
            Picasso.with(getContext())
                    .load(memberDetails.get(position).image)
                    .placeholder(R.drawable.avatar)
                    .into(holder.image);
            holder.email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
//                    intent.putExtra(Intent.EXTRA_EMAIL,email);
                    Uri uri = Uri.parse("mailto:"+email);
                    intent.setData(uri);

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return memberDetails.size();
        }
    }
}
