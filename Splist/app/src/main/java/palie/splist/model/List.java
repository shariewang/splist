package palie.splist.model;

public class List {

    private String key, name, type;

    public List() {}
    public List(String key, String name, String type) {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
