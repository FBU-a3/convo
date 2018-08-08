package a3.com.convo.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
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
        final CardView cvCard = v.findViewById(R.id.card_view);
        final RelativeLayout layout = v.findViewById(R.id.layout);

        // only for portrait mode, they're hidden in landscape
        final ImageView ivProf = v.findViewById(R.id.ivProf);
        final ImageView profPic1 = v.findViewById(R.id.profPic1);
        final ImageView profPic2 = v.findViewById(R.id.profPic2);
        final ImageView ivCover = v.findViewById(R.id.iv_cover);

        // Get player 1 first name
        player1 = ParseUser.getCurrentUser();
        player1name = player1.getString(FULL_NAME);
        player1name = player1name.substring(0, player1name.indexOf(Constants.SPACE));
        // Get player 2 first name
        player2name = player2.getString(FULL_NAME);
        player2name = player2name.substring(0, player2name.indexOf(Constants.SPACE));

        // getItem searches array for page, we find the rest of the information with objectId
        final String objectId = getItem(i);


        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.getInBackground(objectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null) {
                    String category;
                    if (object.getCategory() != null) category = object.getCategory().toLowerCase();
                    else category = Constants.EMPTY_STRING;

                    // variable for determining whose profile picture to show
                    ArrayList<ParseUser> profPics = new ArrayList<>();

                    // Find who the page is liked by
                    String usersWhoLiked;
                    if (player1Likes.contains(objectId) && player2Likes.contains(objectId)) {
                        // If liked by both players
                        usersWhoLiked = context.getResources().getString(R.string.bothUsersLike, player1name, player2name, category);
                        profPics.add(player1);
                        profPics.add(player2);
                    } else if (player1Likes.contains(objectId)) {
                        // If liked by player 1 only
                        usersWhoLiked = context.getResources().getString(R.string.userWhoLikes, player1name, category);
                        profPics.add(player1);
                    } else {
                        // If liked by player 2 only
                        usersWhoLiked = context.getResources().getString(R.string.userWhoLikes, player2name, category);
                        profPics.add(player2);
                    }
                    tvTopic.setText(object.getName());
                    tvUsers.setText(usersWhoLiked);

                    if (object.getCategory() != null && !((object.getPageId()).equals(Constants.EMPTY_STRING)) && object.getCoverUrl() != null) {
                        // check for if activity is finishing in order to avoid crash
                        if (context instanceof PlayGameActivity && ((PlayGameActivity) context).isFinishing()) {
                            return;
                        }
                        if (context.getResources().getConfiguration() != null) {
                            int orientation = context.getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                GlideApp.with(context)
                                        .asBitmap()
                                        .load(object.getCoverUrl())
                                        .listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                Log.e("CardCover", "Cover image didn't load.");
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                if (resource != null) {
                                                    Palette palette = Palette.from(resource).generate();
                                                    Palette.Swatch swatch = palette.getLightMutedSwatch();
                                                    if (swatch != null) {
                                                        cvCard.setBackgroundColor(swatch.getRgb());
                                                        tvTopic.setTextColor(swatch.getTitleTextColor());
                                                        tvUsers.setTextColor(swatch.getBodyTextColor());
                                                    }
                                                }
                                                return false;
                                            }
                                        })
                                        .into(ivCover);

                                GlideApp.with(context)
                                        .load(object.getProfUrl())
                                        .circleCrop()
                                        .into(ivProf);

                                // load liking person's profile picture (or both if both liked the page)
                                if (profPics.contains(player1)) {
                                    GlideApp.with(context)
                                            .load(player1.getString(Constants.PROF_PIC_URL))
                                            .circleCrop()
                                            .into(profPic1);
                                } else {
                                    profPic1.setVisibility(View.INVISIBLE);
                                }

                                if (profPics.contains(player2)) {
                                    GlideApp.with(context)
                                            .load(player2.getString(Constants.PROF_PIC_URL))
                                            .circleCrop()
                                            .into(profPic2);
                                } else {
                                    profPic2.setVisibility(View.INVISIBLE);
                                }
                            } else { // the screen is in landscape
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
                    } else { // if we're dealing with an added topic or location
                        // remove the other views and center the topic (either additional like or location)
                        layout.removeView(ivCover); // this one can be added back in for locations with covers once we grab the url
                        layout.removeView(ivProf);
                        layout.removeView(tvUsers);
                        layout.removeView(profPic1);
                        layout.removeView(profPic2);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvTopic.getLayoutParams();
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        // TODO: make this show up in landscape mode
                        tvTopic.setLayoutParams(params);
                    }
                } else {
                    Log.e("name error", "Oops!");
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}
