package palie.splist.model;

public class List<R> {

    private String key, name, type, buyer;
    private int status;

    public List() {}
    public List(String key, String name, String type, int status) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.status = status;
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
