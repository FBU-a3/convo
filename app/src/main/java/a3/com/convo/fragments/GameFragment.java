package a3.com.convo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daprlabs.aaron.swipedeck.SwipeDeck;

import java.util.ArrayList;

import a3.com.convo.R;
import a3.com.convo.adapters.CardAdapter;


public class GameFragment extends Fragment {

    private ArrayList<String> ids;
    private Context context;
    CardAdapter adapter;
    SwipeDeck cardStack;

    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        ids = new ArrayList<>();
        ids.add("one");
        ids.add("two");
        ids.add("three");
        ids.add("four");
        ids.add("five");
        ids.add("six");

        adapter = new CardAdapter(ids, context);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        cardStack.setAdapter(adapter);

    }
}
