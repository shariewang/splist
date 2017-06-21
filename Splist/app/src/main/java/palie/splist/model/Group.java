package palie.splist.model;

import java.util.ArrayList;

public class Group {

    private String name, key;
    //private String members;
    private int main, vibrant;
    private ArrayList<String> emails, names;
//
//    public Group(String name, String imageID, List<String> members) {
//        this.name = name;
//        this.imageID = imageID;
//        this.members = members;
//    }

    public Group(String name, String key, ArrayList<String> emails, ArrayList<String> names, int main, int vibrant) {
        this.name = name;
        this.key = key;
        this.emails = emails;
        this.names = names;
        this.main = main;
        this.vibrant = vibrant;
    }

    public Group() {}

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public int getMain() {
        return main;
    }

    public int getVibrant() {
        return vibrant;
    }
}
