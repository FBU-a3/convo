package a3.com.convo.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
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
import java.util.concurrent.TimeUnit;

import a3.com.convo.Constants;
import a3.com.convo.GlideApp;
import a3.com.convo.models.Page;
import a3.com.convo.R;

/** The CardAdapter handles the display of playing cards during gameplay, it collects information on
 * liked pages and displays it one page/like at a time. The CardAdapter will change depending the
 * mode picked.
 */

public class CardAdapter extends BaseAdapter {

    private List<String> pages;
    private Context context;
    private ArrayList<String> player1Likes;
    private ArrayList<String> player2Likes;
    private ParseUser player1;
    private ParseUser player2;
    private String player1name;
    private String player2name;
    private static final String FULL_NAME = "name";

    public CardAdapter(List<String> data, Context context, ArrayList<String> player1Likes,
                       ArrayList<String> player2Likes, ParseUser player2) {
        this.pages = data;
        this.context = context;
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

    @SuppressLint("StringFormatMatches")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.item_card, viewGroup, false);
        }

        final TextView tvTopic = v.findViewById(R.id.tv_topic);
        final TextView tvUsers = v.findViewById(R.id.tv_users);
        final ImageView ivCover = v.findViewById(R.id.iv_cover);
        final TextView tvTime = (TextView) v.findViewById(R.id.tvTime);

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

                    CountDownTimer timer = new CountDownTimer(Constants.CARD_TIME, Constants.TIMER_INTERVAL) {
                        @Override
                        public void onTick(long l) {
                            tvTime.setText(
                                    String.format(context.getResources().getString(R.string.timer_format),
                                            TimeUnit.MILLISECONDS.toMinutes(l),
                                            TimeUnit.MILLISECONDS.toSeconds(l)
                                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                            );
                        }

                        @Override
                        public void onFinish() {
                            tvTime.setText(context.getResources().getString(R.string.next_card));
                        }
                    };
                    timer.start();

                    tvUsers.setText(finalUsersWhoLiked);
                    // TODO: takes far too long to load picture
                    if (object.getPageId() != null && !object.getPageId().equals("")) {
                    if (object.getPageId() != null && object.getPageId() != Constants.EMPTY_STRING && object.getCoverUrl() != null) {
                        GlideApp.with(context).load(object.getCoverUrl()).into(ivCover);
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
