package tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.Templater;

import stubs.StringRet;
import stubs.SysOut;

public class SimpleAppTest extends WebAppTestCase {
	Map<String, Object> params;

	public void setUp() {
		params = new HashMap<String, Object>();
		params.put(MojasefConstants.HTTP_APPLICATION, new StringRet());
		params.put(Templater.TEMPLATE + ".pt", "hello ${a},${b}");
		params.put(Templater.TEMPLATE + ".pz", "returned ${this}, something=${something} hello ${a},${b}");
        params.put(Templater.TEMPLATE + ".qq", "hello${parameters.asQueryString}");
        params.put(Templater.TEMPLATE + ".qf", "hello${parameters.asHiddenFields}");
        params.put(Templater.TEMPLATE + ".qp", "${'a,x'|parameters.update}hello${parameters.asQueryString}");
		init(params);
	}

	public void testSingleSlash() throws IOException {
		assertEquals("StringRet index worked", request("/"));
	}
	
	public void testKnownName() throws IOException {
		assertEquals("StringRet ugh worked", request("/ugh"));
	}
	
	public void testUnknownName() throws IOException {
		assertEquals("StringRet GET worked", request("/wibble"));
	}
	
	public void testPOST() throws IOException {
		assertEquals("StringRet worked", request("POST", "/wibble"));
	}
	
	public void testParams() throws IOException {
		assertEquals("StringRet finder worked a='hello' b='world'", request("GET", "/pp?a=hello&b=world"));
	}
	
	public void testParamsImplicitTemplate() throws IOException {
		assertEquals("hello happy,world", request("/pt?a=happy&b=world"));
	}
	
	public void testParamsExplicitTemplate() throws IOException {
		assertEquals("hello super,world", request("/px?a=super&b=world"));
	}
	
	public void testParamsExplicitTemplate2() throws IOException {
		assertEquals("returned coool, something=else hello funny,world", request("/pc?a=funny&b=world"));
	}
	
	public void testParamsDynamicTemplate() throws IOException {
		assertEquals("that's strange, coool", request("/pq?a=strange&b=world"));
	}
	
	public void testSystemOut() throws IOException {
		params.put(MojasefConstants.HTTP_APPLICATION, new SysOut());
		init(params);
		assertEquals("SysOut worked", request("/"));
	}
    
    public void testArgObject() throws IOException {
        String response = request("/qq?a=b&c=dog");
        assertTrue(
            "hello?a=b&c=dog".equals(response) ||
            "hello?c=dog&a=b".equals(response));
        response = request("/qf?a=b&c=dog");
        assertTrue(
            "hello<input type=\"hidden\" name=\"a\" value=\"b\"/><input type=\"hidden\" name=\"c\" value=\"dog\"/>".equals(response) ||
            "hello<input type=\"hidden\" name=\"c\" value=\"dog\"/><input type=\"hidden\" name=\"a\" value=\"b\"/>".equals(response));
        response = request("/qp?a=b&c=dog");
        assertTrue(
            "hello?a=x&c=dog".equals(response) ||
            "hello?c=dog&a=x".equals(response));
    }
}
