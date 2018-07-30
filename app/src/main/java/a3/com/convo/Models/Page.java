package a3.com.convo.Models;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import a3.com.convo.Constants;


/** Page class represents a Facebook page and contains its name, page ID, category,
 * and profile and cover photo URLS. When we get a page from its Graph API endpoint,
 * we store it in our Parse Server to be able to more quickly query it later on.
 * Additionally, other likes that people manually input are turned into Page objects without page IDs.
 **/
@ParseClassName("Page")
public class Page extends ParseObject{

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
        return getString(Constants.PAGE_ID);
    }

    public void setPageId(String pageId) {
        put(Constants.PAGE_ID, pageId);
    }

    public String getName() {
        return getString(Constants.NAME);
    }

    public void setName(String name) {
        put(Constants.NAME, name);
    }

    // getter and setter for the Page's profile picture
    public String getProfUrl() {
        return getString(Constants.PROF_URL);
    }
    public void setProfUrl(String profUrl) {
        put(Constants.PROF_URL, profUrl);
    }

    // getter and setter for the Page's cover photo
    public String getCoverUrl() {
        return getString(Constants.COVER_URL);
    }
    public void setCoverUrl(String coverUrl) {
        put(Constants.COVER_URL, coverUrl);
    }

    public String getCategory() {
        return getString(Constants.CATEGORY_KEY);
    }

    public void setCategory(String category) {
        put(Constants.CATEGORY_KEY, category);
    }

}
