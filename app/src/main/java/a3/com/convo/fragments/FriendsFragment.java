package a3.com.convo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseUser;

import java.util.ArrayList;

import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;
import a3.com.convo.adapters.FriendAdapter;

/**
 * This class is the Fragment in PlayGameActivity where the user can choose the friends they
 * would like to play the game with and click a button to start the game. All the facebook friends
 * who are on the app are shown in a recycler list view. The user can select (highlight by click)
 * the friend they would like to play the game with and click start to choose a game mode.
 **/
public class FriendsFragment extends Fragment {
    ParseUser user = ParseUser.getCurrentUser();
    RecyclerView friendsRv;
    private Context context;
    private Button startButton;
    FriendAdapter friendAdapter;
    ArrayList<String> friends;

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
        context = getActivity();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlayGameActivity)context).goToMode(friendAdapter.getSelectedFriend());
            }
        });
        friends = (ArrayList<String>) user.get("friends");
        friendsRv = view.findViewById(R.id.rv_friends);
        friendAdapter = new FriendAdapter(friends);
        friendsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRv.setAdapter(friendAdapter);
    }
}
