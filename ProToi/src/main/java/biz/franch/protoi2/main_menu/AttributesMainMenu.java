package biz.franch.protoi2.main_menu;

/**
 * Created by Admin on 24.06.2015.
 */
public class AttributesMainMenu {
    String name;
    String description;
    String image;
    String objectId;


    public AttributesMainMenu(String name, String description, String image, String objectId) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.objectId = objectId;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
