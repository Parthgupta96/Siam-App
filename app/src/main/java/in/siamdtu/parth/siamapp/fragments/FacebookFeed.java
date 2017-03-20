package in.siamdtu.parth.siamapp.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import in.siamdtu.parth.siamapp.AppController;
import in.siamdtu.parth.siamapp.R;
import in.siamdtu.parth.siamapp.listeners.EndlessRecyclerViewScrollListener;
import in.siamdtu.parth.siamapp.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFeed extends Fragment {

    public FacebookFeed() {
        // Required empty public constructor
    }
    public static final int FIRST_TIME = 0;
    public static final int ON_SCROLL = 1;
    public static boolean isLoading = false;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    ArrayList<String> newsLines;
    ArrayList<String> newsPhotos;
    ArrayList<String> newsDates;
    ShimmerRecyclerView recyclerView;
    FbFeed fbFeed;
    String firstUrl;
    String TAG = getClass().getSimpleName();
    Context mContext;
    String nextPage;
    CustomAdapter adapter;
    ImageView noNetworkImage;
    private class FbFeed implements Serializable {
        ArrayList<String> fbNewsLines;
        ArrayList<String> fbNewsPhotos;
        ArrayList<String> fbNewsDates;

        public FbFeed() {
        }

        public FbFeed(ArrayList<String> newsPhotos, ArrayList<String> newsLines, ArrayList<String> newsDates) {
            this.fbNewsDates = newsDates;
            this.fbNewsLines = newsLines;
            this.fbNewsPhotos = newsPhotos;
        }

        public void add(ArrayList<String> newsPhotos, ArrayList<String> newsLines, ArrayList<String> newsDates) {
            this.fbNewsDates.addAll(newsDates);
            this.fbNewsLines.addAll(newsLines);
            this.fbNewsPhotos.addAll(newsPhotos);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        newsLines = new ArrayList<>();
        newsPhotos = new ArrayList<>();
        newsDates = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook_feed, container, false);
        init(view);
        recyclerView.setVisibility(View.VISIBLE);
        noNetworkImage.setVisibility(View.GONE);
        mContext = getContext();
        if (Utils.isInternetConnected(mContext) && savedInstanceState == null) {
            getFeed();
        } else {
            if (savedInstanceState != null) {
                FbFeed fbFeed = (FbFeed) savedInstanceState.getSerializable("feed");
                if (fbFeed != null) {
                    CustomAdapter adapter = new CustomAdapter(fbFeed.fbNewsPhotos, fbFeed.fbNewsLines, fbFeed.fbNewsDates, mContext);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(mContext, "internet not connected", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                    noNetworkImage.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(mContext, "internet not connected", Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.GONE);
                noNetworkImage.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    private void getFeed() {
        // Tag used to cancel the request

        firstUrl = "https://graph.facebook.com/v2.8/dtusiam/posts?access_token=605382859616232|zj6UV0o9j6HhSYY9E2tXHC40iP4&fields=message%2Cobject_id%2Ccreated_time%2Clink%2Cfull_picture&__mref=message_bubble";


        FbFeed localFBFeed = getFBfeed(firstUrl,FIRST_TIME);

    }

    private FbFeed getFBfeed(String url,int calledPlace) {
        if (url.isEmpty() || isLoading) {
            return null;
        }
        if (calledPlace==FIRST_TIME && adapter!=null) {
            recyclerView.setAdapter(adapter);
            return null;
        }
            isLoading = true;
        boolean isFirstTime;
        isFirstTime = (fbFeed == null);
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        if (isFirstTime) {
            pDialog.setMessage("Loading...");
            pDialog.show();
            recyclerView.showShimmerAdapter();
        }
        String tag_json_obj = "json_obj_req";
        final ArrayList<String> mNewsLines = new ArrayList<>();
        final ArrayList<String> mNewsPhotos = new ArrayList<>();
        final ArrayList<String> mNewsDates = new ArrayList<>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("data");
                            JSONObject paging = response.getJSONObject("paging");
                            nextPage = paging.getString("next");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.has("message")) {
                                    mNewsLines.add(object.getString("message"));
                                    mNewsDates.add(object.getString("created_time"));
                                    if (object.has("full_picture"))
                                        mNewsPhotos.add(object.getString("full_picture"));
                                    else
                                        mNewsPhotos.add("");
                                }
                            }
                            if (fbFeed == null) {
                                fbFeed = new FbFeed(mNewsPhotos, mNewsLines, mNewsDates);
                                newsDates = mNewsDates;
                                newsLines = mNewsLines;
                                newsPhotos = mNewsPhotos;
                                pDialog.hide();
                                recyclerView.hideShimmerAdapter();
                                adapter = new CustomAdapter(newsPhotos, newsLines, newsDates, mContext);
                                recyclerView.setAdapter(adapter);

                            } else {
                                if (newsDates.contains(mNewsDates.get(0))) {
                                    System.out.println("found");
                                }
                                    newsDates.addAll(mNewsDates);
                                    newsLines.addAll(mNewsLines);
                                    newsPhotos.addAll(mNewsPhotos);
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        } catch (JSONException e) {

                            isLoading = false;
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //                VolleyLog./d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
//                pDialog.hide();
                recyclerView.hideShimmerAdapter();
                isLoading = false;
            }
        });

        // Adding request to request queue
        AppController.getInstance(getContext()).addToRequestQueue(jsonObjReq, tag_json_obj);

        return fbFeed;
    }

    private void init(View view) {
        recyclerView = (ShimmerRecyclerView) view.findViewById(R.id.fb_feed_recycler_view);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(mContext);
        noNetworkImage = (ImageView)view.findViewById(R.id.no_internet_image);
        noNetworkImage.setVisibility(View.GONE);
        recyclerView.setLayoutManager(layoutManager);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(nextPage!=null) {
                    getFBfeed(nextPage, ON_SCROLL);
                }
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        ArrayList<String> photos;
        ArrayList<String> headlines;
        ArrayList<String> dates;
        Context mContext;

        public CustomAdapter(ArrayList<String> photos, ArrayList<String> headlines, ArrayList<String> dates, Context context) {
            this.photos = photos;
            this.headlines = headlines;
            this.dates = dates;
            mContext = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, null);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {
            if (photos.size() > 0 && (photos.get(position).length() != 0)) {

                holder.newsPhoto.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(photos.get(position))
                        .placeholder(R.drawable.siam1)
                        .error(R.drawable.siam1)
                        .into(holder.newsPhoto);
            } else {
                holder.newsPhoto.setVisibility(View.GONE);
            }
            holder.newsLine.setText(headlines.get(position));
            String date = dates.get(position).replace('T', ' ');
            holder.date.setText(date.substring(0, date.indexOf("+")));

            holder.newsPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    final AlertDialog dialog = builder.create();
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View dialogLayout = inflater.inflate(R.layout.image_view, null);
                    final ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.feed_image_enlarge);

                    Picasso.with(mContext)
                            .load(photos.get(position))
                            .into(imageView);
                    Button closeButton = (Button) dialogLayout.findViewById(R.id.feed_image_enlarge_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setView(dialogLayout);
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return headlines.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView newsPhoto;
            TextView newsLine, date;

            public CustomViewHolder(View itemView) {
                super(itemView);
                newsPhoto = (ImageView) itemView.findViewById(R.id.feed_image);
                newsLine = (TextView) itemView.findViewById(R.id.feed_body);
                date = (TextView) itemView.findViewById(R.id.feed_secondary_text);


            }
        }
    }


}
