package biz.franch.protoi2.main_menu;

/**
 * Created by Admin on 27.07.2015.
 */
public class Banner {

    String name;
    String url;
    String image;

    public Banner(String name, String url, String image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
