package a3.com.convo.adapters;

import android.content.Context;
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
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import a3.com.convo.GlideApp;
import a3.com.convo.R;
import a3.com.convo.models.User;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<String> myFriends;
    Context context;

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

        // Get friends name

        GraphRequest picRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), profilePicPath, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject object = response.getJSONObject();
                String picURL = null;
                try {
                    picURL = object.getString("url");
                    GlideApp.with(context)
                            .load(picURL)
                            .circleCrop()
                            .into(holder.ivFriend);
                } catch (JSONException e) {
                    Toast.makeText(context, "Pic URL not retrieved", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        }
    }

    public void clear() {
        myFriends.clear();
        notifyDataSetChanged();
    }
}
