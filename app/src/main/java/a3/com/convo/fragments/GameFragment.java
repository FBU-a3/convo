package a3.com.convo.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daprlabs.aaron.swipedeck.SwipeDeck;

import java.util.ArrayList;

import a3.com.convo.R;
import a3.com.convo.adapters.CardAdapter;

public class GameFragment extends Fragment {

    SwipeDeck cardStack;
    CardAdapter adapter;
    ArrayList<String> ids;

    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ids = new ArrayList<>();
        ids.add("one");
        ids.add("two");
        ids.add("three");
        ids.add("four");
        ids.add("five");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cardStack = (SwipeDeck) view.findViewById(R.id.cardStack);
        adapter = new CardAdapter(ids, getActivity());
        cardStack.setAdapter(adapter);
    }
}
