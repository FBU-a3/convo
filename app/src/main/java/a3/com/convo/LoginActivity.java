package a3.com.convo;

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
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                Toast.makeText(context, "Logged in successfully", Toast.LENGTH_LONG).show();
                getIdAndEmail(loginResult);
                getLikedPageInfo(loginResult);
                getFriendsOnApp(loginResult);
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("user_likes", "user_friends", "email"));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void getLikedPageInfo(LoginResult login_result) {

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        try {
                            ParseUser user = ParseUser.getCurrentUser();
                            // initialize empty likes array
                            // user.put("pageLikes", new ArrayList<String>());
                            // convert Json object into Json array
                            JSONArray likes = json_object.getJSONObject("likes").optJSONArray("data");

                            for (int i = 0; i < likes.length(); i++) {

                                JSONObject page = likes.optJSONObject(i);
                                String id = page.optString("id");
                                String category = page.optString("category");
                                String name = page.optString("name");
                                int count = page.optInt("likes");
                                // print id, page name and number of likes on facebook page
                                Log.e("id -", id+" name -"+name+ " category-"+
                                        category+ " likes count -" + count);
                                // ParseUser.getCurrentUser().add("pageLikes", )
                            }

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        // add the field to get the details of liked pages
        permission_param.putString("fields", "likes{id,category,name,location,likes}");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    protected void getFriendsOnApp(LoginResult login_result) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray friends, GraphResponse response) {
                        try {
                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject friend = friends.optJSONObject(i);
                                String name = friend.optString("name");
                                String id = friend.optString("id");
                                Log.e("id ", id + "name: " + name);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();
    }

    protected void getIdAndEmail(LoginResult login_result) {
        GraphRequest request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            final String id = object.getString("id");
                            final String email = object.getString("email");
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("username", id);
                            query.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.isEmpty()) {
                                            signUpNewUser(id, email);
                                        }
                                        else {
                                            logInUser(id);
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
        permission_param.putString("fields", "id,name,email");
        request.setParameters(permission_param);
        request.executeAsync();
    }

    protected void signUpNewUser(String id, String email) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(id);
        user.setEmail(email);
        user.setPassword("password");
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "Signed up (Parse)!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Username taken or some other issue!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void logInUser(String id) {
        ParseUser.logInInBackground(id, "password", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed login (Parse)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
