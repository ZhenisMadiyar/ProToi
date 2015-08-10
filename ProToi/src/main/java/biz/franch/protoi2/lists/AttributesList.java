package biz.franch.protoi2.lists;

/**
 * Created by Admin on 25.06.2015.
 */
public class AttributesList {
    String name;
    String short_description;
    int order;
    int rating;
    String urlImage;
    String objectId;
    String hex_color;


    public AttributesList(String name, String short_description, int order, int rating,
                          String urlImage, String objectId) {
        this.name = name;
        this.short_description = short_description;
        this.order = order;
        this.rating = rating;
        this.urlImage = urlImage;
        this.objectId = objectId;
        this.hex_color = hex_color;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public String getHex_color() {
        return hex_color;
    }

    public void setHex_color(String hex_color) {
        this.hex_color = hex_color;
    }
}
