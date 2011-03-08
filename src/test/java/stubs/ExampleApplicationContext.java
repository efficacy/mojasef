package stubs;

import org.stringtree.Fetcher;

public class ExampleApplicationContext implements Fetcher {

    public Object getObject(String name) {
        if ("key2".equals(name)) return "value2";
        return null;
    }

}
