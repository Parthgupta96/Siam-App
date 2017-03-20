package in.siamdtu.parth.siamapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import in.siamdtu.parth.siamapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SponsorsFragment extends Fragment {

    RecyclerView recyclerView;
    Context mContext;
    ArrayList<Integer> images;
    public SponsorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sponsors, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        images = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.sponsors_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayout.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        SponsorsAdapter adapter = new SponsorsAdapter(images);
        recyclerView.setAdapter(adapter);
        for (int i=1 ;i<6;i++){
            int id = getResources().getIdentifier("sponsors_"+i, "drawable", mContext.getPackageName());
            images.add(id);
        }
    }

    public class SponsorsAdapter extends RecyclerView.Adapter<SponsorsAdapter.ViewHolder> {
        ArrayList<Integer> images;

        public SponsorsAdapter(ArrayList<Integer> images) {
            this.images = images;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView sponsorImage;

            public ViewHolder(View itemView) {
                super(itemView);
                sponsorImage = (ImageView) itemView.findViewById(R.id.sponsors_image);
            }
        }

        @Override
        public SponsorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sponsors_item_image, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SponsorsAdapter.ViewHolder holder, int position) {
            holder.sponsorImage.setImageResource(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

    }
}
