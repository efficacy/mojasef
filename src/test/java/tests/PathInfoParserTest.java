package tests;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.stringtree.Repository;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.PathInfoParser;

public class PathInfoParserTest extends TestCase {
    Map<String, Object> map;
    Repository repository;
    
    public void setUp() {
        map = new HashMap<String, Object>();
        map.put(MojasefConstants.MOUNTCONTEXT, "/");
        map.put(MojasefConstants.MOUNTPOINT, "");
        repository = new MapFetcher(map);
    }
    
    public void testEmpty() {
        process("");
        check("", "", "", "", "", "", "", null, "", "", false, true, true);
    }
    
    public void testFullURL() {
        process("http://www.example.com:123/application/dir/leaf?a=b&c=d");
        check("http://www.example.com:123/application/dir/leaf", "http", "www.example.com", "123", 
                "/application/dir/leaf", "application/dir/leaf", "leaf", "a=b&c=d", 
                "application", "dir/leaf", true, false, false);
    }
    
    public void testRelativeURL() {
        process("thing");
        check("thing", "", "", "", "thing", "thing", "thing", null, "thing", "", false, true, true);
    }
    
    public void testLocalURL() {
        process("/thing");
        check("/thing", "", "", "", "/thing", "thing", "thing", null, "thing", "", false, true, false);
    }
    
    public void testLocalURLWithMount() {
        map.put(MojasefConstants.MOUNTPOINT, "ugh/aa/");
        process("/ugh/aa/thing/whatever");
        check("/ugh/aa/thing/whatever", "", "", "", "/ugh/aa/thing/whatever", "thing/whatever", "whatever", null, "thing", "whatever", false, true, false);
    }

    private void check(String uri, String scheme, String host, String port, String path,
            String localpath, String leaf, String query, String object, String tail, 
            boolean isfullpath, boolean islocalpath, boolean isrelativepath) {
        
        assertEquals(uri, map.get(MojasefConstants.REQUEST_URI));
        assertEquals(scheme, map.get(MojasefConstants.REQUEST_SCHEME));
        assertEquals(host, map.get(MojasefConstants.REQUEST_HOST));
        assertEquals(port, map.get(MojasefConstants.REQUEST_PORT));
        assertEquals(path, map.get(MojasefConstants.REQUEST_PATH));
        assertEquals(localpath, map.get(MojasefConstants.REQUEST_LOCALPATH));
        assertEquals(leaf, map.get(MojasefConstants.REQUEST_LEAF));
        assertEquals(query, map.get(MojasefConstants.REQUEST_QUERY));
        assertEquals(object, map.get(MojasefConstants.REQUEST_PATHOBJECT));
        assertEquals(tail, map.get(MojasefConstants.REQUEST_PATHTAIL));
        assertEquals(Boolean.toString(isfullpath), map.get(MojasefConstants.REQUEST_ISFULLPATH));
        assertEquals(Boolean.toString(islocalpath), map.get(MojasefConstants.REQUEST_ISLOCALPATH));
        assertEquals(Boolean.toString(isrelativepath), map.get(MojasefConstants.REQUEST_ISRELATIVEPATH));
    }

    private void process(String path) {
        PathInfoParser.setContext(path, repository);
    }
}
