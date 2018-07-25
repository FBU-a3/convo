package a3.com.convo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import a3.com.convo.R;
import a3.com.convo.adapters.CardAdapter;


public class GameFragment extends Fragment {

    private Context context;
    SwipeDeck cardStack;

    // objectId of the other player
    private String friend;

    private static final String PAGE_LIKES = "pageLikes";

    ParseUser player1;
    ArrayList<String> p1Likes;
    ParseUser player2;
    ArrayList<String> p2Likes;
    ArrayList<String> allLikes;
    CardAdapter adapter;

    // Overall game timer elements
    TextView tvTimer;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        CountDownTimer timer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long l) {
                tvTimer.setText(
                        String.format(getString(R.string.timer_format), TimeUnit.MILLISECONDS.toMinutes(l),
                                TimeUnit.MILLISECONDS.toSeconds(l)
                                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                tvTimer.setText(R.string.game_over);
            }
        };
        timer.start();

        player1 = ParseUser.getCurrentUser();
        // pageLikes is guaranteed to be an array, but it's returned as an object anyway
        p1Likes = (ArrayList<String>) player1.get(PAGE_LIKES);

        // get the second player and their likes
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.getInBackground(friend, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    player2 = object;
                    p2Likes = (ArrayList<String>) player2.get(PAGE_LIKES);

                    // put together both player's likes and shuffle them
                    allLikes = new ArrayList<>();
                    allLikes.addAll(p1Likes);
                    allLikes.addAll(p2Likes);
                    Collections.shuffle(allLikes);

                    adapter = new CardAdapter(allLikes, context);
                    cardStack.setAdapter(adapter);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }
}
