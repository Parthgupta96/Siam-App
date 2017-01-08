package com.example.parth.siamapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFeed extends Fragment {

    ArrayList<String> newsLines = new ArrayList<>();
    ArrayList<String> newsPhotos = new ArrayList<>();
    ArrayList<String> newsDates = new ArrayList<>();
    RecyclerView recyclerView;
    String TAG = getClass().getSimpleName();
Context mContext;
    public FacebookFeed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook_feed, container, false);
        init(view);
        getFeed();
        mContext = getContext();
        return view;
    }

    private void getFeed() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "https://graph.facebook.com/v2.8/dtusiam/posts?access_token=605382859616232|zj6UV0o9j6HhSYY9E2tXHC40iP4&fields=message%2Cobject_id%2Ccreated_time%2Clink%2Cfull_picture&__mref=message_bubble";

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray array = response.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.has("message")) {
                                    newsLines.add(object.getString("message"));
                                    newsDates.add(object.getString("created_time"));
                                    if (object.has("full_picture"))
                                        newsPhotos.add(object.getString("full_picture"));
                                    else
                                        newsPhotos.add("");
                                }
                            }
                            CustomAdapter adapter = new CustomAdapter(newsPhotos, newsLines, newsDates,mContext);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {

                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog./d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance(getContext()).addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fb_feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        ArrayList<String> photos;
        ArrayList<String> headlines;
        ArrayList<String> dates;
        Context mContext;

        public CustomAdapter(ArrayList<String> photos, ArrayList<String> headlines, ArrayList<String> dates,Context context) {
            this.photos = photos;
            this.headlines = headlines;
            this.dates = dates;
            mContext =context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, null);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            if (photos.size() > 0 && (photos.get(position).length() != 0)) {

                holder.newsPhoto.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(photos.get(position))
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
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
