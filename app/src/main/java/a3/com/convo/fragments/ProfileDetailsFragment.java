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

import com.parse.ParseUser;

import a3.com.convo.Constants;
import a3.com.convo.GlideApp;
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
        String userHometown = user.getString(Constants.HOMETOWN);
        if (userHometown == null) {
            Log.e("ProfileDetailsFragment", "User doesn't have hometown and that's fine");
            tvUserHometown.setText(Constants.EMPTY_STRING);
        }
        else {
            tvUserHometown.setText(userHometown);
        }

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
