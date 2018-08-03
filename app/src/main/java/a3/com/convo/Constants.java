package a3.com.convo;

public class Constants {

    // length of time that each card takes to autoswipe
    public static final int CARD_SWIPE_DURATION = 80;

    // timer interval (ticks once per second)
    public static final int TIMER_INTERVAL = 1000;

    // null/default value for uninitialized longs
    public static final long LONG_NULL = 0L;

    // empty string constant
    public static final String EMPTY_STRING = "";

    // ArrayList join string
    public static final String JOIN_STRING = ", ";

    // game modes
    public static final String FREESTYLE = "freestyle";
    public static final String TIMED = "timed";

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
    public static final String PARSE_TAGGED_PLACES = "taggedPlaces";
    public static final String PARSE_HOMETOWN = "hometown";
    public static final String PARSE_LOCATION = "location";
    public static final String PAGE_ID = "pageId";
    public static final String PROF_URL = "profUrl";
    public static final String NUM_GAMES = "numGames";
    public static final String LIKES_STRING = "likesString";

    // request params fields for fetching user's likes from Graph API
    public static final String GET_LIKES_FIELDS = "likes{id,category,name,location,likes,cover,engagement,picture.type(large)}";
    public static final String LIMIT = "limit";
    public static final int LIKES_LIMIT = 20;
    public static final String FIELDS = "fields";
    public static final String GET_USER_FIELDS = "id,name,email,picture.type(large)";

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
    public static final String PLACE = "place";
    public static final String TAGGED_PLACES = "tagged_places";
    public static final String LOCATION = "location";
    public static final String HOMETOWN = "hometown";
    public static final String ENGAGEMENT = "engagement";
    public static final String SOCIAL_SENTENCE = "social_sentence";

}
