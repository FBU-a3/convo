package a3.com.convo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseUser;

import java.util.ArrayList;

import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;
import a3.com.convo.adapters.FriendAdapter;
import a3.com.convo.adapters.RecyclerViewItemClickListener;

/**
 * This class is the Fragment in PlayGameActivity where the user can choose the friends they
 * would like to play the game with and click a button to start the game. All the facebook friends
 * who are on the app are shown in a recycler list view. The user can select (highlight by click)
 * the friend they would like to play the game with and click start to choose a game mode.
 **/
public class FriendsFragment extends Fragment implements RecyclerViewItemClickListener {
    private Button startButton;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        startButton = (Button) view.findViewById(R.id.start_btn);
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> friends = (ArrayList<String>) user.get("friends");

        RecyclerView friendsRv = view.findViewById(R.id.rv_friends);
        final FriendAdapter friendAdapter = new FriendAdapter(friends, this);
        friendsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRv.setAdapter(friendAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlayGameActivity)getActivity()).goToMode(friendAdapter.getSelectedFriend());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        startButton.setEnabled(true);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemDelete(View view, int position) {

    }
}
