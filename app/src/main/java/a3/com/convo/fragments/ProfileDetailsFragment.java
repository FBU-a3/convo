package a3.com.convo.fragments;

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
import a3.com.convo.R;
import a3.com.convo.activities.ProfileActivity;
import a3.com.convo.models.Page;

/**
 * This class is the fragment in ProfileActivity where the user can see and modify their
 * profile (by clicking add likes and navigating to the additional info fragment).
 **/
public class ProfileDetailsFragment extends Fragment {
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
        Button addLikes = (Button) view.findViewById(R.id.add_likes_btn);
        TextView tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        final TextView tvUserHometown = (TextView) view.findViewById(R.id.tv_user_hometown);
        ImageView ivUserProfPic = (ImageView) view.findViewById(R.id.iv_user_prof_pic);
        TextView tvNumGamesPlayed = (TextView) view.findViewById(R.id.tv_games_played);

        addLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity)getActivity()).goToAddInfo();
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
                        String hometownName = object.getName();
                        if (hometownName == null || hometownName.isEmpty()){
                            Log.e("ProfileDetailsFragment", "User's hometown object has no name");
                            return;
                        }
                        tvUserHometown.setText(getString(R.string.from) + hometownName);
                    }
                    else {
                        Log.e("hometown", "hometown not showing up");
                        e.printStackTrace();
                    }
                }
            });
        }
        Number gamesPlayedNum = user.getNumber(Constants.NUM_GAMES);
        if (!(gamesPlayedNum instanceof Integer)) {
            Log.e("ProfileDetailsFragment", "Number isn't instance of integer");
            return;
        }
        Integer gamesPlayed = ((Integer)gamesPlayedNum);
        if (gamesPlayed == null) {
            Log.e("ProfileDetailsFragment", "User doesn't have num games");
            return;
        }
        int userGamesPlayed = gamesPlayed.intValue();
        tvNumGamesPlayed.setText(getString(R.string.num_games_played) + ": " + userGamesPlayed);

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
