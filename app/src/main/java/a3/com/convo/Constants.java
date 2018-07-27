package a3.com.convo;

public class Constants {
    // length of game in milliseconds
    public static final int GAME_TIME = 10000;

    // time for each card in milliseconds
    public static final int CARD_TIME = 5000;

    // length of time that each card takes to autoswipe
    public static final int CARD_SWIPE_DURATION = 100;

    // timer interval (ticks once per second)
    public static final int TIMER_INTERVAL = 1000;

    // empty string constant
    public static final String EMPTY_STRING = "";

    // permissions we request from the user upon signup
    public static final String USER_LIKES = "user_likes";
    public static final String USER_FRIENDS = "user_friends";
    public static final String EMAIL = "email";
    public static final String USER_HOMETOWN = "user_hometown";
    public static final String USER_LOCATION = "user_location";
    public static final String USER_TAGGED_PLACES = "user_tagged_places";

    // Parse server fields
    public static final String PARSE_OBJECT_ID_KEY = "objectId";
    public static final String PARSE_PAGE_LIKES_KEY = "pageLikes";
    public static final String PARSE_FRIENDS_KEY = "friends";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROF_PIC_URL = "profPicUrl";
    public static final String COVER_URL = "coverUrl";
    public static final String OTHER_LIKES = "otherLikes";
    public static final String PAGE_LIKES = "pageLikes";

    // request params fields for fetching user's likes from Graph API
    public static final String GET_LIKES_FIELDS = "likes{id,category,name,location,likes,cover,picture}";
    public static final String LIMIT = "limit";
    public static final int LIKES_LIMIT = 20;
    public static final String FIELDS = "fields";
    public static final String GET_USER_FIELDS = "id,name,email,picture";

    // field names in Graph API response data object
    public static final String DATA_KEY = "data";
    public static final String ID_KEY = "id";
    public static final String LIKES_KEY = "likes";
    public static final String CATEGORY_KEY = "category";
    public static final String NAME = "name";
    public static final String COVER = "cover";
    public static final String SOURCE = "source";
    public static final String PICTURE = "picture";
    public static final String URL = "url";

}
