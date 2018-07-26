package a3.com.convo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import a3.com.convo.R;
import a3.com.convo.activities.ProfileActivity;

/**
 * This class is the fragment in ProfileActivity where the user can see and modify their
 * profile (by clicking add likes and navigating to the additional info fragment).
 **/
public class ProfileDetailsFragment extends Fragment {
    private Context context;
    private Button addLikes;
    private TextView userName;
    private TextView userHometown;
    private ImageView userProfPic;

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
        context = getActivity();

        addLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity)context).goToAddInfo();
            }
        });

    }
}
