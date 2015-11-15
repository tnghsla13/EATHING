package company.kr.sand.rsibal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import company.kr.sand.R;
import company.kr.sand.controller.AppController;
import company.kr.sand.data.FeedItem;

import java.util.ArrayList;

/**
 * Created by User on 2015-11-12.
 */
public class GridImageActivity extends Activity {

    private ImageLoader mImageLoader;
    private ArrayList<FeedItem> feed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_item);

        mImageLoader = AppController.getInstance().getImageLoader();

        // get intent data
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("position");
        feed = (ArrayList<FeedItem>) i.getSerializableExtra("imagelist");
        FeedItem item = feed.get(position);

        TextView name = (TextView) findViewById(R.id.name);
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) findViewById(R.id.feedImage1);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), mImageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), mImageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

    }
}
