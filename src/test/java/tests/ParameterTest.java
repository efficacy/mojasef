package tests;

import org.stringtree.mojasef.model.ParameterCollection;

import junit.framework.TestCase;

public class ParameterTest extends TestCase {
    ParameterCollection params;
    
    public void setUp() {
        params = new ParameterCollection();
    }
    
    public void testEmpty() {
        assertEquals("", params.asQueryString());
        assertEquals("", params.asHiddenFields());
    }
    
    public void testSingle() {
        params.put("thing", "wotsit");
        assertEquals("?thing=wotsit", params.asQueryString());
        assertEquals("<input type=\"hidden\" name=\"thing\" value=\"wotsit\"/>", params.asHiddenFields());
    }
    
    public void testSingleWithEquals() {
        params.update("thing,wotsit");
        assertEquals("?thing=wotsit", params.asQueryString());
        assertEquals("<input type=\"hidden\" name=\"thing\" value=\"wotsit\"/>", params.asHiddenFields());
    }
    
    public void testDouble() {
        params.put("thing", "wotsit");
        params.put("yee", "hah");
        assertTrue(
            "?thing=wotsit&yee=hah".equals(params.asQueryString()) || 
            "?yee=hah&thing=wotsit".equals(params.asQueryString()));
        assertTrue(
            "<input type=\"hidden\" name=\"thing\" value=\"wotsit\"/><input type=\"hidden\" name=\"yee\" value=\"hah\"/>".equals(params.asHiddenFields()) ||
            "<input type=\"hidden\" name=\"yee\" value=\"hah\"/><input type=\"hidden\" name=\"thing\" value=\"wotsit\"/>".equals(params.asHiddenFields()));
    }
    
    public void testEscaping() {
        params.put("thi ng", "wot&s\"it");
        assertEquals("?thi+ng=wot%26s%22it", params.asQueryString());
        assertEquals("<input type=\"hidden\" name=\"thi ng\" value=\"wot&amp;s&quot;it\"/>", params.asHiddenFields());
    }
    
    public void testRemove() {
        params.put("thing", "wotsit");
        params.put("yee", "hah");
        params.remove("thing");
        assertEquals("?yee=hah", params.asQueryString());
        assertEquals("<input type=\"hidden\" name=\"yee\" value=\"hah\"/>", params.asHiddenFields());
    }
}
