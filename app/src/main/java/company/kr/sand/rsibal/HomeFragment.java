package company.kr.sand.rsibal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ViewFlipper;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import company.kr.sand.R;
import company.kr.sand.adapter.PicListAdapter;
import company.kr.sand.controller.AppController;
import company.kr.sand.data.FeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private GridView gridView;
    private ViewFlipper viewFlipper;
    private float lastX;
    private PicListAdapter listAdapter;
    private ArrayList<FeedItem> feedItems;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    public HomeFragment() {

    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);


                feedItems.add(item);

            }

            // notify data changes to list adapater
           listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){

            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        gridView = (GridView) view.findViewById(R.id.grid_view);
        feedItems = new ArrayList<FeedItem>();

        listAdapter = new PicListAdapter(getActivity(), feedItems);
        gridView.setAdapter(listAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), GridImageActivity.class);
                i.putExtra("position", position);
                i.putExtra("imagelist", feedItems);
                startActivity(i);
            }
        });

        viewFlipper = (ViewFlipper) view.findViewById(R.id.view_flipper);
        viewFlipper.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = motionEvent.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float currentX = motionEvent.getX();

                        if (lastX < currentX) {
                            if (viewFlipper.getDisplayedChild() == 0)
                                return true;

                            viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_from_left);
                            viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_to_right);

                            viewFlipper.showNext();
                        }

                        if (lastX > currentX) {
                            if (viewFlipper.getDisplayedChild() == 1)
                                return true;

                            viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_from_right);
                            viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_to_left);

                            viewFlipper.showPrevious();
                        }
                        return true;
                }
                return false;
            }
        });

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }


        return view;
    }
}