package a3.com.convo.activities;

import android.app.Activity;
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

import a3.com.convo.Models.Page;
import a3.com.convo.R;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    Activity context;
    boolean onSuccessCalled;
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OBJECT_ID = "objectId";
    private static final String PAGE_LIKES = "pageLikes";
    private static final String LIKES = "likes";
    private static final String CATEGORY = "category";
    private static final String COVER = "cover";
    private static final String SOURCE = "source";
    private static final String DATA = "data";
    private static final String PICTURE = "picture";
    private static final String URL = "url";
    private static final String FRIENDS = "friends";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PROF_PIC_URL = "profPicUrl";
    private static final String OTHER_LIKES = "otherLikes";

    // maps Page IDs to Object IDs for quick lookup of duplicate pages
    HashMap<String, String> existingPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // TODO fix this quick fix to on success being called twice
        // onSuccess for login is being called twice even though login button onClick is called once
        onSuccessCalled = false;

        context = this;

        existingPages = new HashMap<>();
        ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
        query.whereExists(OBJECT_ID);
        query.findInBackground(new FindCallback<Page>() {
            @Override
            public void done(List<Page> objects, ParseException e) {
                if (e == null) {
                    assert !objects.isEmpty();
                    for (Page page: objects) {
                        existingPages.put(page.getPageId(), page.getObjectId());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            ParseUser user = ParseUser.getCurrentUser();
            // if user logged into facebook and parse
            if (user != null) {
                getLikedPageInfo(accessToken);
                getFriendsOnApp(accessToken);
                Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            }
            // if user logged into facebook but not parse
            else {
                // log them into Parse
                getUserInfo(accessToken);
            }
        }

        // if user not logged in/signed up to facebook
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("user_likes", "user_friends", "email", "user_hometown", "user_location", "user_tagged_places"));

            }


        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (!onSuccessCalled) {
                    Toast.makeText(context, R.string.login_message, Toast.LENGTH_LONG).show();
                    getUserInfo(loginResult.getAccessToken());
                    Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(i);
                    onSuccessCalled = true;
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void getLikedPageInfo(AccessToken access_token) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                access_token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        assert json_object != null;

                        try {
                            final ParseUser user = ParseUser.getCurrentUser();
                            // initialize empty likes array
                            user.put(PAGE_LIKES, new ArrayList<String>());
                            // convert Json object into Json array
                            JSONArray likes = json_object.getJSONObject(LIKES).optJSONArray(DATA);

                            for (int i = 0; i < likes.length(); i++) {
                                final JSONObject page = likes.optJSONObject(i);
                                String id = page.optString(ID);

                                if (existingPages.containsKey(id)) {
                                    // page already exists in Parse, so we just get the object id and add it to their likes array
                                    user.add(PAGE_LIKES, existingPages.get(id));
                                } else {
                                    // doesn't exist yet, so we add it to the server
                                    String category = page.optString(CATEGORY);
                                    String name = page.optString(NAME);
                                    String coverUrl = page.getJSONObject(COVER).optString(SOURCE);
                                    String profUrl = page.getJSONObject(PICTURE).getJSONObject(DATA).optString(URL);
                                    final Page newPage = Page.newInstance(id, name, profUrl, coverUrl, category);

                                    newPage.saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.e("LoginActivity", "Create page success");
                                                user.add(PAGE_LIKES, newPage.getObjectId());
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
        // add the field to get the details of liked pages
        permission_param.putString("fields", "likes{id,category,name,location,likes,cover,picture}");
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
                            final ParseUser user = ParseUser.getCurrentUser();
                            // initialize empty likes array
                            user.put(FRIENDS, new ArrayList<String>());
                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject friend = friends.optJSONObject(i);
                                String name = friend.optString(NAME);
                                final String id = friend.optString(ID);
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo(USERNAME, id);
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> objects, ParseException e) {
                                        if (objects != null && objects.size() == 1) {
                                            assert objects.size() == 1;
                                            // get the friend ParseUser with the username matching the friend of current user
                                            ParseUser friend = objects.get(0);
                                            assert friend != null;
                                            String objectId = friend.getObjectId();
                                            user.add(FRIENDS, objectId);
                                            user.saveInBackground();
                                        } else {
                                            // if the user has a friend on the app that is for some
                                            // reason not on the server as well, skip adding that
                                            // friend and continue
                                            Log.e("LoginActivity", "Friend with facebook id " + id + " could not be found on Parse server.");
                                        }
                                    }
                                });

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
                        try {
                            final String id = object.getString(ID);
                            final String email = object.getString(EMAIL);
                            final String name = object.getString(NAME);
                            final String profPicUrl = object.getJSONObject(PICTURE).getJSONObject(DATA).optString(URL);
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo(USERNAME, id);
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
                                    }
                                    else {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture");
        request.setParameters(permission_param);
        request.executeAsync();
    }

    protected void signUpNewUser(String id, String email, String name, final String profPicUrl, final AccessToken access_token) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(id);
        user.setEmail(email);
        user.setPassword(PASSWORD);
        user.put(NAME, name);
        user.put(PROF_PIC_URL, profPicUrl);
        user.put(OTHER_LIKES, new ArrayList<String>());
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
        ParseUser.logInInBackground(id, PASSWORD, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show();
                    user.put(NAME, name);
                    user.put(PROF_PIC_URL, profPicUrl);
                    getLikedPageInfo(access_token);
                    getFriendsOnApp(access_token);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed login (Parse)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
