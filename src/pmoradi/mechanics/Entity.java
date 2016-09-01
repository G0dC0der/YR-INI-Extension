package pmoradi.mechanics;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private final String id;
    private List<Pair> tags;

    public Entity(String id) {
        this.id = id;
        tags = new ArrayList<>(20);
    }

    public void add(String key, String value) {
        tags.add(new Pair(key, value));
    }

    public void add(Pair pair) {
        tags.add(pair);
    }

    public void add(List<Pair> pairs) {
        tags.addAll(pairs);
    }

    public void remove(String key) {
        for(int i = 0; i < tags.size(); i++) {
            if(tags.get(i).key.equals(key)) {
            tags.remove(i);
                return;
            }
        }
    }

    public String getId() {
        return id;
    }

    public List<Pair> getTags(){
        return new ArrayList<>(tags);
    }

    public boolean containsKey(String key) {
        for(Pair tag : tags)
            if(tag.key.equals(key))
                return true;
        return false;
    }

    @Override
    public String toString() {
        return id + "(" + tags.size() + " tags)";
    }
}
