package a3.com.convo.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import java.util.Arrays;
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
    private boolean isGuest;

    public CardAdapter(List<String> data, ArrayList<String> player1Likes,
                       ArrayList<String> player2Likes, ParseUser player2) {
        this.pages = data;
        this.player1Likes = player1Likes;
        this.player2Likes = player2Likes;
        this.player2 = player2;
    }

    // if user is in guest mode, nobody will be logged in
    public CardAdapter(List<String> guestData) {
        this.pages = guestData;
        isGuest = true;
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

        // just a regular old box to contain the profile picture and users who liked the topic
        final View box = v.findViewById(R.id.box);

        if (!isGuest) {
            // Get player 1 first name
            player1 = ParseUser.getCurrentUser();
            player1name = player1.getString(FULL_NAME);
            player1name = player1name.substring(0, player1name.indexOf(Constants.SPACE));
            // Get player 2 first name
            player2name = player2.getString(FULL_NAME);
            player2name = player2name.substring(0, player2name.indexOf(Constants.SPACE));
        }

        final ToggleButton toggleButton = (ToggleButton) v.findViewById(R.id.myToggleButton);
        final Drawable heart = ContextCompat.getDrawable(toggleButton.getContext(), R.drawable.facebook_bright_heart);
        DrawableCompat.setTint(heart, ContextCompat.getColor(context, R.color.color_secondary));

        toggleButton.setChecked(false);
        toggleButton.setBackgroundDrawable(heart);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DrawableCompat.setTint(heart, ContextCompat.getColor(context, R.color.like));
                    toggleButton.setBackgroundDrawable(heart);
                }
                else {
                    DrawableCompat.setTint(heart, ContextCompat.getColor(context, R.color.color_secondary));
                    toggleButton.setBackgroundDrawable(heart);
                }
            }
        });

        // getItem searches array for page, we find the rest of the information with objectId
        final String objectId = getItem(i);

        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.getInBackground(objectId, new GetCallback<Page>() {
            @Override
            public void done(Page object, ParseException e) {
                if (e == null) {
                    // super messy for now just to see if it actually works
                    if (isGuest) {
                        // remove unneeded elements and center topic TextView
                        adjustLayout(layout, Arrays.asList(tvUsers, profPic1, profPic2, box), tvTopic, false);

                        // just load topic, cover photo, and profile photo
                        tvTopic.setText(object.getName());

                        // load in the cover image and dynamically set the background with color palette
                        loadCoverBackground(object, cvCard, tvTopic, tvUsers, ivCover);

                        // load profile picture
                        loadProfilePic(object.getProfUrl(), ivProf);
                        return;
                    }

                    // user is not in guest mode if they make it here
                    String category;
                    if (object.getCategory() != null) category = object.getCategory().toLowerCase();
                    else category = Constants.EMPTY_STRING;

                    // variable for determining whose profile picture to show
                    ArrayList<ParseUser> profPics = new ArrayList<>();

                    String usersWhoLiked = determineWhoLiked(context, objectId, profPics, category);

                    tvTopic.setText(object.getName());
                    tvUsers.setText(usersWhoLiked);

                    if (object.getCategory() != null && !((object.getPageId()).equals(Constants.EMPTY_STRING)) && object.getCoverUrl() != null) {
                        // check for if activity is finishing in order to avoid crash
                        if (context instanceof PlayGameActivity && ((PlayGameActivity) context).isFinishing()) return;

                        if (context.getResources().getConfiguration() != null) {
                            int orientation = context.getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) { // if it's a real page and in portrait
                                // load in the cover image and dynamically set the background with color palette
                                loadCoverBackground(object, cvCard, tvTopic, tvUsers, ivCover);

                                // load the profile pic from the given url into the given ImageView
                                loadProfilePic(object.getProfUrl(), ivProf);

                                // load liking person's profile picture (or both if both liked the page)
                                setPlayerProfPics(profPics, profPic1, profPic2);
                            } else { // if it's a real page and in landscape
                                String coverUrl = object.getCoverUrl();
                                if (coverUrl.contains(Constants.GOOGLE_API_URL))
                                    coverUrl += ivCover.getContext().getString(R.string.google_api_key);
                                loadAsBackground(context, coverUrl, cvCard);
                            }
                        }
                    } else { // if we're dealing with an added topic or location
                        Configuration config = context.getResources().getConfiguration();
                        // locations have both page IDs and cover urls, but not categories
                        if (object.getPageId() != null && object.getCoverUrl() != null) { // if it's a location
                            // only in portrait
                            if (config != null && config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                loadCoverBackground(object, cvCard, tvTopic, tvUsers, ivCover);
                                RelativeLayout.LayoutParams topicParams = (RelativeLayout.LayoutParams) tvTopic.getLayoutParams();
                                topicParams.addRule(RelativeLayout.BELOW, R.id.iv_cover);
                                tvTopic.setLayoutParams(topicParams);

                                setPlayerProfPics(profPics, profPic1, profPic2);
                            } else {
                                loadAsBackground(context, object.getCoverUrl(), cvCard);
                            }
                        } else { // if it is an added topic (no picture, just user, topic, and fave button)
                            tvTopic.setTextColor(context.getResources().getColor(R.color.color_black));
                            if (config != null && config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                adjustLayout(layout, Arrays.asList(ivCover, (View) ivProf), tvTopic, true);
                                setPlayerProfPics(profPics, profPic1, profPic2);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) profPic1.getLayoutParams();
                                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                                int margin = (int) context.getResources().getDimension(R.dimen.card_prof_pic_margin);
                                params.setMargins(margin, 0, 0, margin);
                            } else { // landscape added topic
                                tvUsers.setTextColor(context.getResources().getColor(R.color.color_black));
                            }

                        }
                    }
                } else {
                    Log.e("name error", "Oops!");
                    e.printStackTrace();
                }
            }
        });
        return v;
    }

    private void adjustLayout(RelativeLayout layout, List<View> removeItems, TextView tv, boolean centerTopic) {
        for (View v: removeItems) if (v != null) layout.removeView(v);

        if (centerTopic) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(params);
        }
    }

    private void loadCoverBackground(Page object, final CardView cvCard, final TextView tvTopic, final TextView tvUsers, final ImageView ivCover) {
        String coverUrl = object.getCoverUrl();
        if (coverUrl.contains(Constants.GOOGLE_API_URL))
            coverUrl += ivCover.getContext().getString(R.string.google_api_key);
        GlideApp.with(cvCard.getContext())
                .asBitmap()
                .load(coverUrl)
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
    }

    private void loadAsBackground(Context context, String coverUrl, final CardView cv) {
        GlideApp.with(context)
                .load(coverUrl)
                .dontTransform()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        cv.setBackground(resource);
                    }
                });
    }

    private void loadProfilePic(String from, ImageView iv) {
        GlideApp.with(iv.getContext())
                .load(from)
                .circleCrop()
                .into(iv);
    }

    private void setPlayerProfPics(ArrayList<ParseUser> profPics, ImageView profPic1, ImageView profPic2) {
        if (profPics.contains(player1)) {
            loadProfilePic(player1.getString(Constants.PROF_PIC_URL), profPic1);
        } else {
            profPic1.setVisibility(View.INVISIBLE);
        }

        if (profPics.contains(player2)) {
            loadProfilePic(player2.getString(Constants.PROF_PIC_URL), profPic2);
        } else {
            profPic2.setVisibility(View.INVISIBLE);
        }
    }

    // Find who liked the page
    private String determineWhoLiked(Context context, String objectId, ArrayList<ParseUser> profPics, String category) {
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
        return usersWhoLiked;
    }
}
