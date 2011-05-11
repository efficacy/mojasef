package org.stringtree.mojasef.standalone;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.model.BasicOutputCollector;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.util.StreamUtils;

public class StandaloneOutputCollector extends BasicOutputCollector {

    private StringBuffer headers;
    private int code;
    private String type;
    
    public StandaloneOutputCollector(RequestContext context) {
        super(context);
        headers = new StringBuffer();
    }

    protected void collectBody() { }

    public void setHeader(String name, String value) {
		if (!"Content-Type".equals(name)) {
	        headers.append(name);
	        headers.append(":");
	        headers.append(value);
	        headers.append(HTTPConstants.WEB_EOL);
		}
    }

    public void setCookie(String name, String value) {
        headers.append("Set-Cookie: ");
        headers.append(name);
        headers.append("=");
        headers.append(value);
        headers.append(HTTPConstants.WEB_EOL);
    }

    /* example cookie, would be stored in context as 
            key(height) 
            value(12; path=/; expires=Thu, 15 Apr 2010 20:00:00 GMT)

            print.print("Set-Cookie: ");
            print.println("height=12; path=/; expires=Thu, 15 Apr 2010 20:00:00 GMT");
    */
//     TODO utility functions for setting cookie path, domain, and expiry in the correct format        

    public void setResponse(int code, String type) {
        this.code = code;
        this.type = type;
    }
    
    public void write(OutputStream out) throws IOException {
        write(out, toByteArray(), headers.toString());
    }
    
    public void write(OutputStream out, byte[] bytes, String headers) throws IOException {
        //ByteArrayOutputStream page = new ByteArrayOutputStream();
        PrintStream print = StreamUtils.ensurePrint(out);
        print.print("HTTP/1.0 ");
        print.print(code);
        print.print(" ");
        print.print("OK");
        print.print(HTTPConstants.WEB_EOL);
        if (null != headers) print.print(headers);
        print.print("Content-Type:");
        print.println(type);
        print.print("Content-Length:");
        print.print(null==bytes ? 0 : bytes.length);
        print.print(HTTPConstants.WEB_EOL);
        print.print(HTTPConstants.WEB_EOL);
        if (null != bytes) print.write(bytes);
        //print.flush();

        //byte[] response = page.toByteArray();
		//out.write(response);
    }

    public int getResponseCode() {
        return code;
    }
}
