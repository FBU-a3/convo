package a3.com.convo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.parse.ParseUser;

import java.util.ArrayList;

import a3.com.convo.R;
import a3.com.convo.adapters.CardAdapter;


public class GameFragment extends Fragment {

    private ArrayList<String> ids;
    private Context context;
    CardAdapter adapter;
    SwipeDeck cardStack;
    private String friend;

    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        Toast.makeText(context, friend, Toast.LENGTH_SHORT).show();
        ParseUser user = ParseUser.getCurrentUser();
        // pageLikes is guaranteed to be an array, but it's returned as an object anyway
        ids = (ArrayList<String>) user.get("pageLikes");


        adapter = new CardAdapter(ids, context);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        cardStack.setAdapter(adapter);
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }
}
