package palie.splist;

import java.util.List;

class Group {

    private String name, imageKey;
    private String members;
//    private List<String> members;
//
//    public Group(String name, String imageID, List<String> members) {
//        this.name = name;
//        this.imageID = imageID;
//        this.members = members;
//    }

    Group(String name, String imageKey, String members) {
        this.name = name;
        this.imageKey = imageKey;
        this.members = members;
    }

    public Group() {}

    public String getName() {
        return name;
    }

    public String getImageKey() {
        return imageKey;
    }

    public String getMembers() {
        return members;
    }
}
