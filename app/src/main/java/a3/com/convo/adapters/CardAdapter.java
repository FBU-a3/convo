package a3.com.convo.adapters;

import android.content.Context;
import android.os.CountDownTimer;
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
import java.util.concurrent.TimeUnit;

import a3.com.convo.Constants;
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
        final TextView tvTime = (TextView) v.findViewById(R.id.tvTime);

        // get the page name associated with the id at that position in the array
        String objectId = getItem(i);

        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.getInBackground(objectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null) {
                    tvTopic.setText(object.getName());

                    CountDownTimer timer = new CountDownTimer(Constants.CARD_TIME, Constants.TIMER_INTERVAL) {
                        @Override
                        public void onTick(long l) {
                            tvTime.setText(
                                    String.format(context.getResources().getString(R.string.timer_format),
                                            TimeUnit.MILLISECONDS.toMinutes(l),
                                            TimeUnit.MILLISECONDS.toSeconds(l)
                                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                            );
                        }

                        @Override
                        public void onFinish() {
                            tvTime.setText(R.string.next_card);
                        }
                    };
                    timer.start();

                    // TODO: takes far too long to load picture
                    if (object.getPageId() != null && object.getPageId() != "") {
                        GlideApp.with(context).load(object.getCoverUrl()).into(ivCover);
                    }
                }
                else {
                    Log.e("name error", "Oops!");
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}
