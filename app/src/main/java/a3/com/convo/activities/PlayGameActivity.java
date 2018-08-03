package a3.com.convo.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import a3.com.convo.R;
import a3.com.convo.fragments.ConclusionFragment;
import a3.com.convo.fragments.FriendsFragment;
import a3.com.convo.fragments.GameFragment;
import a3.com.convo.fragments.ModeFragment;

public class PlayGameActivity extends AppCompatActivity {

    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // for orientation changes, saves the instance of GameFragment that's active in the game
        if (savedInstanceState != null) {
            GameFragment gameFrag = (GameFragment) getSupportFragmentManager().getFragment(savedInstanceState, "gameFrag");
            if (gameFrag != null) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.play_game_fragment, gameFrag).commit();
                return;
            }
        }

        // if there's no saved instance or no saved GameFragment, set up the FriendsFragment
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.play_game_fragment, new FriendsFragment());
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        // if we've already navigated to the GameFragment, save that to restore later
//        if (gameFrag != null)
//            getSupportFragmentManager().putFragment(outState, "gameFrag", gameFrag);
    }

    public void goToFriends() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, new FriendsFragment());
        fragmentTransaction.commit();
    }

    public void goToMode(String selectedFriend){
        ModeFragment modeFrag = new ModeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, modeFrag);
        fragmentTransaction.commit();
        modeFrag.setFriend(selectedFriend);
    }

    public void goToGame(String selectedFriend, String mode, int time, int numTopics) {
        GameFragment gameFrag = new GameFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, gameFrag);
        fragmentTransaction.commit();
        gameFrag.setFriend(selectedFriend);
        gameFrag.setMode(mode);
        gameFrag.setTime(time);
        if (numTopics != 0) gameFrag.setNumTopics(numTopics);
    }

    public void goToConclusion(ArrayList<String> topicsDiscussed) {
        ConclusionFragment conclusionFrag = new ConclusionFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, conclusionFrag);
        fragmentTransaction.commit();
        conclusionFrag.setDiscussedTopics(topicsDiscussed);
    }
}
