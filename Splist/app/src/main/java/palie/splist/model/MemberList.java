package palie.splist.model;

import java.util.ArrayList;

public class MemberList {

    private String name, uid;
    private ArrayList<Item> items;

    public MemberList() {

    }

    public MemberList(String uid, String name, ArrayList<Item> items) {
        this.items = items;
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
