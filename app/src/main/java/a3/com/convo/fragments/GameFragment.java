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

    // gameplay mode (freestyle vs timed)
    private String mode;

    // indicates if user is in guest mode, which sets the 'mode' var above to freestyle
    private boolean isGuest; // TODO: check this any time we do anything with player1 or player2

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
    private ArrayList<String> player1Topics;
    private ArrayList<String> player1Places;
    private ParseUser player2;
    private ArrayList<String> player2Likes;
    private ArrayList<String> player2Topics;
    private ArrayList<String> player2Places;
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
            isGuest = Parcels.unwrap(savedInstanceState.getParcelable(Constants.GUEST));
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
        outState.putParcelable(Constants.GUEST, Parcels.wrap(isGuest));
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
            }
        };

        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                cardSwiped(stableId);
            }

            @Override
            public void cardSwipedRight(long stableId) {
                cardSwiped(stableId);
            }
        });

        // if user is in guest mode, skip the rest of this and send them straight to guest mode
        if (isGuest) {
            sendToGuestMode();
            return;
        } else {
            setUpUsers();
            timer.start();
        }
    }

    private void sendToGuestMode() {
        allLikes = new ArrayList<>();
        allLikes.addAll(Constants.GUEST_TOPICS);
        Collections.shuffle(allLikes);
        adapter = new CardAdapter(allLikes);
        cardStack.setAdapter(adapter);
        timer.start();
    }

    private void setUpUsers() {
        player1 = ParseUser.getCurrentUser();
        // pageLikes is guaranteed to be an array, but it's returned as an object anyway
        player1Likes = (ArrayList<String>) player1.get(Constants.PARSE_PAGE_LIKES_KEY);
        player1Topics = (ArrayList<String>) player1.get(Constants.OTHER_LIKES);
        player1Places = (ArrayList<String>) player1.get(Constants.PARSE_TAGGED_PLACES);

        // get the second player and their likes
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        if (friend != null && !friend.equals(Constants.EMPTY_STRING)) {
            query.getInBackground(friend, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (object != null) {
                        player2 = object;
                        player2Likes = (ArrayList<String>) player2.get(Constants.PARSE_PAGE_LIKES_KEY);
                        player2Topics = (ArrayList<String>) player2.get(Constants.OTHER_LIKES);
                        player2Places = (ArrayList<String>) player2.get(Constants.PARSE_TAGGED_PLACES);

                        combineAndShuffleTopics();

                        adapter = new CardAdapter(allLikes, player1Likes, player2Likes, player2);
                        cardStack.setAdapter(adapter);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // put together both player's likes, other likes, and places and shuffle them
    private void combineAndShuffleTopics() {
        if (player1Topics != null) player1Likes.addAll(player1Topics);
        if (player1Places != null) player1Likes.addAll(player1Places);
        if (player2Topics != null) player2Likes.addAll(player2Topics);
        if (player2Places != null) player2Likes.addAll(player2Places);
        allLikes = new ArrayList<>();
        allLikes.addAll(player1Likes);
        allLikes.addAll(player2Likes);
        Collections.shuffle(allLikes);
    }

    // called in the fragment lifecycle, overridden to avoid crashes from timer continuing after
    @Override
    public void onStop() {
        timer.cancel();
        super.onStop();
    }

    private void onTimerTick(long l) {
        timeLeft = l;
        if (getContext() != null) {
            tvTimer.setText(
                    String.format(getContext().getResources().getString(R.string.timer_format),
                            TimeUnit.MILLISECONDS.toMinutes(l),
                            TimeUnit.MILLISECONDS.toSeconds(l)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
            );
        }
    }

    // when a card is swiped, add it to topics discussed and reset the card timer if in game mode
    private void cardSwiped(long stableId) {
        if (mode.equals(Constants.TIMED)) {
            numTopics--; // TODO: check countdown of numTopics, off by one now
            restartTimer();
        }
        if (stableId <= Integer.MAX_VALUE && stableId <= allLikes.size()) {
            topicsDiscussed.add(allLikes.get((int)stableId));
        }
    }

    private void onTimerFinish() {
        if (player1 != null && player2 != null) {
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
        }
        if (mode.equals(Constants.FREESTYLE)) {
            endGame();
        } else {
            cardStack.swipeTopCardLeft(Constants.CARD_SWIPE_DURATION);
            restartTimer();
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

    public void setGuestMode() {
        isGuest = true;
    }
}
