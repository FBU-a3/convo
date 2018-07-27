package a3.com.convo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import a3.com.convo.R;
import a3.com.convo.fragments.ConclusionFragment;
import a3.com.convo.fragments.FriendsFragment;
import a3.com.convo.fragments.GameFragment;
import a3.com.convo.fragments.ModeFragment;

public class PlayGameActivity extends AppCompatActivity {

    private FriendsFragment friendsFrag;
    private ModeFragment modeFrag;
    private GameFragment gameFrag;
    private ConclusionFragment conclusionFrag;
    private Fragment playGameFrag;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        ft = getSupportFragmentManager().beginTransaction();

        friendsFrag = new FriendsFragment();
        modeFrag = new ModeFragment();
        gameFrag = new GameFragment();
        conclusionFrag = new ConclusionFragment();
        playGameFrag = friendsFrag;

        ft.replace(R.id.play_game_fragment, playGameFrag);
        ft.commit();
    }

    public void goToFriends() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, friendsFrag);
        fragmentTransaction.commit();
    }

    public void goToMode(String selectedFriend){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, modeFrag);
        fragmentTransaction.commit();
        modeFrag.setFriend(selectedFriend);
    }

    public void goToGame(String selectedFriend) {
        //Fragment fragment = new GameFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, gameFrag);
        fragmentTransaction.commit();
        gameFrag.setFriend(selectedFriend);
    }

    public void goToConclusion() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.play_game_fragment, conclusionFrag);
        fragmentTransaction.commit();
    }
}
