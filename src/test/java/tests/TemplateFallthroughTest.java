package tests;

import java.io.IOException;

import org.stringtree.Tract;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.tract.MapTract;

import stubs.ValueIncrementer;

public class TemplateFallthroughTest extends WebAppTestCase {
	public void setUp() throws IOException {
		init("src/test/files/cc1.spec");
	}
	
	public void testSingleSlash() throws IOException {
		assertEquals("start here", request("/"));
	}
	
	public void testTemplateName() throws IOException {
		assertEquals("hello", request("/ugh"));
	}
	
	public void testTemplateWithParameters() throws IOException {
		assertEquals("hello xx,yy", request("GET", "/param?a=xx&b=yy"));
		assertEquals(200, output.responseCode);
		assertEquals("text/html", output.responseType);
		assertEquals(11, output.responseLength);
	}
	
	public void testTemplateNotFound() throws IOException {
		assertEquals("<h2>Error 404</h2>Page '/xx' not found..", request("/xx"));
		assertEquals(404, output.responseCode);
		assertEquals("text/html", output.responseType);
		assertEquals(40, output.responseLength);
	}
	
	public void testActiveTract() throws IOException {
		Tract tpl = new MapTract("val=${inc.get}, val=${inc.get}");
		putAndInit(tpl, "inc", new ValueIncrementer(7));
		common.put("template.thing", tpl);
		assertEquals("val=7, val=8", request("/thing"));
	}
    
    public void testTemplateForResultCode() throws IOException {
        common.put(MojasefConstants.HTTP_APPLICATION, new CodeResponder("302"));
        request("/whatever");
        assertEquals(302, output.responseCode);
        assertEquals("<h2>Error 302</h2>Nothing to see here", output.body);
    }
    
    public void testTemplateForResultCodeGroup() throws IOException {
        common.put(MojasefConstants.HTTP_APPLICATION, new CodeResponder("303"));
        request("/whatever");
        assertEquals(303, output.responseCode);
        assertEquals("<h2>generic 3xx</h2>Gone Fishin!", output.body);
    }
    
    public void testPageWithNoPrologueOrEpilogue() throws IOException {
        common.put("template.thing", new MapTract("hello"));
        assertEquals("hello", request("/thing"));
    }
    
    public void testPageWithDefaultPrologue() throws IOException {
        common.put("template.thing", new MapTract("hello"));
        common.put("template.page_prologue", "<<");
        assertEquals("<<hello", request("/thing"));
    }
    
    public void testPageWithDefaultEpilogue() throws IOException {
        common.put("template.thing", new MapTract("hello"));
        common.put("template.page_epilogue", ">>");
        assertEquals("hello>>", request("/thing"));
    }
    
    public void testPageWithDefaultPrologueAndEpilogue() throws IOException {
        common.put("template.thing", new MapTract("hello"));
        common.put("template.page_prologue", "<<");
        common.put("template.page_epilogue", ">>");
        assertEquals("<<hello>>", request("/thing"));
    }
    
    public void testOverridePrologue() throws IOException {
        common.put("template.thing", new MapTract("hello"));
        common.put("template.page_prologue", "<<");
        common.put("template.page_epilogue", ">>");
        common.put("template.new_prologue", "xx");
        common.put("template.new_epilogue", "yy");
        assertEquals("xxhello>>", request("/thing?prologue.template=new_prologue"));
    }
    
    public void testOverrideEpilogue() throws IOException {
        common.put("template.thing", new MapTract("hello"));
        common.put("template.page_prologue", "<<");
        common.put("template.page_epilogue", ">>");
        common.put("template.new_prologue", "xx");
        common.put("template.new_epilogue", "yy");
        assertEquals("<<helloyy", request("/thing?epilogue.template=new_epilogue"));
    }
}
