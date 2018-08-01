package a3.com.convo.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
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

    private ParseUser player1;
    private ArrayList<String> player1Likes;
    private ParseUser player2;
    private ArrayList<String> player2Likes;
    private ArrayList<String> allLikes;
    private CardAdapter adapter;

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
        final SwipeDeck cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        topicsDiscussed = new ArrayList<>();


        // Overall game timer elements
        final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        CountDownTimer timer = new CountDownTimer(Constants.GAME_TIME, Constants.TIMER_INTERVAL) {
            @Override
            public void onTick(long l) {
                tvTimer.setText(
                        String.format(getActivity().getResources().getString(R.string.timer_format),
                                TimeUnit.MILLISECONDS.toMinutes(l),
                                TimeUnit.MILLISECONDS.toSeconds(l)
                                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                tvTimer.setText(getString(R.string.game_over));
                if (getContext() instanceof PlayGameActivity)
                    ((PlayGameActivity) getContext()).goToConclusion(topicsDiscussed);
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
        };
        timer.start();

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

                        // when a card is swiped, add it to topics discussed
                        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
                            @Override
                            public void cardSwipedLeft(long stableId) {
                                // reset the timer of the next card
                                if (stableId <= Integer.MAX_VALUE && stableId <= allLikes.size()) {
                                    topicsDiscussed.add(allLikes.get((int)stableId));
                                }
                            }

                            @Override
                            public void cardSwipedRight(long stableId) {
                                // reset the timer of the next card
                                if (stableId <= Integer.MAX_VALUE && stableId <= allLikes.size()) {
                                    topicsDiscussed.add(allLikes.get((int)stableId));
                                }
                            }
                        });
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }
}