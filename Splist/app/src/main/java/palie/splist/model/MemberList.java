package palie.splist.model;

import java.util.ArrayList;

public class MemberList {

    private String name;
    private ArrayList<Item> items;

    public MemberList() {

    }

    public MemberList(String name, ArrayList<Item> items) {
        this.items = items;
        this.name = name;
    }

    public String getName() {
        return name;
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
