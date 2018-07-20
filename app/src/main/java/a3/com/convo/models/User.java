package a3.com.convo.models;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.List;

public class User extends ParseObject {
    private static final String KEY_FRIENDS = "friends";
    //private static final String KEY_FBFRIENDS = "fbFriends";
    //private static final String KEY_CONVOS = "convos";

    public List<String> getFriends() {
        return getList(KEY_FRIENDS);
    }

    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }
    }
}
