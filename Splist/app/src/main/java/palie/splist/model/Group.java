package palie.splist.model;

public class Group {

    private String name, key;
    private String members;
    private int main, vibrant;
//    private List<String> members;
//
//    public Group(String name, String imageID, List<String> members) {
//        this.name = name;
//        this.imageID = imageID;
//        this.members = members;
//    }

    public Group(String name, String key, String members, int main, int vibrant) {
        this.name = name;
        this.key = key;
        this.members = members;
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

    public String getMembers() {
        return members;
    }

    public int getMain() {
        return main;
    }

    public int getVibrant() {
        return vibrant;
    }
}
