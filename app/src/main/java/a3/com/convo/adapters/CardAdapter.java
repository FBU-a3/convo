package a3.com.convo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import a3.com.convo.R;

public class CardAdapter extends BaseAdapter {

    private List<String> topics;
    private Context context;

    public CardAdapter(List<String> data, Context context) {
        this.topics = topics;
        this.context = context;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public String getItem(int i) {
        return topics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            // normally use a viewholder
            v = inflater.inflate(R.layout.item_card, viewGroup, false);
        }

        TextView tvTopic = (TextView) v.findViewById(R.id.tvTopic);
        tvTopic.setText(getItem(i));

        return v;
    }
}
