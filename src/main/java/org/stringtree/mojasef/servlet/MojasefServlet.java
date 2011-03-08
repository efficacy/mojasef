package org.stringtree.mojasef.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.mojasef.model.Server;

@SuppressWarnings("serial")
public class MojasefServlet extends HttpServlet {
    
    public static final String MOJASEF_SERVER = "mojasef.server.instance";
    public static final String MOJASEF_SERVLET_INSTANCE_COUNT = "mojasef.servlet.instance.count";
    public static final String MOJASEF_SERVLET_REQUEST = "mojasef.servlet.request";
    public static final String MOJASEF_SERVLET_RESPONSE = "mojasef.servlet.response";

    private Server server;
    private Map<String, Object> overrides;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        
        String servletName = getServletName();
        String id = MOJASEF_SERVER + "." + servletName;
        
        synchronized (this) {
            server = (Server)servletContext.getAttribute(id);
            if (null == server) {
                String spec = getSpec(config);
                try {
                    server = new Server(spec, false);
                    CommonContext commonContext = server.getCommonContext();
                    if (null != overrides) {
                        commonContext.putAll(overrides);
                    }
                    if (!commonContext.contains(MojasefConstants.MOJASEF_SERVER_NAME)) {
                        commonContext.put(MojasefConstants.MOJASEF_SERVER_NAME, servletName);
                    }
                    servletContext.setAttribute(id, server);

                    Integer obj = (Integer)servletContext.getAttribute(MOJASEF_SERVLET_INSTANCE_COUNT);
                    int icount = (obj != null) ? obj.intValue() + 1 : 1;
                    servletContext.setAttribute(MOJASEF_SERVLET_INSTANCE_COUNT, new Integer(icount));

            		@SuppressWarnings("unchecked")
                    Enumeration<String> at = servletContext.getAttributeNames();
                    while (at.hasMoreElements()) {
                        String name = at.nextElement();
                        Object value = servletContext.getAttribute(name);
                        commonContext.put(name, value);
                    }
                    
            		@SuppressWarnings("unchecked")
                    Enumeration<String> pn = config.getInitParameterNames();
                    while (pn.hasMoreElements()) {
                        String name = pn.nextElement();
                        Object value = config.getInitParameter(name);
                        commonContext.put(name, value);
                    }
                } catch (IOException e) {
                    throw new ServletException(e);
                }
            }

        }
    }

    protected String getSpec(ServletConfig config) {
        return getInitParameter("mojasef.config.URL", "classpath:http.spec");
    }

    public synchronized void destroy() {
        Integer obj = (Integer)getServletContext().getAttribute(MOJASEF_SERVLET_INSTANCE_COUNT);
        int icount = 0;
        if (obj != null) {
            icount = obj.intValue();
            if (icount > 0) icount -= 1;
        }
        getServletContext().setAttribute(MOJASEF_SERVLET_INSTANCE_COUNT, new Integer(icount));

        if (0 == icount) {
            server.terminate();
        }
    }

	public void doRequest(String method, HttpServletRequest req, HttpServletResponse res) {
	    StringBuffer fullRequest = req.getRequestURL();
	    String query = req.getQueryString();
	    if (null != query) {
            fullRequest.append('?');
	        fullRequest.append(query);
	    }
		if (null == server.getCommonContext().getObject("silent.running")) {
		    System.out.println(method + " " + fullRequest.toString());
		}

    	RequestContext context = new ServletRequestContext(server.getCommonContext(), method, req, res);
		ServletOutputCollector output = new ServletOutputCollector(res, context);
        server.request(context, output);
    }
 
    public void service(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        doRequest(req.getMethod(), req, res);
    }

    public String getServletInfo() {
        return (String)server.getCommonContext().getObject(MojasefConstants.APPLICATION_NAME);
    }
    
    private String getInitParameter(String name, String dfl) {
        String ret = getServletConfig().getInitParameter(name);
        if (ret == null) {
            ret = dfl;
        }
        return ret;
    }
    
    public void override(String name, Object value) {
        ensureOverrides();
        overrides.put(name, value);
    }

    private synchronized void ensureOverrides() {
        if (null == overrides) overrides = new HashMap<String, Object>();
    }
}