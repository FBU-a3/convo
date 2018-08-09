package a3.com.convo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.fragments.ConclusionFragment;
import a3.com.convo.fragments.FriendsFragment;
import a3.com.convo.fragments.GameFragment;
import a3.com.convo.fragments.ModeFragment;

public class PlayGameActivity extends AppCompatActivity {

    private static final String MODE_FRAG_TAG = "modeFrag";
    private static final String GAME_FRAG_TAG = "gameFrag";
    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (getIntent().getBooleanExtra("guest", false)) {
            isGuest = true;
            GameFragment guestGame = new GameFragment();
            ft.replace(R.id.play_game_fragment, guestGame, GAME_FRAG_TAG).commit();
            guestGame.setGuestMode();
            guestGame.setMode(Constants.FREESTYLE);
            guestGame.setTime(5);
            return;
        }

        // for orientation changes, saves the instance of GameFragment that's active in the game
        if (savedInstanceState != null) {
            GameFragment gf = (GameFragment) getSupportFragmentManager().findFragmentByTag(GAME_FRAG_TAG);
            if (gf != null) {
                ft.replace(R.id.play_game_fragment, gf, GAME_FRAG_TAG).commit();
                return;
            }

            ModeFragment mf = (ModeFragment) getSupportFragmentManager().findFragmentById(R.id.modeFragment);
            if (mf != null) {
                ft.replace(R.id.play_game_fragment, mf, MODE_FRAG_TAG).commit();
                return;
            }
        }

        // if there's no saved instance or no saved GameFragment, set up the FriendsFragment
        ft.replace(R.id.play_game_fragment, new FriendsFragment());
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GameFragment gf = (GameFragment) getSupportFragmentManager().findFragmentByTag(GAME_FRAG_TAG);
        ModeFragment mf = (ModeFragment) getSupportFragmentManager().findFragmentByTag(MODE_FRAG_TAG);
        if (gf != null) {
            getSupportFragmentManager().putFragment(outState, GAME_FRAG_TAG, gf);
        }
        if (mf != null) {
            getSupportFragmentManager().putFragment(outState, MODE_FRAG_TAG, mf);
        }
    }

    public void goHome() {
        Intent i = new Intent(PlayGameActivity.this, HomeScreenActivity.class);
        startActivity(i);
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
        fragmentTransaction.replace(R.id.play_game_fragment, modeFrag, MODE_FRAG_TAG);
        fragmentTransaction.commit();
        modeFrag.setFriend(selectedFriend);
    }

    public void goToGame(String selectedFriend, String mode, int time, int numTopics) {
        GameFragment gameFrag = new GameFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, gameFrag, GAME_FRAG_TAG);
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
