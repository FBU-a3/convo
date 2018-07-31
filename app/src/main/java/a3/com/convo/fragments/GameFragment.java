package a3.com.convo.fragments;


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

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.adapters.CardAdapter;
import a3.com.convo.activities.PlayGameActivity;

/**
 * This class is a Fragment in PlayGameActivity where the user actually plays the game with the
 * friend they chose. Cards with the page likes, additional likes, and places each user has
 * been to are displayed in a stack. In this mode (freestyle mode) the user swipes cards away
 * to get the next card until the cards run out.
 **/

public class GameFragment extends Fragment {
    private SwipeDeck cardStack;

    // objectId of the other player
    private String friend;

    // gameplay mode
    private String mode;

    // amount of time per game/card, depending on mode above
    private int time;

    private ParseUser player1;
    private ArrayList<String> player1Likes;
    private ParseUser player2;
    private ArrayList<String> player2Likes;
    private ArrayList<String> allLikes;
    private CardAdapter adapter;

    // declared as instance for use in other methods
    private CountDownTimer timer;

    private ArrayList<String> topicsDiscussed;


    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        topicsDiscussed = new ArrayList<>();

        // change initial amount based on if timer is set per game or per card
        int startTime = time;

        // Overall game timer elements
        final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        timer = new CountDownTimer(startTime, Constants.TIMER_INTERVAL) {
            @Override
            public void onTick(long l) {
                tvTimer.setText(
                        String.format(view.getContext().getResources().getString(R.string.timer_format),
                                TimeUnit.MILLISECONDS.toMinutes(l),
                                TimeUnit.MILLISECONDS.toSeconds(l)
                                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                if (mode.equals(Constants.FREESTYLE)) {
                    endGame(tvTimer);
                } else {
                    cardStack.swipeTopCardLeft(Constants.CARD_SWIPE_DURATION);
                    restartTimer();
                }
            }
        };

        // when a card is swiped, add it to topics discussed and reset the card timer if in game mode
        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                // reset the timer of the next card if they're playing in timed mode
                if (mode.equals(Constants.TIMED)) restartTimer();
                if (stableId <= Integer.MAX_VALUE && stableId <= allLikes.size()) {
                    topicsDiscussed.add(allLikes.get((int)stableId));
                }
            }

            @Override
            public void cardSwipedRight(long stableId) {
                // reset the timer of the next card if they're playing in timed mode
                if (mode.equals(Constants.TIMED)) restartTimer();
                if (stableId <= Integer.MAX_VALUE && stableId <= allLikes.size()) {
                    topicsDiscussed.add(allLikes.get((int)stableId));
                }
            }
        });

        player1 = ParseUser.getCurrentUser();
        // pageLikes is guaranteed to be an array, but it's returned as an object anyway
        player1Likes = (ArrayList<String>) player1.get(Constants.PARSE_PAGE_LIKES_KEY);

        // get the second player and their likes
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        if (!friend.equals(Constants.EMPTY_STRING)) {
            query.getInBackground(friend, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (object != null) {
                        player2 = object;
                        player2Likes = (ArrayList<String>) player2.get(Constants.PARSE_PAGE_LIKES_KEY);

                        // put together both player's likes and shuffle them
                        allLikes = new ArrayList<>();
                        allLikes.addAll(player1Likes);
                        allLikes.addAll(player2Likes);
                        Collections.shuffle(allLikes);

                        adapter = new CardAdapter(allLikes, player1Likes, player2Likes, player2);
                        cardStack.setAdapter(adapter);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
        timer.start();
    }

    private void endGame(TextView tv) {
        tv.setText(getString(R.string.game_over));
        if (getContext() instanceof PlayGameActivity)
            ((PlayGameActivity) getContext()).goToConclusion(topicsDiscussed);
    }

    private void restartTimer() {
        timer.cancel();
        timer.start();
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }
    public void setMode(String selectedMode) {
        mode = selectedMode;
    }

    // sets the time per card (timed mode) or per game (freestyle mode)
    public void setTime(int selectedTime) {
        if (mode.equals(Constants.FREESTYLE)) time = 1000 * 60 * selectedTime; //convert entered number of minutes to ms
        else time = 1000 * selectedTime; // convert entered number of seconds to ms
    }
}
