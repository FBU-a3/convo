package a3.com.convo.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

@ParseClassName("Page")
public class Page extends ParseObject{

    public static Page newInstance(String pageId, String name, String profUrl, String coverUrl, String category) {
        Page page = new Page();
        page.setPageId(pageId);
        page.setName(name);
        page.setProfUrl(profUrl);
        page.setCoverUrl(coverUrl);
        page.setCategory(category);
        page.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("LoginActivity", "Create page success");
                }
                else {
                    e.printStackTrace();
                }
            }
        });
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
