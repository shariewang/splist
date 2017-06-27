package palie.splist.model;

public class Item {

    private boolean checked;
    private String item, imageKey;

    public Item() {}

    public Item(String item) {
        this.checked = false;
        this.item = item;
        this.imageKey = "false";
    }

    public Item(String item, String key) {
        this.checked = false;
        this.item = item;
        this.imageKey = key;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggle() {
        checked = !checked;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String key) {
        imageKey = key;
    }
}
