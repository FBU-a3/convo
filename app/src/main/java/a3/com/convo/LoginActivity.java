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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

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
                LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("user_likes", "user_friends"));
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
                            // convert Json object into Json array
                            JSONArray likes = json_object.getJSONObject("likes").optJSONArray("data");

                            for (int i = 0; i < likes.length(); i++) {

                                JSONObject post = likes.optJSONObject(i);
                                String id = post.optString("id");
                                String category = post.optString("category");
                                String name = post.optString("name");
                                int count = post.optInt("likes");
                                // print id, page name and number of likes on facebook page
                                Log.e("id -", id+" name -"+name+ " category-"+
                                        category+ " likes count -" + count);
                            }

                        } catch(Exception e){

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
        Toast.makeText(context, "Going to get friends", Toast.LENGTH_LONG).show();
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

                        }
                    }
                });
        request.executeAsync();
    }
}
