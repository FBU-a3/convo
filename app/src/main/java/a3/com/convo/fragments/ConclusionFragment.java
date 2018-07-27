package a3.com.convo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;

public class ConclusionFragment extends Fragment {
    private Context context;
    private Button playAgainButton;
    private String discussedTopics;

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
        context = getActivity();

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlayGameActivity)context).goToFriends();
            }
        });
    }
}
