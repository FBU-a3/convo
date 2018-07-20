package a3.com.convo.adapters;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import a3.com.convo.GlideApp;
import a3.com.convo.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<String> myFriends;
    Context context;
    String selectedFriend;

    public FriendAdapter(ArrayList<String> friends) {
        myFriends = friends;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View friendView = inflater.inflate(R.layout.item_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(friendView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendAdapter.ViewHolder holder, int position) {
        // Get friends user ID's
        String friendId = myFriends.get(position);
        final String profilePicPath = "/" + friendId + "/picture";


        // Get friends profile picture
        GraphRequest picRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), profilePicPath, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    String picURL = response.getJSONObject().getString("url");
                    System.out.println("\n\n" +picURL + "\n\n");
                    GlideApp.with(context)
                            .load(picURL)
                            .circleCrop()
                            .into(holder.ivFriend);
                } catch (JSONException e) {
                    Toast.makeText(context, "Pic URL not retrieved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Get friends name
        Bundle picParams = new Bundle();
        picParams.putString("fields", "url");
        picRequest.setParameters(picParams);
        picRequest.executeAsync();

        GraphRequest nameRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/" + friendId, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    String name = response.getJSONObject().getString("name");
                    holder.tvFriend.setText(name);
                } catch (JSONException e) {
                    Toast.makeText(context, "Name not retrieved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nameRequest.executeAsync();
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
        }
    }

    public String getSelectedFriend() {
        return selectedFriend;
    }

    public void clear() {
        myFriends.clear();
        notifyDataSetChanged();
    }
}
