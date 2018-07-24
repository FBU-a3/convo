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
        query.whereExists("objectId");
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
                    Toast.makeText(context, "Logged in to facebook", Toast.LENGTH_LONG).show();
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

                        try {
                            final ParseUser user = ParseUser.getCurrentUser();
                            // initialize empty likes array
                            user.put("pageLikes", new ArrayList<String>());
                            // convert Json object into Json array
                            JSONArray likes = json_object.getJSONObject("likes").optJSONArray("data");

                            for (int i = 0; i < likes.length(); i++) {
                                final JSONObject page = likes.optJSONObject(i);
                                String id = page.optString("id");

                                if (existingPages.containsKey(id)) {
                                    // page already exists in Parse, so we just get the object id and add it to their likes array
                                    user.add("pageLikes", existingPages.get(id));
                                } else {
                                    // doesn't exist yet, so we add it to the server
                                    String category = page.optString("category");
                                    String name = page.optString("name");
                                    String coverUrl = page.getJSONObject("cover").optString("source");
                                    String profUrl = page.getJSONObject("picture").getJSONObject("data").optString("url");
                                    final Page newPage = Page.newInstance(id, name, profUrl, coverUrl, category);

                                    newPage.saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.e("LoginActivity", "Create page success");
                                                user.add("pageLikes", newPage.getObjectId());
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
                            user.put("friends", new ArrayList<String>());
                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject friend = friends.optJSONObject(i);
                                String name = friend.optString("name");
                                final String id = friend.optString("id");
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo("username", id);
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> objects, ParseException e) {
                                        if (e == null) {
                                            // get the friend ParseUser with the username matching the friend of current user
                                            ParseUser friend = objects.get(0);
                                            String objectId = friend.getObjectId();
                                            user.add("friends", objectId);
                                            user.saveInBackground();
                                        } else {
                                            e.printStackTrace();
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
                            final String id = object.getString("id");
                            final String email = object.getString("email");
                            final String name = object.getString("name");
                            final String profPicUrl = object.getJSONObject("picture").getJSONObject("data").optString("url");
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("username", id);
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
        user.setPassword("password");
        user.put("name", name);
        user.put("profPicUrl", profPicUrl);
        user.put("otherLikes", new ArrayList<String>());
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
        ParseUser.logInInBackground(id, "password", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show();
                    user.put("name", name);
                    user.put("profPicUrl", profPicUrl);
                    getLikedPageInfo(access_token);
                    getFriendsOnApp(access_token);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed login (Parse)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
