package a3.com.convo;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {

    // length of time that each card takes to autoswipe
    public static final int CARD_SWIPE_DURATION = 80;

    // timer interval (ticks once per second)
    public static final int TIMER_INTERVAL = 1000;

    // null/default value for uninitialized longs
    public static final long LONG_NULL = 0L;

    // empty string constant
    public static final String EMPTY_STRING = "";

    // space char constant
    public static final char SPACE = ' ';

    // ArrayList join string
    public static final String JOIN_STRING = ", ";

    // game modes
    public static final String FREESTYLE = "freestyle";
    public static final String TIMED = "timed";

    public static final ArrayList<String> GUEST_TOPICS = new ArrayList<>(
            Arrays.asList("NfNTwueCZE", "GJG1HwMj7x", "wa2JPO1iUS",
                    "zzf2BKHbLK", "5iCIgmPEeO", "KIbHjWl64r", "C7w3f4kS7j", "x6Ro2h7JjP", "mwT5afKE1r", "BLRcokU58E"));

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
    public static final String GET_TAGGED_PLACES_FIELDS = "tagged_places{place{cover,name}}";

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

    public static final ArrayList <String>  thirty_six_questions = new ArrayList<String>() {{
        add("1. Given the choice of anyone in the world, whom would you want as a dinner guest?");
        add("2. Would you like to be famous? In what way?");
        add("3. Before making a telephone call, do you ever rehearse what you are going to say? Why?");
        add("4. What would constitute a “perfect” day for you?");
        add("5. When did you last sing to yourself? To someone else?");
        add("6. If you were able to live to the age of 90 and retain either the mind or body of a 30-year-old for the last 60 years of your life, which would you want?");
        add("7. Do you have a secret hunch about how you will die?");
        add("8. Name three things you and your partner appear to have in common.");
        add("9. For what in your life do you feel most grateful?");
        add("10. If you could change anything about the way you were raised, what would it be?");
        add("11. Take four minutes and tell your partner your life story in as much detail as possible.");
        add("12. If you could wake up tomorrow having gained any one quality or ability, what would it be?");
        add("13. If a crystal ball could tell you the truth about yourself, your life, the future or anything else, what would you want to know?");
        add("14. Is there something that you’ve dreamed of doing for a long time? Why haven’t you done it?");
        add("15. What is the greatest accomplishment of your life?");
        add("16. What do you value most in a friendship?");
        add("17. What is your most treasured memory?");
        add("18. What is your most terrible memory?");
        add("19. If you knew that in one year you would die suddenly, would you change anything about the way you are now living? Why?");
        add("20. What does friendship mean to you?");
        add("21. What roles do love and affection play in your life?");
        add("22. Alternate sharing something you consider a positive characteristic of your partner. Share a total of five items.");
        add("23. How close and warm is your family? Do you feel your childhood was happier than most other people’s?");
        add("24. How do you feel about your relationship with your mother?");
        add("25. Make three true “we” statements each. For instance, “We are both in this room feeling ... “");
        add("26. Complete this sentence: “I wish I had someone with whom I could share ... “");
        add("27. If you were going to become a close friend with your partner, please share what would be important for him or her to know.");
        add("28. Tell your partner what you like about them; be very honest this time, saying things that you might not say to someone you’ve just met.");
        add("29. Share with your partner an embarrassing moment in your life.");
        add("30. When did you last cry in front of another person? By yourself?");
        add("31. Tell your partner something that you like about them already.");
        add("32. What, if anything, is too serious to be joked about?");
        add("33. If you were to die this evening with no opportunity to communicate with anyone, what would you most regret not having told someone? Why haven’t you told them yet?");
        add("34. Your house, containing everything you own, catches fire. After saving your loved ones and pets, you have time to safely make a final dash to save any one item. What would it be? Why?");
        add("35. Of all the people in your family, whose death would you find most disturbing? Why?");
        add("36. Share a personal problem and ask your partner’s advice on how he or she might handle it. Also, ask your partner to reflect back to you how you seem to be feeling about the problem you have chosen.");
    }};

}
