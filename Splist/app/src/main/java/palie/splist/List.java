package palie.splist;

/**
 * Created by Sharie on 6/13/2017.
 */

public class List {

    private String key, name;

    public List() {}
    public List(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
