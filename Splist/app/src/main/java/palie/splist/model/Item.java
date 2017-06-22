package palie.splist.model;

public class Item {

    private boolean checked;
    private String item;

    public Item() {}

    public Item(String item) {
        this.checked = false;
        this.item = item;
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
}
