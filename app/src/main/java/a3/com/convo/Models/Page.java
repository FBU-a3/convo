package a3.com.convo.Models;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

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
        return getString("pageId");
    }

    public void setPageId(String pageId) {
        put("pageId", pageId);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getProfUrl() {
        return getString("profUrl");
    }

    public void setProfUrl(String profUrl) {
        put("profUrl", profUrl);
    }

    public String getCoverUrl() {
        return getString("coverUrl");
    }

    public void setCoverUrl(String coverUrl) {
        put("coverUrl", coverUrl);
    }

    public String getCategory() {
        return getString("category");
    }

    public void setCategory(String category) {
        put("category", category);
    }

}
