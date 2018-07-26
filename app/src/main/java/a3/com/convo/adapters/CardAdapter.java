package a3.com.convo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import a3.com.convo.GlideApp;
import a3.com.convo.Models.Page;
import a3.com.convo.R;

/** The CardAdapter handles the display of playing cards during gameplay, it collects information on
 * liked pages and displays it one page/like at a time. The CardAdapter will change depending the
 * mode picked.
 */

public class CardAdapter extends BaseAdapter {

    private List<String> pages;
    private Context context;
    ArrayList<String> player1Likes;
    ArrayList<String> player2Likes;
    ParseUser player1;
    ParseUser player2;

    public CardAdapter(List<String> data, Context context, ArrayList<String> player1Likes, ArrayList<String> player2Likes) {
        this.pages = data;
        this.context = context;
        this.player1Likes = player1Likes;
        this.player2Likes = player2Likes;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public String getItem(int i) {
        return pages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.item_card, viewGroup, false);
        }

        final TextView tvTopic = v.findViewById(R.id.tvTopic);
        final TextView tvUsers = v.findViewById(R.id.tvUsers);
        final ImageView ivCover = v.findViewById(R.id.ivCover);

        // getItem searches array for page, we find the rest of the information with objectId
        String objectId = getItem(i);

        // Find who the page is liked by
        String usersWhoLiked = "Liked by: ";
        if (player1Likes.contains(objectId) && player2Likes.contains(objectId)) {
            // If liked by both players
            usersWhoLiked = usersWhoLiked + " Player 1 & 2";
        } else if (player1Likes.contains(objectId)) {
            // If liked by player 1 only
            usersWhoLiked = usersWhoLiked + " Player 1";
        } else {
            // If liked by player 2 only
            usersWhoLiked = usersWhoLiked + " Player 2";
        }

        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        final String finalUsersWhoLiked = usersWhoLiked;
        query.getInBackground(objectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null) {
                    tvTopic.setText(object.getName());
                    tvUsers.setText(finalUsersWhoLiked);
                    // TODO: takes far too long to load picture
                    if (object.getPageId() != null && !object.getPageId().equals("")) {
                        GlideApp.with(context).load(object.getCoverUrl()).into(ivCover);
                    }
                }
                else {
                    Log.e("Error displaying card.", "Oops!");
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}
