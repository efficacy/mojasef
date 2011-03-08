package org.stringtree.mojasef.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.stringtree.mojasef.model.BasicOutputCollector;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.iterator.Spliterator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ServletOutputCollector extends BasicOutputCollector {

	private HttpServletResponse res;

	public ServletOutputCollector(HttpServletResponse res, RequestContext context) {
		super(context);
		this.res = res;
	}
	
	public void setHeader(String name, String value) {
        res.setHeader(name, value);
	}

    public void setCookie(String name, String value) {
        Cookie cookie = null;
        if (value.indexOf(';') > 0) {
            Spliterator it = new Spliterator(value, ";");
            if (it.hasNext()) {
                String realValue = it.nextString();
                cookie = makeCookie(name, realValue);
                while (it.hasNext()) {
                    String property = it.nextString().trim();
                    if (property.indexOf('=') > 0) {
                        Spliterator pit = new Spliterator(property, "=");
                        String pname = pit.hasNext() ? pit.nextString().trim() : "";
                        String pvalue = pit.hasNext() ? pit.nextString().trim() : "";
                        if ("path".equals(pname)) cookie.setPath(pvalue);
                        if ("ttl".equals(pname)) cookie.setMaxAge(IntegerNumberUtils.intValue(pvalue, 0));
                    }
                }
            }
        }
        if ( null == cookie ) cookie = makeCookie(name, value);
        res.addCookie(cookie);
    }

    private Cookie makeCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        return cookie;
    }

	public void setCookie(String name, String value, String path, int secondsToLive) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(secondsToLive);
        res.addCookie(cookie);
	}

	protected void collectBody() {
			byte[] bytes = toByteArray();
			if (null != context.getObject("autovalidate")) {
			    System.err.println("ServletOutputCollector.validate: " + validateDTD(bytes));
			}

			res.setContentLength(length());
			if (!res.isCommitted()) {
                try {
                    ServletOutputStream out = res.getOutputStream();
                    if (null != out) {
                        out.write(bytes);
            			out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
	}
    
    private static boolean validateDTD(final byte[] bytes) {
        boolean ret = false;
        try {
            InputSource source = new InputSource(new ByteArrayInputStream(bytes));
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.parse(source);
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
      }

	public void setResponse(int code, String type) {
		res.setStatus(code);
		res.setContentType(type);
	}
}
