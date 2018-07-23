package a3.com.convo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;

public class ModeFragment extends Fragment {
    private Context context;
    private Button startGame;
    private String friend;

    public ModeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        Toast.makeText(context, friend, Toast.LENGTH_SHORT).show();
        System.out.println(friend);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mode, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        startGame = (Button) view.findViewById(R.id.start_game_btn);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlayGameActivity)context).goToGame(friend);
            }
        });
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }

}
