package a3.com.convo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import a3.com.convo.Constants;
import a3.com.convo.GlideApp;
import a3.com.convo.R;
import a3.com.convo.models.Page;

public class HomeScreenActivity extends AppCompatActivity {
    TextView tvThreeLikes;
    TextView tvNumGamesPlayed;
    TextView tvUserName;
    ImageView ivUserProfPic;
    TextView tvUserHometown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button playGame = (Button) findViewById(R.id.play_game_btn);
        TextView logout = (TextView) findViewById(R.id.logout_txt);
        Button addLikes = (Button) findViewById(R.id.add_likes_btn);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserHometown = (TextView) findViewById(R.id.tv_user_hometown);
        ivUserProfPic = (ImageView) findViewById(R.id.iv_user_prof_pic);
        tvNumGamesPlayed = (TextView) findViewById(R.id.tv_games_played);
        tvThreeLikes = (TextView) findViewById(R.id.tv_three_likes);

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
        addLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreenActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            Log.e("ProfileDetailsFragment", "Somehow user was logged out of parse?");
            return;
        }

        setUserHometown(user);
        setProfPic(user);
        setUserName(user);
        setNumGamesPlayed(user);
        addLikesSentence(user);
    }

    private void setUserHometown(ParseUser user) {
        String userHometownObjectId = user.getString(Constants.HOMETOWN);
        if (userHometownObjectId == null) {
            Log.e("ProfileDetailsFragment", "User doesn't have hometown and that's fine");
            tvUserHometown.setText(Constants.EMPTY_STRING);
        }
        else {
            ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
            query.getInBackground(userHometownObjectId, new GetCallback<Page>() {
                @Override
                public void done(Page object, ParseException e) {
                    if (e == null && object != null) {
                        String hometownName = object.getName();
                        if (hometownName == null || hometownName.isEmpty()){
                            Log.e("ProfileDetailsFragment", "User's hometown object has no name");
                            return;
                        }
                        tvUserHometown.setText(getString(R.string.from) + " " + hometownName);
                    }
                    else {
                        Log.e("hometown", "hometown not showing up");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setProfPic(ParseUser user) {
        String profPicUrl = user.getString(Constants.PROF_PIC_URL);
        if (profPicUrl != null) {
            GlideApp.with(this)
                    .load(profPicUrl)
                    .circleCrop()
                    .into(ivUserProfPic);
        }
        else {
            Log.e("ProfileDetailsFragment", "User doesn't have profile picture and that's fine");
        }
    }

    private void setUserName(ParseUser user) {
        String userName = user.getString(Constants.NAME);
        if (userName == null) {
            Log.e("ProfileDetailsFragment", "User doesn't have name for some reason.");
            return;
        }
        tvUserName.setText(userName);
    }

    private void setNumGamesPlayed(ParseUser user) {
        Number gamesPlayedNum = user.getNumber(Constants.NUM_GAMES);
        if (gamesPlayedNum == null || !(gamesPlayedNum instanceof Integer)) {
            Log.e("ProfileDetailsFragment", "Num games played is null or not an integer.");
            return;
        }
        Integer gamesPlayed = (Integer)gamesPlayedNum;
        int userGamesPlayed = gamesPlayed.intValue();
        tvNumGamesPlayed.setText(userGamesPlayed + " " + getString(R.string.num_games));
    }

    private void addLikesSentence(ParseUser user) {
        ArrayList<String> userLikes = (ArrayList<String>) user.get(Constants.PARSE_PAGE_LIKES_KEY);
        if (userLikes == null || userLikes.isEmpty()) {
            Log.e("ProfileDetailsFragment", "User has no likes.");
            return;
        }
        else {
            final String[] likesSentence = {"Some of the pages you like on Facebook include...\n"};
            for (int i = 0; i < 3 && i < userLikes.size(); i++) {
                String likeObjectId = userLikes.get(i);
                if (likeObjectId == null || likeObjectId.isEmpty()) {
                    continue;
                }
                ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
                query.getInBackground(likeObjectId, new GetCallback<Page>() {
                    @Override
                    public void done(Page object, ParseException e) {
                        if (e == null && object != null) {
                            String likeName = object.getName();
                            if (likeName == null || likeName.isEmpty()) {
                                Log.e("LikeAdapter", "User's hometown object has no name");
                                return;
                            }
                            likesSentence[0] += " • " + likeName + "\n";
                            tvThreeLikes.setText(likesSentence[0]);
                        }
                        else if (object == null){
                            Log.e("likeAdapter", "like name not showing up");
                        }
                        else {
                            Log.e("likeAdapter", "error e");
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }
}
