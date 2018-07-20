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
import android.widget.EditText;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

import a3.com.convo.R;
import a3.com.convo.activities.ProfileActivity;
import a3.com.convo.adapters.AdditionalLikeAdapter;
import a3.com.convo.adapters.RecyclerViewItemClickListener;

public class AddInfoFragment extends Fragment {
    private Context context;
    private Button backButton;
    private Button addLikeButton;
    private EditText etNewLike;
    RecyclerView rvLikes;
    ArrayList<String> additionalLikes;
    AdditionalLikeAdapter alAdapter;

    public AddInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_info, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        context = getActivity();
        backButton = (Button) view.findViewById(R.id.back_to_prof_btn);
        etNewLike = (EditText) view.findViewById(R.id.et_new_like);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity)context).goBackToProfile();
            }
        });

        rvLikes = (RecyclerView) view.findViewById(R.id.rv_likes);

        readInExistingLikes();

        alAdapter = new AdditionalLikeAdapter(additionalLikes);
        rvLikes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLikes.setAdapter(alAdapter);

        addLikeButton = (Button) view.findViewById(R.id.add_like_btn);
        addLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemText = etNewLike.getText().toString();
                //alAdapter.add(itemText);
                additionalLikes.add(0, itemText);
                alAdapter.notifyDataSetChanged();
                ParseUser user = ParseUser.getCurrentUser();
                user.add("otherLikes", itemText);
                user.saveInBackground();
                etNewLike.setText("");
            }
        });

        alAdapter.setOnItemLongClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                String a = additionalLikes.get(position);
                ParseUser user = ParseUser.getCurrentUser();
                user.removeAll("otherLikes", Arrays.asList(additionalLikes.get(position)));
                additionalLikes.remove(position);
                alAdapter.notifyDataSetChanged();
                user.saveInBackground();
            }
        });
    }

    public void readInExistingLikes() {
        additionalLikes = new ArrayList<>();
        ParseUser user = ParseUser.getCurrentUser();
        additionalLikes.addAll(user.<String>getList("otherLikes"));
    }

}
