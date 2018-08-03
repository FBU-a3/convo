package a3.com.convo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;

public class ConclusionFragment extends Fragment {
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
        Button playAgainButton = (Button) view.findViewById(R.id.play_again_btn);
        TextView tvTopics = (TextView) view.findViewById(R.id.tv_topics);

        ParseQuery<a3.com.convo.models.Page> query = ParseQuery.getQuery(a3.com.convo.models.Page.class);
        query.getInBackground(objectId, new GetCallback<a3.com.convo.models.Page>() {
            @Override
            public void done(a3.com.convo.models.Page object, ParseException e) {
                if (e == null && discussedTopics != null) {
                    for (int i = 0; i < discussedTopics.size(); i++) {
                        discussedTopics.set(i, object.getName());
                    }
                }
                else {
                        Log.e("Page name error", "Oops!");
                        e.printStackTrace();
                }
            }
        });

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
