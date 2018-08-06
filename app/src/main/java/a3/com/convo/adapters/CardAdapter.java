package a3.com.convo.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import a3.com.convo.Constants;
import a3.com.convo.GlideApp;
import a3.com.convo.R;
import a3.com.convo.activities.PlayGameActivity;
import a3.com.convo.models.Page;

/** The CardAdapter handles the display of playing cards during gameplay, it collects information on
 * liked pages and displays it one page/like at a time. The CardAdapter will change depending the
 * mode picked.
 */

public class CardAdapter extends BaseAdapter {

    private List<String> pages;
    private ArrayList<String> player1Likes;
    private ArrayList<String> player2Likes;
    private ParseUser player1;
    private ParseUser player2;
    private String player1name;
    private String player2name;
    private static final String FULL_NAME = "name";

    public CardAdapter(List<String> data, ArrayList<String> player1Likes,
                       ArrayList<String> player2Likes, ParseUser player2) {
        this.pages = data;
        this.player1Likes = player1Likes;
        this.player2Likes = player2Likes;
        this.player2 = player2;
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
    public View getView(final int i, final View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        View v = view;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.item_card, viewGroup, false);
        }

        final TextView tvTopic = v.findViewById(R.id.tv_topic);
        final TextView tvUsers = v.findViewById(R.id.tv_users);
        final ImageView ivCover = v.findViewById(R.id.iv_cover);
        final CardView cvCard = v.findViewById(R.id.card_view);
        final ImageView ivProf = v.findViewById(R.id.ivProf);

        // Get player 1 first name
        player1 = ParseUser.getCurrentUser();
        player1name = player1.getString(FULL_NAME);
        player1name = player1name.substring(0, player1name.indexOf(' '));
        // Get player 2 first name
        player2name = player2.getString(FULL_NAME);
        player2name = player2name.substring(0, player2name.indexOf(' '));

        // getItem searches array for page, we find the rest of the information with objectId
        String objectId = getItem(i);

        // Find who the page is liked by
        String usersWhoLiked;
        if (player1Likes.contains(objectId) && player2Likes.contains(objectId)) {
            // If liked by both players
            usersWhoLiked = context.getResources().getString(R.string.bothUsersLike, player1name, player2name);
        } else if (player1Likes.contains(objectId)) {
            // If liked by player 1 only
            usersWhoLiked = context.getResources().getString(R.string.userWhoLikes, player1name);
        } else {
            // If liked by player 2 only
            usersWhoLiked = context.getResources().getString(R.string.userWhoLikes, player2name);
        }

        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        final String finalUsersWhoLiked = usersWhoLiked;
        query.getInBackground(objectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null) {
                    tvTopic.setText(object.getName());
                    tvUsers.setText(finalUsersWhoLiked);

                    if (object.getPageId() != null && !((object.getPageId()).equals(Constants.EMPTY_STRING)) && object.getCoverUrl() != null) {
                        // check for if activity is finishing in order to avoid crash
                        if (context instanceof PlayGameActivity && ((PlayGameActivity) context).isFinishing()) {
                            return;
                        }
                        if (context.getResources().getConfiguration() != null) {
                            int orientation = context.getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                GlideApp.with(context)
                                        .load(object.getCoverUrl())
                                        .dontTransform()
                                        .into(ivCover);
                                GlideApp.with(context)
                                        .load(object.getProfUrl())
                                        .circleCrop()
                                        .into(ivProf);
                            } else {
                                GlideApp.with(context)
                                        .load(object.getCoverUrl())
                                        .dontTransform()
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                cvCard.setBackground(resource);
                                            }
                                        });
                            }
                        }
                    }
                }
                else {
                    Log.e("name error", "Oops!");
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}
