package a3.com.convo.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.HomeScreenActivity;
import a3.com.convo.adapters.AdditionalLikeAdapter;
import a3.com.convo.adapters.RecyclerViewItemClickListener;
import a3.com.convo.models.Page;

/**
 * This class is the fragment in Profile Activity where the user can add additional likes.
 * The likes are entered in a text field and upon clicking the add button, they are added to a
 * recycler view list by name and the ParseUser's otherLikes field by objectId. If the user
 * long clicks on any list item, it's deleted from the recycler view and the otherLikes array.
 **/
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

                Intent intent = new Intent(getActivity(), HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        rvLikes = (RecyclerView) view.findViewById(R.id.rv_likes);

        // read in existing likes
        additionalLikes = new ArrayList<>();
        ParseUser user = ParseUser.getCurrentUser();
        additionalLikes.addAll(user.<String>getList("otherLikes"));

        alAdapter = new AdditionalLikeAdapter(additionalLikes);
        rvLikes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLikes.setAdapter(alAdapter);

        addLikeButton = (Button) view.findViewById(R.id.add_like_btn);
        addLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemText = etNewLike.getText().toString();
                if (!itemText.isEmpty()) {
                    //alAdapter.add(itemText);
                    final ParseUser user = ParseUser.getCurrentUser();
                    final Page newPage = Page.newInstance(null, itemText, null, null, null, null);
                    newPage.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.e("LoginActivity", "Create page success");
                                user.add(Constants.OTHER_LIKES, newPage.getObjectId());
                                user.saveInBackground();
                                additionalLikes.add(0, newPage.getObjectId());
                                alAdapter.notifyDataSetChanged();
                                etNewLike.setText("");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "Text is empty", Toast.LENGTH_LONG).show();
                }
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
                user.removeAll(Constants.OTHER_LIKES, Arrays.asList(additionalLikes.get(position)));
                additionalLikes.remove(position);
                alAdapter.notifyDataSetChanged();
                user.saveInBackground();
            }

            @Override
            public void onItemDelete(View view, int position) {
                String a = additionalLikes.get(position);
                ParseUser user = ParseUser.getCurrentUser();
                user.removeAll(Constants.OTHER_LIKES, Arrays.asList(additionalLikes.get(position)));
                additionalLikes.remove(position);
                alAdapter.notifyDataSetChanged();
                user.saveInBackground();
            }
        });
    }
}
