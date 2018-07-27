package a3.com.convo.models;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;


/** Page class represents a Facebook page and contains its name, page ID, category,
 * and profile and cover photo URLS. When we get a page from its Graph API endpoint,
 * we store it in our Parse Server to be able to more quickly query it later on.
 * Additionally, other likes that people manually input are turned into Page objects without page IDs.
 **/
@ParseClassName("Page")
public class Page extends ParseObject{

    private static final String PAGE_ID = "pageId";
    private static final String NAME = "name";
    private static final String PROF_URL = "profUrl";
    private static final String COVER_URL = "coverUrl";
    private static final String CATEGORY = "category";

    public static Page newInstance(@Nullable String pageId, String name, @Nullable String profUrl, @Nullable String coverUrl, @Nullable String category) {
        final Page page = new Page();
        if (pageId != null)
            page.setPageId(pageId);
        page.setName(name);
        if (profUrl != null)
            page.setProfUrl(profUrl);
        if (coverUrl != null)
            page.setCoverUrl(coverUrl);
        if (category != null)
            page.setCategory(category);
        return page;
    }

    public String getPageId() {
        return getString(PAGE_ID);
    }

    public void setPageId(String pageId) {
        put(PAGE_ID, pageId);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    // getter and setter for the Page's profile picture
    public String getProfUrl() {
        return getString(PROF_URL);
    }
    public void setProfUrl(String profUrl) {
        put(PROF_URL, profUrl);
    }

    // getter and setter for the Page's cover photo
    public String getCoverUrl() {
        return getString(COVER_URL);
    }
    public void setCoverUrl(String coverUrl) {
        put(COVER_URL, coverUrl);
    }

    public String getCategory() {
        return getString(CATEGORY);
    }

    public void setCategory(String category) {
        put(CATEGORY, category);
    }

}
