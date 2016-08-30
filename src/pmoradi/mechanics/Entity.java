package pmoradi.mechanics;

import java.util.LinkedList;
import java.util.List;

public class Entity {

    private final String id;
    private List<Pair> tags;

    public Entity(String id) {
        this.id = id;
        tags = new LinkedList<>();
    }

    public void add(String key, String value) {
        tags.add(new Pair(key, value));
    }

    public void add(Pair pair) {
        tags.add(pair);
    }

    public String getId() {
        return id;
    }

    public List<Pair> getTags(){
        return tags;
    }

    @Override
    public String toString() {
        return id + "(" + tags.size() + " tags)";
    }
}
