package org.stringtree.mojasef.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtree.Fetcher;
import org.stringtree.mojasef.Alias;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.StringUtils;

public class ServletRequestContext extends RequestContext {

	public ServletRequestContext(Fetcher context, Collection<Alias> aliases, String method, 
            HttpServletRequest req, HttpServletResponse res) {
		super(context, aliases);
        setRequestValues(method, req, res);
	}

	public ServletRequestContext(Fetcher context, String method, 
            HttpServletRequest req, HttpServletResponse res) {
		this(context, null, method, req, res);
	}

	void setRequestValues(String method, HttpServletRequest req, HttpServletResponse res) {
		try {
            setStreams(req.getInputStream(), res.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        put(MojasefServlet.MOJASEF_SERVLET_REQUEST, req);
        put(MojasefServlet.MOJASEF_SERVLET_RESPONSE, res);

        String self = req.getRequestURI().toString();
        put(MojasefConstants.REQUEST_SELF, self);

        String mount = StringUtils.nullToEmpty(req.getContextPath()) + StringUtils.nullToEmpty(req.getServletPath());
        if (!mount.endsWith("/")) mount += "/";
        put(MojasefConstants.MOUNTCONTEXT, mount);

        setRemoteAddress(req.getRemoteAddr());
        Cookie[] cookies = req.getCookies(); 
        if (cookies != null) for (int i = 0; i < cookies.length; ++i) {
            Cookie cookie = cookies[i];
            setCookie(cookie.getName(), cookie.getValue());
        }

        @SuppressWarnings("unchecked")
        Enumeration<String> en = req.getHeaderNames();
        while (en.hasMoreElements()) {
            String headerName = en.nextElement();
            setHeader(headerName, req.getHeader(headerName));
        }
        
        String query = req.getQueryString();
        String type = req.getHeader(HTTPConstants.CONTENT_TYPE);
        if (!StringUtils.isBlank(type)) {
            String fromBody = null;
            try {
                fromBody = readParametersFromBody(req.getInputStream(), 
                    extractContentType(type),
                    IntegerNumberUtils.intValue(req.getHeader(HTTPConstants.CONTENT_LENGTH)));
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            
            query = mergeParameters(query, fromBody);
        }
        
        setRequest(req.getProtocol(), req.getMethod(), StringUtils.nullToEmpty(self), query);
	}
}
