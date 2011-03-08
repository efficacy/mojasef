package tests;

import java.util.HashMap;
import java.util.Map;

import org.stringtree.mojasef.model.BasicOutputCollector;
import org.stringtree.mojasef.model.RequestContext;

public class TestOutputCollector extends BasicOutputCollector {

	public Map<String, String> cookies = new HashMap<String, String>();
	public Map<String, String> headers = new HashMap<String, String>();
	public String body;
	public int responseCode;
	public String responseType;
	public int responseLength;
	
	public TestOutputCollector(RequestContext context) {
		super(context);
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public void setCookie(String name, String value) {
		cookies.put(name, value);
	}

	protected void collectBody() {
        body = super.toString();
		responseLength = length();
	}

	public void setResponse(int code, String type) {
		responseCode = code;
		responseType = type;
	}
}
