package a3.com.convo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import a3.com.convo.GlideApp;
import a3.com.convo.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private static Context context;
    // Your friends usernames
    private ArrayList<String> myFriends;
    // Selected position on RV/selected friend(s)
    private int selectedPos = RecyclerView.NO_POSITION;
    private String selectedFriend;
    // For display
    private ParseUser currentFriend;
    private String profPic, name;
    // Parse columns
    private static final String username = "username";
    private static final String profPicUrl = "profPicUrl";
    private static final String fullName = "name";

    // Brings friends in to adjust into RV
    public FriendAdapter(ArrayList<String> friends) {
        myFriends = friends;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View friendView = inflater.inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(friendView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendAdapter.ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(selectedPos == position ? Color.rgb(229,229,229) : Color.TRANSPARENT);
        // Get friend
        final String friend = myFriends.get(position);
        // Find friend in background
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(username, friend);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects != null && !objects.isEmpty()) {
                    // Get object's values from parse
                    currentFriend = objects.get(0);
                    profPic = currentFriend.getString(profPicUrl);
                    name = currentFriend.getString(fullName);

                    // Set friend's photo
                    if(profPic != null && context != null && holder.ivFriend != null) {
                        GlideApp.with(context)
                                .load(profPic)
                                .circleCrop()
                                .into(holder.ivFriend);
                    }

                    // Set friends name
                    holder.tvFriend.setText(name);
                } else {
                    Toast.makeText(context, "Objects may be empty.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivFriend;
        public TextView tvFriend;

        public ViewHolder (View friendView) {
            super(friendView);

            ivFriend = friendView.findViewById(R.id.friend_iv);
            tvFriend = friendView.findViewById(R.id.friend_tv);

            friendView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            // Get friend position
            int position = getAdapterPosition();
            // Make sure the position is valid/actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // Get selected friend to play with
                selectedFriend = myFriends.get(position);
                System.out.println("/" + selectedFriend);
            }

            // Highlight selected friend(s)
            notifyItemChanged(selectedPos);
            selectedPos = getAdapterPosition();
            notifyItemChanged(selectedPos);
        }
    }

    // Passes selected friend onto game fragment
    public String getSelectedFriend() {
        return selectedFriend;
    }

}
