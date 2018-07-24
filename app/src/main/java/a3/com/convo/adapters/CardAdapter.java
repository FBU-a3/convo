package a3.com.convo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import a3.com.convo.GlideApp;
import a3.com.convo.Models.Page;
import a3.com.convo.R;

public class CardAdapter extends BaseAdapter {

    private List<String> pages;
    private Context context;

    public CardAdapter(List<String> data, Context context) {
        this.pages = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public String getItem(int i) {
        return pages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            // normally use a viewholder
            v = inflater.inflate(R.layout.item_card, viewGroup, false);
        }

        final TextView tvTopic = (TextView) v.findViewById(R.id.tvTopic);
        final ImageView ivCover = (ImageView) v.findViewById(R.id.ivCover);

        // get the page name associated with the id at that position in the array
        String objectId = getItem(i);

        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.getInBackground(objectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null) {
                    tvTopic.setText(object.getName());

                    // TODO: fix this, picture currently isn't showing up
                    GlideApp.with(context).load(object.getCoverUrl()).into(ivCover);
                }
                else {
                    Log.e("name error", object.toString());
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}
