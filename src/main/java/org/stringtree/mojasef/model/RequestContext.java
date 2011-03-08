package org.stringtree.mojasef.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;

import org.stringtree.Fetcher;
import org.stringtree.fetcher.FallbackRepository;
import org.stringtree.fetcher.FetcherStringHelper;
import org.stringtree.fetcher.ListableHelper;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.fetcher.filter.StringPrefixFilter;
import org.stringtree.mojasef.Alias;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.StreamUtils;
import org.stringtree.util.StringUtils;

public class RequestContext extends FallbackRepository {
    
	private Collection<Alias> aliases;

	public RequestContext(Fetcher context, Collection<Alias> aliases) {
		super(new MapFetcher(), context);
		this.aliases = aliases;
	}

	public void setStreams(InputStream in, OutputStream out) {
		put(MojasefConstants.REQUEST_STREAM, in);
		put(MojasefConstants.RESPONSE_STREAM, out);
	}

	protected void setRemoteAddress(String addr) {
		put(HTTPConstants.REMOTE_ADDRESS, addr);
	}
    
    public void setCookie(String name, String value) {
        put(HTTPConstants.REQUEST_COOKIE+name, value);
    }
    
    public void setHeader(String name, String value) {
        put(HTTPConstants.REQUEST_HEADER+name, value);
        put(HTTPConstants.REQUEST_HEADER+(name.toLowerCase()), value);
    }

	public void setRequest(String protocol, String method, String url, String query) {
        String request = method + " " + url + " " + protocol;
        String fromUrl = null;

        url = applyAliases(url);

        int q = url.indexOf('?');
        if (q > 0) {
            fromUrl = url.substring(q+1);
            url = url.substring(0, q);
        }
        
        query = mergeParameters(fromUrl, query);

        put(HTTPConstants.HTTP_REQUEST, request);
        put(HTTPConstants.REQUEST_METHOD, method);
        put(HTTPConstants.REQUEST_PROTOCOL, protocol);
        put(MojasefConstants.REQUEST_PREFIX+"query", query);

        PathInfoParser.setContext(url, this);
        ParameterParser.parse(query, this);
        
        setLeafValues(method);
        setMountValues();
	}

    private String applyAliases(String url) {
    	if (null != aliases) {
    		for (Alias alias : aliases) {
    			Matcher matcher = alias.match(url);
				if (null != matcher) {
					url = alias.replace(matcher, url);
					if (alias.terminates()) {
						return url;
					}
	    		}
	    	}
    	}
		return url;
	}

	public String mergeParameters(String groupA, String groupB) {
        if (StringUtils.isBlank(groupA) && StringUtils.isBlank(groupB)) return "";
        if (StringUtils.isBlank(groupA)) return groupB;
        if (StringUtils.isBlank(groupB)) return groupA;
        
        return groupA + "&" + groupB;
    }

    public String readParametersFromBody(InputStream in, String type, int len ) {
        String query = null;
        if (HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED.equals(type)) {
            try {
                query = StreamUtils.readStream(in, len, false);
            } catch (IllegalStateException e) {
                e.printStackTrace(System.err);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        return query;
    }
	
    public void setRequest(String url, String query) {
        put(MojasefConstants.REQUEST_PREFIX+"query", query==null ? "" : query);

        PathInfoParser.setContext(url, this);
        ParameterParser.parse(query, this);
        setMountValues();
    }


    private void setMountValues() {
        String path = getString(MojasefConstants.REQUEST_URI);
        String mount = getString(MojasefConstants.MOUNTCONTEXT) +
            getString(MojasefConstants.MOUNTPOINT);
        if (path.startsWith(mount)) {
            path = path.substring(mount.length());
            put(MojasefConstants.REQUEST_URI, path);
        }
    }

    private void setLeafValues(String method) {
        ParameterCollection parameters = (ParameterCollection) getObject(MojasefConstants.COMMAND_ARGS_OBJECT);
        String leafname = (String)getObject(MojasefConstants.REQUEST_PREFIX+"leaf");
        boolean hasQuery = ! parameters.isEmpty();
        String query = parameters.asQueryString();

        String leafq = leafname;
        String leafplus = leafname;
        
        if ("get".equalsIgnoreCase(method)) {
            leafplus += (hasQuery ? query : "");
            leafq += (hasQuery ? query + "&" : "?");
        }

        put(MojasefConstants.REQUEST_LEAFQ, leafq);
        put(MojasefConstants.REQUEST_LEAFPLUS, leafplus);
    }

    public void setRequest(String protocol, String command, String url) {
        setRequest(protocol, command, url, null);
    }

	private String getString(String name) {
        return FetcherStringHelper.getString(this, name);
    }

    private Iterator<String> listCookies() {
		Fetcher cookies = ListableHelper.subset(this, new StringPrefixFilter(HTTPConstants.RESPONSE_COOKIE));
		return ListableHelper.list(cookies);
	}

	private Iterator<String> listHeaders() {
		Fetcher headers = ListableHelper.subset(this, new StringPrefixFilter(HTTPConstants.RESPONSE_HEADER));
		return ListableHelper.list(headers);
	}

	protected void collectCookies(CookieCollector cookies) {
        int prefix = HTTPConstants.RESPONSE_COOKIE.length();
		Iterator<String> it = listCookies();
		while (it.hasNext()) {
		    String key = it.next();
            String name = key.substring(prefix);
		    String value = (String)getObject(key);
		    
		    cookies.setCookie(name, value);
		}
	}

	protected void collectHeaders(HeaderCollector headers) {
        int prefix = HTTPConstants.RESPONSE_HEADER.length();
		Iterator<String> it = listHeaders();
		while (it.hasNext()) {
		    String key = it.next();
            String name = key.substring(prefix);
		    String value = (String)getObject(key);
	
		    headers.setHeader(name, value);
		}
	}

	public void collectResponse(ResponseCollector collector) {
		int code = IntegerNumberUtils.intValue(getObject(HTTPConstants.RESPONSE_CODE));
		String type = (String)getObject(HTTPConstants.RESPONSE_CONTENT_TYPE);
		collector.setResponse(code, type);
	}

    public String extractContentType(String type) {
        if (null == type) return type;
        
        int semicolon = type.indexOf(';');
        if (semicolon > 0) {
            String extra = type.substring(semicolon+1).trim();
            type = type.substring(0, semicolon).trim();
            put(HTTPConstants.REQUEST_CONTENT_TYPE, type);
            
            if (extra.startsWith("charset")) {
                String charset = extra.substring(8).trim();
                put(HTTPConstants.REQUEST_CONTENT_TYPE_CHARSET, charset);
            }
        }
        
        return type;
    }
}
