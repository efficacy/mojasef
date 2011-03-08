package tests;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.finder.MapStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.PathInfoParser;
import org.stringtree.mojasef.rest.RESTCollection;

import stubs.Item;
import stubs.Items;

public class RESTTest extends TestCase {
    StringKeeper context;
    Items items;
    RESTCollection rest;
    
    public void setUp() throws IOException {
        context = new MapStringKeeper();
        items = new Items();
        rest = new RESTCollection(items);
    }
    
    public void testGETUnknownIdReturnsError() {
        PathInfoParser.setContext("/99", context);
        rest.GET(context);
        assertEquals("404", context.get(HTTPConstants.RESPONSE_CODE));
        assertNull(context.getObject("this"));
    }
    
    public void testGETKnownIdReturnsItem() {
        PathInfoParser.setContext("123", context);
        items.put("123", new Item("123", "Frank"));
        rest.GET(context);
        Object object = context.getObject("this"); 
        assertTrue(object instanceof Item);
        Item item = (Item)object;
        assertEquals("123", item.getId());
        assertEquals("Frank", item.getName());
    }
    
    public void testPUTReturnsUpdatedItem() throws IOException {
        PathInfoParser.setContext("123", context);
        context.put(MojasefConstants.REQUEST_STREAM, new FileInputStream("src/test/input/item1.xml"));
        rest.PUT(context);
        Object object = context.getObject("this"); 
        assertTrue(object instanceof Item);
        Item item = (Item)object;
        assertEquals("123", item.getId());
        assertEquals("Margaret", item.getName());
    }
    
    public void testPUTthenGETReturnsUpdatedItem() throws IOException {
        testPUTReturnsUpdatedItem();
        context.remove("success");
        context.remove("this");
        rest.GET(context);
        Object object = context.getObject("this"); 
        assertTrue(object instanceof Item);
        Item item = (Item)object;
        assertEquals("123", item.getId());
        assertEquals("Margaret", item.getName());
    }
    
    public void testPOSTReturnsNewId() {
        rest.POST(context);
        assertEquals("303", context.get(HTTPConstants.RESPONSE_CODE));
        assertEquals("9901", context.get(HTTPConstants.RESPONSE_HEADER + "Location"));

        rest.POST(context);
        assertEquals("303", context.get(HTTPConstants.RESPONSE_CODE));
        assertEquals("9902", context.get(HTTPConstants.RESPONSE_HEADER + "Location"));
    }
    
    public void testDELETERemovesItem() {
        PathInfoParser.setContext("123", context);
        items.put("123", new Item("123", "Frank"));
        assertNotNull(items.getObject("123"));
        rest.DELETE(context);
        assertNull(items.getObject("123"));
    }
}
