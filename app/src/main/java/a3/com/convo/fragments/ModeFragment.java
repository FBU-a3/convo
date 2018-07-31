package a3.com.convo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;

/**
 * This class is the fragment in PlayGameActivity where the user selects the mode
 * they would like to play in and can click a button to start the game.
 **/
public class ModeFragment extends Fragment {
    private Button startGame;
    private String friend;
    private String mode;

    public ModeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getContext(), friend, Toast.LENGTH_SHORT).show();
        System.out.println(friend);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mode, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        startGame = (Button) view.findViewById(R.id.start_game_btn);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof PlayGameActivity) {
                    ((PlayGameActivity) getContext()).goToGame(friend, Constants.FREESTYLE);
                }
            }
        });

        Button timedMode = (Button) view.findViewById(R.id.timed_mode);
        timedMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof PlayGameActivity) {
                    ((PlayGameActivity) getContext()).goToGame(friend, Constants.TIMED);
                }
            }
        });
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }

}
