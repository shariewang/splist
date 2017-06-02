package palie.splist;

import java.util.List;

/**
 * Created by Sharie on 6/1/2017.
 */

public class Group {

    private String name, imageID;
    private List<String> members;

    public Group(String name, String imageID, List<String> members) {
        this.name = name;
        this.imageID = imageID;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public String getImageID() {
        return imageID;
    }

    public List<String> getMembers() {
        return members;
    }
}
