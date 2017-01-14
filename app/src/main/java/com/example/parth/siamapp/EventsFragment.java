package com.example.parth.siamapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    RecyclerView recyclerView;
    Context mContext;

    private DatabaseReference mDatabase;

    public EventsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        init(view);

        return view;
    }

    public interface OnGetDataListener {
        public void onStart();

        public void onSuccess(ArrayList<EventsModel> pastEvents,ArrayList<EventsModel> upcomingEvents);
    }

    private void init(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.events_recycler_view);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        getEvents(new OnGetDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage("Fetching Data...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(ArrayList<EventsModel> pastEvents,ArrayList<EventsModel> upcomingEvents) {
                progressDialog.dismiss();
                StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();

                // set a header position callback to set elevation on sticky headers, because why not
                layoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager.HeaderPositionChangedCallback() {
                    @Override
                    public void onHeaderPositionChanged(int sectionIndex, View header, StickyHeaderLayoutManager.HeaderPosition oldPosition, StickyHeaderLayoutManager.HeaderPosition newPosition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
                            header.setElevation(elevated ? 8 : 0);
                        }
                    }
                });

                recyclerView.setLayoutManager(layoutManager);

                EventsAdapter eventsAdapter = new EventsAdapter(getContext(), upcomingEvents, pastEvents, new ItemClickListener() {
                    @Override
                    public void onItemClick(EventsModel eventsModel) {
                        Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(eventsAdapter);

            }
        });


    }

    void getEvents(final OnGetDataListener onGetDataListener) {
        onGetDataListener.onStart();
        final ArrayList<EventsModel> upcomingEvents = new ArrayList<>();
        final ArrayList<EventsModel> pastEvents = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event Details");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childNumber = 0;
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    System.out.println();
                    for (DataSnapshot events : messageSnapshot.getChildren()) {
//                        EventsModel eventsModel = events.getValue(EventsModel.class);
                        if(childNumber==1){
                            upcomingEvents.add(events.getValue(EventsModel.class));

                        }else{
                            pastEvents.add(events.getValue(EventsModel.class));
                        }
                        System.out.println();
                    }
//                    EventsModel eventsModel = dataSnapshot.getChildren();
                childNumber=1;
                }
                if(upcomingEvents.size()==0){
                    upcomingEvents.add(new EventsModel("name","date","http\\:"));
                }
                onGetDataListener.onSuccess(pastEvents,upcomingEvents);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private interface ItemClickListener {
        void onItemClick(EventsModel eventsModel);
    }



    private class EventsAdapter extends SectioningAdapter {

        Context context;
        ArrayList<EventsModel> upcomingEvents;
        ArrayList<EventsModel> pastEvents;
        ItemClickListener itemClickListener;

        public EventsAdapter(Context context, ArrayList<EventsModel> upcomingEvents, ArrayList<EventsModel> pastEvents, ItemClickListener itemClickListener) {
            this.context = context;
            this.upcomingEvents = upcomingEvents;
            this.pastEvents = pastEvents;
            this.itemClickListener = itemClickListener;
        }


        public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
            TextView titleTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            }
        }

        public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
            TextView eventName;
            TextView eventDate;
            ImageView eventImage;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventImage = (ImageView) itemView.findViewById(R.id.events_item_image);
                eventName = (TextView) itemView.findViewById(R.id.events_name);
                eventDate = (TextView) itemView.findViewById(R.id.events_date);
            }
        }

        @Override
        public int getNumberOfSections() {
            return 2;
        }

        @Override
        public int getNumberOfItemsInSection(int sectionIndex) {
            if(sectionIndex==0){
                return upcomingEvents.size();
            }else{
                return pastEvents.size();
            }
        }

        @Override
        public boolean doesSectionHaveHeader(int sectionIndex) {
            return true;
        }

        @Override
        public SectioningAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.events_header, parent, false);
            return new HeaderViewHolder(v);
        }

        @Override
        public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.events_item, parent, false);
            return new ItemViewHolder(v);
        }

        @Override
        public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
            HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
            if (sectionIndex == 0) {
                hvh.titleTextView.setText("upcomingEvents");
            } else {
                hvh.titleTextView.setText("pastEvents");
            }
        }

        @Override
        public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
            ItemViewHolder ivh = (ItemViewHolder) viewHolder;
            final EventsModel events;

            if (sectionIndex == 0) {
                events = upcomingEvents.get(itemIndex);
            } else {
                events = pastEvents.get(itemIndex);
            }
            ivh.eventName.setText(events.eventName);
            ivh.eventDate.setText(events.date);

            Picasso.with(mContext).load(events.imageUrl)
                    .error(R.drawable.siam)
                    .placeholder(R.drawable.siam)
                    .into(ivh.eventImage);


            ivh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(events);
                }
            });
        }
    }
}
