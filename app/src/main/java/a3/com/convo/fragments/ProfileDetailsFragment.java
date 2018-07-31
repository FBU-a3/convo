package a3.com.convo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import a3.com.convo.Constants;
import a3.com.convo.GlideApp;
import a3.com.convo.Models.Page;
import a3.com.convo.R;
import a3.com.convo.activities.ProfileActivity;

/**
 * This class is the fragment in ProfileActivity where the user can see and modify their
 * profile (by clicking add likes and navigating to the additional info fragment).
 **/
public class ProfileDetailsFragment extends Fragment {
    private Context context;
    private Button addLikes;
    private TextView tvUserName;
    private TextView tvUserHometown;
    private ImageView ivUserProfPic;
    private TextView tvNumGamesPlayed;

    public ProfileDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        addLikes = (Button) view.findViewById(R.id.add_likes_btn);
        tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        tvUserHometown = (TextView) view.findViewById(R.id.tv_user_hometown);
        ivUserProfPic = (ImageView) view.findViewById(R.id.iv_user_prof_pic);
        tvNumGamesPlayed = (TextView) view.findViewById(R.id.tv_games_played);
        context = getActivity();

        addLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity)context).goToAddInfo();
            }
        });

        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            Log.e("ProfileDetailsFragment", "Somehow user was logged out of parse?");
            return;
        }
        String userName = user.getString(Constants.NAME);
        if (userName == null) {
            Log.e("ProfileDetailsFragment", "User doesn't have name for some reason.");
            return;
        }
        tvUserName.setText(userName);
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
                        String hometown_name = object.getName();
                        if (hometown_name == null || hometown_name.isEmpty()){
                            Log.e("ProfileDetailsFragment", "User's hometown object has no name");
                            return;
                        }
                        tvUserHometown.setText("From " + hometown_name);
                    }
                    else {
                        Log.e("hometown", "hometown not showing up");
                        e.printStackTrace();
                    }
                }
            });
        }
        Integer gamesPlayed = ((Integer)user.getNumber(Constants.NUM_GAMES));
        if (gamesPlayed == null) {
            Log.e("ProfileDetailsFragment", "User doesn't have num games");
            return;
        }
        int userGamesPlayed = gamesPlayed.intValue();
        tvNumGamesPlayed.setText("Number of games played: " + userGamesPlayed);

        String profPicUrl = user.getString(Constants.PROF_PIC_URL);
        if (profPicUrl != null) {
            GlideApp.with(view.getContext())
                    .load(profPicUrl)
                    .circleCrop()
                    .into(ivUserProfPic);
        }
        else {
            Log.e("ProfileDetailsFragment", "User doesn't have profile picture and that's fine");
        }

    }
}
