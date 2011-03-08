package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.finder.MapStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.apps.URLRouter;
import org.stringtree.mojasef.model.PathInfoParser;
import org.stringtree.template.ByteArrayStringCollector;
import org.stringtree.template.StringCollector;

public class FolderTestCase extends TestCase {
    URLRouter folder;
    StringKeeper context;
    StringCollector collector;
    
    public void setUp() throws IOException {
        context = new MapStringKeeper();
        collector = new ByteArrayStringCollector();
        context.put(MojasefConstants.OUTPUT_COLLECTOR, collector);
        context.put(MojasefConstants.MOUNTCONTEXT, "/");
        context.put(MojasefConstants.MOUNTPOINT, "");
    }

    public void testSpecFileLoadedOK() {
        assertEquals("value", context.getObject("foldertest.example"));
        assertEquals("", collector.toString());
    }
    
    public void testDelegationToKnownMethod() {
        PathInfoParser.setContext("/rec/a", context);
        folder.request(context);
        assertEquals("rec:a[]().", collector.toString());
    }
    
    public void testDelegationToUnknownMethod() {
        PathInfoParser.setContext("/rec/b", context);
        folder.request(context);
        assertEquals("rec:request().", collector.toString());
    }
    
    public void testDelegationToDeepMethod() {
        PathInfoParser.setContext("/deep/c/b/a", context);
        folder.request(context);
        assertEquals("deep:a[]().", collector.toString());
    }
    
    public void testDelegationWithLeafPath() {
        PathInfoParser.setContext("/deep/c/b/a/x/y", context);
        folder.request(context);
        assertEquals("deep:a[](x/y).", collector.toString());
    }
    
    public void testDelegationWithPathTokens() {
        PathInfoParser.setContext("/token/123/b/a/x/y", context);
        folder.request(context);
        assertEquals("token:a[123](x/y).", collector.toString());
    }

}
