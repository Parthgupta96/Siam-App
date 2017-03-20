package in.siamdtu.parth.siamapp.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import in.siamdtu.parth.siamapp.EventsModel;
import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    RecyclerView recyclerView;
    Context mContext;
    ImageView ivNoInternet;
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

        public void onSuccess(ArrayList<EventsModel> pastEvents, ArrayList<EventsModel> upcomingEvents);
    }

    private void init(View view) {
        ivNoInternet = (ImageView) view.findViewById(R.id.events_no_internet);
        recyclerView = (RecyclerView) view.findViewById(R.id.events_recycler_view);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);

        if (Utils.isInternetConnected(mContext)) {
            getEvents(new OnGetDataListener() {
                          @Override
                          public void onStart() {
                              progressDialog.setMessage("Fetching Data...");
                              progressDialog.show();
                          }

                          @Override
                          public void onSuccess(ArrayList<EventsModel> pastEvents, ArrayList<EventsModel> upcomingEvents) {
                              progressDialog.dismiss();
                              StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();

                              // set a header position callback to set elevation on sticky headers, because why not
                              layoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager
                                      .HeaderPositionChangedCallback() {
                                  @Override
                                  public void onHeaderPositionChanged(int sectionIndex,
                                                                      View header,
                                                                      StickyHeaderLayoutManager.HeaderPosition oldPosition,
                                                                      StickyHeaderLayoutManager.HeaderPosition newPosition) {
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

                                      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                      final AlertDialog dialog = builder.create();
                                      LayoutInflater inflater = getActivity().getLayoutInflater();
                                      final View dialogLayout = inflater.inflate(R.layout.event_dialog_view, null);

                                      final ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.events_dialog_image);
                                      final TextView eventName = (TextView) dialogLayout.findViewById(R.id.events_dialog_event_name);
                                      final TextView eventDate = (TextView) dialogLayout.findViewById(R.id.events_dialog_event_date);
                                      final TextView eventDescription = (TextView) dialogLayout.findViewById(R.id.events_dialog_description);

                                      Picasso.with(mContext)
                                              .load(eventsModel.imageUrl)
                                              .into(imageView);

                                      eventName.setText(eventsModel.eventName);
                                      eventDate.setText(eventsModel.date);
                                      if (eventsModel.description != null) {
                                          eventDescription.setText(eventsModel.description);
                                      }
                                      ImageView closeButton = (ImageView) dialogLayout.findViewById(R.id.dialog_close);

                                      closeButton.setOnClickListener(new View.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(View view) {
                                                                             dialog.dismiss();
                                                                         }
                                                                     }

                                      );
                                      dialog.setView(dialogLayout);
                                      dialog.show();
                                  }
                              }

                              );
                              recyclerView.setAdapter(eventsAdapter);

                          }
                      }
            );
            ivNoInternet.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(mContext, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            ivNoInternet.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
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
                upcomingEvents.clear();
                pastEvents.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    System.out.println();
                    for (DataSnapshot events : messageSnapshot.getChildren()) {
//                        EventsModel eventsModel = events.getValue(EventsModel.class);
                        if (childNumber == 1) {
                            try {
                                upcomingEvents.add(events.getValue(EventsModel.class));
                            } catch (Exception e) {
                                Utils.makeToast("event 1 has issues",mContext);
                            }
                        } else {

                            try {
                                pastEvents.add(events.getValue(EventsModel.class));
                            } catch (Exception e) {
                                Utils.makeToast("event 0 has issues",mContext);
                            }
                        }
                        System.out.println();
                    }
//                    EventsModel eventsModel = dataSnapshot.getChildren();
                    childNumber = 1;
                }
                if (upcomingEvents.size() == 0) {
                    upcomingEvents.add(new EventsModel("null", "date", "http\\:"));
                }
                onGetDataListener.onSuccess(pastEvents, upcomingEvents);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private interface ItemClickListener {
        void
        onItemClick(EventsModel eventsModel);
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
            TextView noEvent;
            ImageView eventImage;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventImage = (ImageView) itemView.findViewById(R.id.events_item_image);
                eventName = (TextView) itemView.findViewById(R.id.events_name);
                eventDate = (TextView) itemView.findViewById(R.id.events_date);
                noEvent = (TextView) itemView.findViewById(R.id.no_event);
                noEvent.setVisibility(View.GONE);
            }
        }

        @Override
        public int getNumberOfSections() {
            return 2;
        }

        @Override
        public int getNumberOfItemsInSection(int sectionIndex) {
            if (sectionIndex == 0) {
                return upcomingEvents.size();
            } else {
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
                hvh.titleTextView.setText("upcoming Events");
            } else {
                hvh.titleTextView.setText("past Events");
            }
        }

        @Override
        public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
            ItemViewHolder ivh = (ItemViewHolder) viewHolder;
            final EventsModel events;

            ivh.noEvent.setVisibility(View.GONE);
            ivh.eventImage.setVisibility(View.VISIBLE);
            ivh.eventDate.setVisibility(View.VISIBLE);
            ivh.eventName.setVisibility(View.VISIBLE);
            if (sectionIndex == 0) {

                if (upcomingEvents.get(itemIndex).eventName.equalsIgnoreCase("null") && upcomingEvents.size() == 1) {
                    events = null;
                    ivh.itemView.setOnClickListener(null);
                    ivh.noEvent.setVisibility(View.VISIBLE);
                    ivh.eventImage.setVisibility(View.GONE);
                    ivh.eventDate.setVisibility(View.GONE);
                    ivh.eventName.setVisibility(View.GONE);
                } else {
                    events = upcomingEvents.get(itemIndex);
                }

            } else {
                events = pastEvents.get(itemIndex);
            }
            if (events != null) {
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
}
