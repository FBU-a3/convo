package a3.com.convo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import a3.com.convo.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<String> myFriends;
    Context context;

    public FriendAdapter(List<String> friends) {
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
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        // Get friends user ID's
        String friendId = myFriends.get(position);

        // Get friends name
        holder.tvFriend.setText("Friend Name");

        ParseFile profPic = ParseUser.getCurrentUser().getParseFile("profile");
//        GlideApp.with(context)
//                .load(profPic.getUrl())
//                .circleCrop()
//                .into(holder.ivFriend);
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
}
