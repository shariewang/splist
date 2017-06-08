package palie.splist;

import java.util.List;

class Group {

    private String name, key;
    private String members;
//    private List<String> members;
//
//    public Group(String name, String imageID, List<String> members) {
//        this.name = name;
//        this.imageID = imageID;
//        this.members = members;
//    }

    Group(String name, String key, String members) {
        this.name = name;
        this.key = key;
        this.members = members;
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
}
