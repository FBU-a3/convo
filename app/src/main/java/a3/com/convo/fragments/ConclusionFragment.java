package a3.com.convo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;
import a3.com.convo.adapters.TopicAdapter;

public class ConclusionFragment extends Fragment {
    private ArrayList<String> topicsDiscussed;

    public ConclusionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conclusion, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button playAgainButton = (Button) view.findViewById(R.id.play_again_btn);
        playAgainButton = (Button) view.findViewById(R.id.play_again_btn);
        TextView tvTopics = (TextView) view.findViewById(R.id.tv_topics);

        tvTopics.setText(TextUtils.join(Constants.JOIN_STRING, topicsDiscussed));

        RecyclerView topicsRv = view.findViewById(R.id.rv_topics);
        TopicAdapter topicAdapter = new TopicAdapter(topicsDiscussed);
        topicsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        topicsRv.setAdapter(topicAdapter);

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof PlayGameActivity)
                    ((PlayGameActivity) getContext()).goToFriends();
            }
        });
    }

    public void setDiscussedTopics(ArrayList<String> topicsDiscussed) {
        this.topicsDiscussed = topicsDiscussed;
    }
}
