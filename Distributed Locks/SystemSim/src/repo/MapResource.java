package repo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/8/14
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapResource implements Resource {

    private String name;
    private Map<String, String> entries;

    public MapResource(String name, Map<String, String> entries) {
        this.name = name;
        this.entries = entries;
    }

    public MapResource cloneResource() {
        try {
            return (MapResource)this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(String key, String value) {
        entries.put(key, value);
        return true;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MapResource{" +
                "name='" + name + '\'' +
                ", entries=" + entries +
                '}';
    }
}
