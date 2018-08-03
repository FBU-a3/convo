package a3.com.convo.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;
import a3.com.convo.adapters.CardAdapter;

/**
 * This class is a Fragment in PlayGameActivity where the user actually plays the game with the
 * friend they chose. Cards with the page likes, additional likes, and places each user has
 * been to are displayed in a stack. In this mode (freestyle mode) the user swipes cards away
 * to get the next card until the cards run out.
 **/

public class GameFragment extends Fragment {
    // string constants used only in this fragment
    private static final String FRIEND = "friend";
    private static final String MODE = "mode";
    private static final String TIME = "time";
    private static final String TIME_LEFT = "timeLeft";
    private static final String CONFIG_CHANGE = "configChange";

    private SwipeDeck cardStack;

    // objectId of the other player
    private String friend;

    // gameplay mode
    private String mode;

    // amount of time per game/card, depending on mode above
    private long time;

    // amount of time left at any given moment, used for orientation changes
    private long timeLeft;

    // boolean telling if configuration was changed and timer needs to be reset
    private boolean configChange;

    // number of topics if in timed mode
    private int numTopics;

    private ParseUser player1;
    private ArrayList<String> player1Likes;
    private ParseUser player2;
    private ArrayList<String> player2Likes;
    private ArrayList<String> allLikes;
    private CardAdapter adapter;

    // declared as instance for use in other methods
    private CountDownTimer timer;

    // need as an instance for when we recreate the CountdownTimer after config change
    private TextView tvTimer;

    private ArrayList<String> topicsDiscussed;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            friend = Parcels.unwrap(savedInstanceState.getParcelable(FRIEND));
            mode = Parcels.unwrap(savedInstanceState.getParcelable(MODE));
            time = Parcels.unwrap(savedInstanceState.getParcelable(TIME));
            timeLeft = Parcels.unwrap(savedInstanceState.getParcelable(TIME_LEFT));
            configChange = Parcels.unwrap(savedInstanceState.getParcelable(CONFIG_CHANGE));
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(FRIEND, Parcels.wrap(friend));
        outState.putParcelable(MODE, Parcels.wrap(mode));
        outState.putParcelable(TIME, Parcels.wrap(time));
        outState.putParcelable(TIME_LEFT, Parcels.wrap(timeLeft));
        configChange = true;
        outState.putParcelable(CONFIG_CHANGE, Parcels.wrap(configChange));
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        topicsDiscussed = new ArrayList<>();

        // check for if timeLeft is null (default value), and if so, set clock to "time"
        long startTime = (timeLeft != Constants.LONG_NULL) ? timeLeft : time;

        // Overall game timer elements
        tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        timer = new CountDownTimer(startTime, Constants.TIMER_INTERVAL) {
            @Override
            public void onTick(long l) {
                onTimerTick(l);
            }

            @Override
            public void onFinish() {
                onTimerFinish();
                Integer player1Games = (Integer)player1.getNumber(Constants.NUM_GAMES);
                if (player1Games == null) {
                    Log.e("GameFragment", "Query returned null number of games in player1games");
                    return;
                }
                Integer player1GamesIncremented = new Integer(player1Games.intValue() + 1);
                player1.put(Constants.NUM_GAMES, player1GamesIncremented);
                player1.saveInBackground();
                Integer player2Games = (Integer)player2.getNumber(Constants.NUM_GAMES);
                if (player2Games == null) {
                    Log.e("GameFragment", "Query returned null number of games in player2games");
                    return;
                }
                Integer player2GamesIncremented = new Integer(player2Games.intValue() + 1);
                player2.put(Constants.NUM_GAMES, player2GamesIncremented);
                player2.saveInBackground();
                if (mode.equals(Constants.FREESTYLE)) {
                    endGame(tvTimer);
                } else {
                    cardStack.swipeTopCardLeft(Constants.CARD_SWIPE_DURATION);
                    restartTimer();
                }
            }
        };

        // TODO: check countdown of numTopics, off by one now
        // when a card is swiped, add it to topics discussed and reset the card timer if in game mode
        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                // reset the timer of the next card if they're playing in timed mode
                if (mode.equals(Constants.TIMED)) {
                    numTopics--;
                    restartTimer();
                }
                if (stableId <= Integer.MAX_VALUE && stableId <= allLikes.size()) {
                    topicsDiscussed.add(allLikes.get((int)stableId));
                }
            }

            @Override
            public void cardSwipedRight(long stableId) {
                // reset the timer of the next card if they're playing in timed mode
                if (mode.equals(Constants.TIMED)) {
                    numTopics--;
                    restartTimer();
                }
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

        if (friend != null && !friend.equals(Constants.EMPTY_STRING)) {
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

    // called in the fragment lifecycle, overridden to avoid crashes from timer continuing after
    @Override
    public void onStop() {
        // stop the timer so that endGame is not called after the fragment goes away
        timer.cancel();
        super.onStop();
    }

    private void onTimerTick(long l) {
        timeLeft = l;
        tvTimer.setText(
                String.format(getActivity().getResources().getString(R.string.timer_format),
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
        );
    }

    private void onTimerFinish() {
        if (mode.equals(Constants.FREESTYLE)) {
            endGame();
        } else {
            if (numTopics == 0) {
                endGame();
            } else {
                cardStack.swipeTopCardLeft(Constants.CARD_SWIPE_DURATION);
                restartTimer();
            }
        }
    }

    private void endGame() {
        if (getContext() instanceof PlayGameActivity)
            ((PlayGameActivity) getContext()).goToConclusion(topicsDiscussed);
    }

    private void restartTimer() {
        timer.cancel();
        if (numTopics == 0) endGame();
        // on restart timer, we don't want to restart with timeLeft but rather with original time
        if (configChange) {
            timer = new CountDownTimer(time, Constants.TIMER_INTERVAL) {
                @Override
                public void onTick(long l) {
                    onTimerTick(l);
                }

                @Override
                public void onFinish() {
                    onTimerFinish();
                }
            };
        }
        timer.start();
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }
    public void setMode(String selectedMode) {
        mode = selectedMode;
    }
    public void setNumTopics(int selectedNumber) {
        numTopics = selectedNumber;
        Log.e("Topics", String.valueOf(numTopics));
    }

    // sets the time per card (timed mode) or per game (freestyle mode)
    public void setTime(int selectedTime) {
        if (mode.equals(Constants.FREESTYLE)) time = 1000 * 60 * selectedTime; //convert entered number of minutes to ms
        else time = 1000 * selectedTime; // convert entered number of seconds to ms
    }
}
