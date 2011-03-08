package stubs;

import org.stringtree.mojasef.rest.Identifiable;
import org.stringtree.util.iterator.Spliterator;

public class Item implements Identifiable {

    private String id;
    private String name;

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(String tail) {
        Spliterator it = new Spliterator(tail);
        this.id = it.nextString();
        this.name = it.nextString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        return "Item[id=" + id + ",name=" + name + "]";
    }
}
