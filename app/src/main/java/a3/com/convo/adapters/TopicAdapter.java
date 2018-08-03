package a3.com.convo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import a3.com.convo.models.Page;

import a3.com.convo.R;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    List<String> topics;
    private String selectedTopic;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public TopicAdapter(ArrayList<String> topicsDiscussed) {
        topics = topicsDiscussed;
    }

    public String getItem(int i) {
        return topics.get(i);
    }

    @NonNull
    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View topicView = inflater.inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(topicView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        holder.position = i;
        String topicObjectId = (String) topics.get(i);
        if (topicObjectId == null) {
            Log.e("TopicAdapter", "topicObjectId is null");
            return;
        }
        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.getInBackground(topicObjectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null && object != null) {
                    String topicName = object.getName();
                    if (topicName == null || topicName.isEmpty()) {
                        Log.e("TopicAdapter", "Topic has no name.");
                        return;
                    }
                    holder.tvTopic.setText(topicName);
                }
                else if (object == null){
                    Log.e("TopicAdapter", "Object is null.");
                }
                else {
                    Log.e("TopicAdapter", "Error e");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvTopic;
        private int position;
        public ViewHolder (View topicView) {
            super(topicView);
            tvTopic = topicView.findViewById(R.id.topic_tv);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewItemClickListener.onItemLongClick(v,position);
                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {
            // Get topic position
            int position = getAdapterPosition();

            // Make sure the position is valid/actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // Get selected topic
                selectedTopic = topics.get(position);

                String url = "https://facebook.com/" + selectedTopic;

//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(url));
//                context.startActivity(intent);
            }
        }
    }
}
