package tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.stringtree.Tract;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.Templater;
import org.stringtree.tract.MapTract;

import stubs.StringRet;

public class TractReturnTest extends WebAppTestCase {
    Map<String, Object> params;

    public void setUp() {
        Tract t1 = new MapTract("hello");
        Tract t2 = new MapTract("hello${@" + HTTPConstants.RESPONSE_CODE +"='404'}");
        params = new HashMap<String, Object>();
        params.put(MojasefConstants.HTTP_APPLICATION, new StringRet());
        params.put(Templater.TEMPLATE + ".t1", t1);
        params.put(Templater.TEMPLATE + ".t2", t2);
        init(params);
    }

    public void testSimpleTract() throws IOException {
        assertEquals("hello", request("/t1"));
        assertEquals("200", context.getObject(HTTPConstants.RESPONSE_CODE));
    }

    public void testReturningTract() throws IOException {
        assertEquals("hello", request("/t2"));
        assertEquals("404", context.getObject(HTTPConstants.RESPONSE_CODE));
    }

}
