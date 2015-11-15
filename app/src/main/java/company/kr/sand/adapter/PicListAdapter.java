package company.kr.sand.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;

import company.kr.sand.rsibal.PicImageView;
import company.kr.sand.R;
import company.kr.sand.controller.AppController;
import company.kr.sand.data.FeedItem;

import java.util.List;

/**
 * Created by User on 2015-11-03.
 */
public class PicListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feeditems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public PicListAdapter(Activity activity, List<FeedItem> feeditems) {
        this.activity = activity;
        this.feeditems = feeditems;
    }
    @Override
    public int getCount() {
        return feeditems.size();
    }

    @Override
    public Object getItem(int location) {
        return feeditems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        PicImageView pic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pic_item, null);
            holder.pic = (PicImageView) convertView.findViewById(R.id.picImage1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        PicImageView picImageView = holder.pic;

        String item = feeditems.get(position).getImge();

        if(item != null) {
            picImageView.setImageUrl(item, imageLoader);
            picImageView.setVisibility(View.VISIBLE);
            picImageView
                    .setResponseObserver(new PicImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            //picImageView.setVisibility(View.GONE);
        }


        return convertView;
    }

}
