package a3.com.convo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import a3.com.convo.R;
import a3.com.convo.adapters.CardAdapter;

/**
 * This class is a Fragment in PlayGameActivity where the user actually plays the game with the
 * friend they chose. Cards with the page likes, additional likes, and places each user has
 * been to are displayed in a stack. In this mode (freestyle mode) the user swipes cards away
 * to get the next card until the cards run out.
 **/
public class GameFragment extends Fragment {

    private Context context;
    SwipeDeck cardStack;
    private String friend;
    ParseUser player1;
    ArrayList<String> p1Likes;
    ParseUser player2;
    ArrayList<String> p2Likes;
    ArrayList<String> allLikes;
    CardAdapter adapter;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        Toast.makeText(context, "User selected: " + friend, Toast.LENGTH_SHORT).show();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);

        player1 = ParseUser.getCurrentUser();
        // pageLikes is guaranteed to be an array, but it's returned as an object anyway
        p1Likes = (ArrayList<String>) player1.get("pageLikes");

        // get the second player and their likes
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", friend);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    player2 = objects.get(0);
                    p2Likes = (ArrayList<String>) player2.get("pageLikes");

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

//    // put together all the page likes from both players, then add in their manual likes
//    private ArrayList<String> combineLikes() {
//        // will just be a list of names of pages and other topics
//        final ArrayList<String> combinedLikes = new ArrayList<>();
//
//        // add the manual likes
//        combinedLikes.addAll((ArrayList<String>)player1.get("otherLikes"));
//        combinedLikes.addAll((ArrayList<String>)player2.get("otherLikes"));
//
//        // query Parse for the pages they like (both players)
//        for (int i = 0; i < p1Likes.size(); i++) {
//            ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
//            query.getInBackground(p1Likes.get(i), new GetCallback<Page>() {
//                @Override
//                public void done(Page object, ParseException e) {
//                    if (e == null) {
//                        combinedLikes.add(object.getName());
//                    } else {
//                        Log.e("GameFragment", "Error retrieving Page names");
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//        return combinedLikes;
//    }
}
