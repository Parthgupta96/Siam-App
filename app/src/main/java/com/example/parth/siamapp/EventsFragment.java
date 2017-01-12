package com.example.parth.siamapp;


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

import com.squareup.picasso.Picasso;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    RecyclerView recyclerView;
    Context mContext;
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

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.events_recycler_view);
        String image = "http\\";
        EventsModel demos[] = {new EventsModel("name1",
                "date1",image
                ),
                new EventsModel("name2",
                        "date2",image
                ),
                new EventsModel("name3",
                        "date3",image
                )
        };
        EventsAdapter eventsAdapter = new EventsAdapter(getContext(), demos, demos,new ItemClickListener() {
            @Override
            public void onItemClick(EventsModel eventsModel) {
                Toast.makeText(getActivity(),"click",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(eventsAdapter);

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

    }

    private interface ItemClickListener {
        void onItemClick(EventsModel eventsModel);
    }

    public class EventsModel {
        String eventName;
        String eventDate;
        String imageUrl;

        public EventsModel(String name, String date, String image) {
            eventName = name;
            eventDate = date;
            this.imageUrl = image;
        }
    }

    private  class EventsAdapter extends SectioningAdapter {

        Context context;
        EventsModel[] eventsModels;
        EventsModel[] eventsModels1;
        ItemClickListener itemClickListener;

        public EventsAdapter(Context context, EventsModel[] events,EventsModel[] events2, ItemClickListener itemClickListener) {
            this.context = context;
            this.eventsModels = events;
            this.eventsModels1 = events2;
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
            return eventsModels.length;
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
                hvh.titleTextView.setText("1");
            } else {
                hvh.titleTextView.setText("2");
            }
        }

        @Override
        public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
            ItemViewHolder ivh = (ItemViewHolder) viewHolder;
            final EventsModel events;

            if(sectionIndex==0){
                events = eventsModels[itemIndex];
            }else{
                events = eventsModels1[itemIndex];
            }
            ivh.eventName.setText(events.eventName);
            ivh.eventDate.setText(events.eventDate);

            Picasso.with(mContext).load(events.imageUrl)
                    .error(R.drawable.avatar)
                    .placeholder(R.drawable.avatar)
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
