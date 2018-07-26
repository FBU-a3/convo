package a3.com.convo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import a3.com.convo.Constants;
import a3.com.convo.Models.Page;
import a3.com.convo.R;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Context context;

    // maps Page IDs to Object IDs for quick lookup of duplicate pages
    private HashMap<String, String> existingPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        // populate the existing pages HashMap from the Parse server
        existingPages = new HashMap<>();
        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.whereExists(Constants.PARSE_OBJECT_ID_KEY);
        query.findInBackground(new FindCallback<Page>() {
            @Override
            public void done(List<Page> objects, ParseException e) {
                if (e == null) {
                    for (Page page: objects) {
                        existingPages.put(page.getPageId(), page.getObjectId());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        // check to see if the user is already logged in
        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            ParseUser user = ParseUser.getCurrentUser();
            // if user logged into Facebook and Parse, then refresh their info and send them to the home screen
            if (user != null) {
                getLikedPageInfo(accessToken);
                getFriendsOnApp(accessToken);
                Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            }
            // if user logged into Facebook but not Parse, then get their info and log them into Parse
            else {
                getUserInfo(accessToken);
            }
        }

        // if user is not logged in/signed up to Facebook, the button shows up
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList(Constants.USER_LIKES,
                                Constants.USER_FRIENDS,
                                Constants.EMAIL,
                                Constants.USER_HOMETOWN,
                                Constants.USER_LOCATION,
                                Constants.USER_TAGGED_PLACES));

            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(context, "Logged in to facebook", Toast.LENGTH_LONG).show();
                getUserInfo(loginResult.getAccessToken());
                Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
                startActivity(i);
            }

            @Override
            public void onCancel() {
                Log.e("LoginActivity", "Facebook login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("LoginActivity", "Facebook login error: " + exception.toString());
                exception.printStackTrace();
            }
        });
    }


    // called when Facebook login returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // pulls a user's likes from the Graph API "likes" edge
    protected void getLikedPageInfo(AccessToken access_token) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                access_token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        try {
                            final ParseUser user = ParseUser.getCurrentUser();
                            // initialize empty likes array
                            user.put(Constants.PARSE_PAGE_LIKES_KEY, new ArrayList<String>());
                            // convert Json object into Json array
                            JSONArray likes = json_object.getJSONObject(Constants.LIKES_KEY).optJSONArray(Constants.DATA_KEY);

                            // for each page, add it to our server if it's not there and add it to the user's likes array
                            for (int i = 0; i < likes.length(); i++) {
                                final JSONObject page = likes.optJSONObject(i);
                                String id = page.optString(Constants.ID_KEY);

                                if (existingPages.containsKey(id)) {
                                    // page already exists in Parse, so we just get the object id and add it to their likes array
                                    user.add(Constants.PARSE_PAGE_LIKES_KEY, existingPages.get(id));
                                } else {
                                    // page doesn't exist yet, so we add it to the server
                                    String category = page.optString(Constants.CATEGORY_KEY);
                                    String name = page.optString(Constants.NAME);
                                    String coverUrl = page.getJSONObject(Constants.COVER).optString(Constants.SOURCE);
                                    String profUrl = page.getJSONObject(Constants.PICTURE).getJSONObject(Constants.DATA_KEY).optString(Constants.URL);
                                    final Page newPage = Page.newInstance(id, name, profUrl, coverUrl, category);

                                    newPage.saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.e("LoginActivity", "Create page success");
                                                user.add(Constants.PARSE_PAGE_LIKES_KEY, newPage.getObjectId());
                                                user.saveInBackground();
                                            }
                                            else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });

        Bundle permission_param = new Bundle();
        // add fields to get the details of liked pages
        permission_param.putString(Constants.FIELDS, Constants.GET_LIKES_FIELDS);
        // grab more than 25 pages
        permission_param.putString(Constants.LIMIT, Integer.toString(Constants.LIKES_LIMIT));
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }


    protected void getFriendsOnApp(AccessToken access_token) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                access_token,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray friends, GraphResponse response) {
                        try {
                            ParseUser user = ParseUser.getCurrentUser();
                            // initialize empty likes array
                            user.put(Constants.PARSE_FRIENDS_KEY, new ArrayList<String>());
                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject friend = friends.optJSONObject(i);
                                String name = friend.optString(Constants.NAME);
                                String id = friend.optString(Constants.ID_KEY);
                                user.add(Constants.PARSE_FRIENDS_KEY, id);
                                user.saveInBackground();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();
    }



    protected void getUserInfo(final AccessToken access_token) {
        GraphRequest request = GraphRequest.newMeRequest(
                access_token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            try {
                                final String id = object.getString(Constants.ID_KEY);
                                final String email = object.getString(Constants.EMAIL);
                                final String name = object.getString(Constants.NAME);
                                final String profPicUrl = object.getJSONObject(Constants.PICTURE).getJSONObject(Constants.DATA_KEY).optString(Constants.URL);
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo(Constants.USERNAME, id);
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> objects, ParseException e) {
                                        if (e == null) {
                                            // if the user doesn't exist
                                            if (objects.isEmpty()) {
                                                signUpNewUser(id, email, name, profPicUrl, access_token);
                                            }
                                            // if they're already in our server
                                            else {
                                                logInUser(id, name, profPicUrl, access_token);
                                            }
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString(Constants.FIELDS, Constants.GET_USER_FIELDS);
        request.setParameters(permission_param);
        request.executeAsync();
    }

    protected void signUpNewUser(String id, String email, String name, final String profPicUrl, final AccessToken access_token) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(id);
        user.setEmail(email);
        user.setPassword(Constants.PASSWORD);
        user.put(Constants.NAME, name);
        user.put(Constants.PROF_PIC_URL, profPicUrl);
        user.put(Constants.OTHER_LIKES, new ArrayList<String>());
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "Signed up (Parse)!", Toast.LENGTH_LONG).show();

                    getLikedPageInfo(access_token);
                    getFriendsOnApp(access_token);
                } else {
                    Toast.makeText(LoginActivity.this, "Username taken or some other issue!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void logInUser(String id, final String name, final String profPicUrl, final AccessToken access_token) {
        ParseUser.logInInBackground(id, Constants.PASSWORD, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show();
                    user.put(Constants.NAME, name);
                    user.put(Constants.PROF_PIC_URL, profPicUrl);
                    getLikedPageInfo(access_token);
                    getFriendsOnApp(access_token);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed login (Parse)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
