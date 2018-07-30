package a3.com.convo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;
import a3.com.convo.adapters.FriendAdapter;

public class ConclusionFragment extends Fragment {
    private Button playAgainButton;
    private ArrayList<String> discussedTopics;

    public ConclusionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conclusion, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        playAgainButton = (Button) view.findViewById(R.id.play_again_btn);
        TextView tvTopics = (TextView) view.findViewById(R.id.tv_topics);

        tvTopics.setText(TextUtils.join(Constants.JOIN_STRING, discussedTopics));


        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof PlayGameActivity)
                    ((PlayGameActivity) getContext()).goToFriends();
            }
        });

    }

    public void setDiscussedTopics(ArrayList<String> topicsDiscussed) {
        discussedTopics = topicsDiscussed;
    }
}
