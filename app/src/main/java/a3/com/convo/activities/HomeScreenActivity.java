package a3.com.convo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import a3.com.convo.R;

public class HomeScreenActivity extends AppCompatActivity {

    private Button editProfile;
    private Button playGame;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        editProfile = (Button) findViewById(R.id.edit_profile_btn);
        playGame = (Button) findViewById(R.id.play_game_btn);
        logout = (Button) findViewById(R.id.logout_btn);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch to edit profile activity
                Intent intent = new Intent(HomeScreenActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to play game activity
                Intent intent = new Intent(HomeScreenActivity.this, PlayGameActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logout of facebook
                LoginManager.getInstance().logOut();
                // logout of parse
                ParseUser.logOut();
                // go back to log in screen
                Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
