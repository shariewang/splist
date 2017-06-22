package palie.splist.model;

import java.util.ArrayList;

public class Group {

    private String name, key;
    private int main, vibrant;
    private ArrayList<String> emails, names;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public int getMain() {
        return main;
    }

    public void setMain(int main) {
        this.main = main;
    }

    public int getVibrant() {
        return vibrant;
    }

    public void setVibrant(int vibrant) {
        this.vibrant = vibrant;
    }
}
