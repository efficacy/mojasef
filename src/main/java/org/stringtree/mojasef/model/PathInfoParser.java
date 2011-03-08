package org.stringtree.mojasef.model;

import java.util.StringTokenizer;

import org.stringtree.Repository;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.BooleanUtils;

public class PathInfoParser
{
    protected static final int ERROR = 0;
	protected static final int SCHEME = 1;
	protected static final int MAYBE = 2;
	protected static final int HOST = 3;
	protected static final int PORT = 4;
	protected static final int PATH = 5;
	protected static final int EXTRA = 6;
	protected static final int QUERY = 7;
	
	protected static final String SSEP = ":";
	protected static final String PSEP = "/";
	protected static final String QSEP = "?";
	
	public static void setContext(String full, Repository context)
	{
		int state = SCHEME;
		
        String mountcontext = (String)context.getObject(MojasefConstants.MOUNTCONTEXT);
        String mountpoint = (String)context.getObject(MojasefConstants.MOUNTPOINT);
        String mount = mountcontext + mountpoint;
        
		String URI = "";
		String scheme = "";
		String host = "";
		String port = "";
		String maybe = "";
		String path = "";
        String object = "";
        String tail = "";
		String leafname = "";
		String query = "";
		String localpath = "";
		boolean local = true;

		StringTokenizer tok = new StringTokenizer(full, PSEP + QSEP + SSEP, true);
		while (tok.hasMoreTokens())
		{
			String s = tok.nextToken();
			
			switch (state)
			{
			case SCHEME:	
				if (s.equals(SSEP))
				{
					URI += s;
					state = MAYBE;
				}
				else if (s.equals(PSEP))
				{
					URI += s;
					if (scheme.length() > 0)
					{
						path += scheme + s;
						scheme = "";
						state = EXTRA;
					}
					else
					{
						maybe += s;
						state = MAYBE;
					}
				}
				else if (s.equals(QSEP))
				{
					if (scheme.length() > 0)
					{
						path += scheme;
						scheme = "";
						state = QUERY;
					}
					else
					{
						state = QUERY;
					}
				}
				else
				{
					URI += s;
					scheme += s;
				}
				break;

			case MAYBE:	
				URI += s;
				if (s.equals(PSEP))
				{
					if (maybe.equals(""))
					{
						maybe += s;
					}
					else if (maybe.equals(PSEP))
					{
						maybe = "";
						state = HOST;
					}
				}
				else
				{
					path += maybe + s;
					maybe = "";
					state = PATH;
				}
				break;

			case HOST:	
				if (s.equals(PSEP))
				{
					URI += s;
					path += s;
					state = PATH;
				}
				else if (s.equals(QSEP))
				{
					state = QUERY;
				}
				else if (s.equals(SSEP))
				{
					URI += s;
					state = PORT;
				}
				else
				{
					URI += s;
					host += s;
				}
				break;
				
			case PORT:
				if (s.equals(PSEP))
				{
					URI += s;
					path += s;
					state = PATH;					
				}
				else if (s.equals(QSEP))
				{
					state = QUERY;
				}
				else
				{
					URI += s;
					port += s;
				}
				break;

			case PATH:
				if (s.equals(PSEP))
				{
                    URI += s;
					path += s;
					state = EXTRA;
				}
				else if (s.equals(QSEP))
				{
					state = QUERY;
				}
				else
				{
                    URI += s;
					path += s;
				}
				break;

			case EXTRA:
				if (s.equals(QSEP))
				{
					state = QUERY;
				}
				else
				{
					URI += s;
					path += s;
				}
				break;
				
			case QUERY:
				query += s;
				break;
			}
			
		}
		
		// handle the case of just a '/' which gets stuck
		// in "maybe" wayting for the next character
		//
		if (!maybe.equals(""))
		{
			path += maybe;
            URI += maybe;
		}
		
		// handle the case of a simple filename which gets stuck
		// in "scheme"
		if (scheme.length() > 0 && host.equals("") && path.equals(""))
		{
			path = scheme;
			scheme = "";
		}
		
		// work out if it is a local path
		//
		if (!scheme.equals("") || !host.equals(""))
		{
			local = false;
		}
		else
		{
			local = true;
		}
		
        if (path.startsWith(mount)) {
            localpath = path.substring(mount.length());
        } else {
            localpath = path;
        }
        
        int slash = localpath.indexOf('/');
        object = slash > -1
            ? localpath.substring(0, slash)
            : localpath;

        tail = localpath.startsWith(object + PSEP)
            ? localpath.substring(object.length()+1)
            : "";
            
		int lastSlash = localpath.lastIndexOf(PSEP);
		if (lastSlash >= 0) {
			leafname = localpath.substring(lastSlash+1);
		} else {
			leafname = localpath;
		}
            
        boolean hasQuery = !"".equals(query);
        
		context.put(MojasefConstants.REQUEST_URI, URI);
		context.put(MojasefConstants.REQUEST_SCHEME, scheme);
		context.put(MojasefConstants.REQUEST_HOST, host);
		context.put(MojasefConstants.REQUEST_PORT, port);
		context.put(MojasefConstants.REQUEST_PATH, path);
		context.put(MojasefConstants.REQUEST_LOCALPATH, localpath);
		context.put(MojasefConstants.REQUEST_LEAF, leafname);
        context.put(MojasefConstants.REQUEST_PATHOBJECT, object);
        context.put(MojasefConstants.REQUEST_PATHTAIL, tail);
		context.put(MojasefConstants.REQUEST_ISFULLPATH, BooleanUtils.toString(!local));
		context.put(MojasefConstants.REQUEST_ISLOCALPATH, BooleanUtils.toString(local));
		context.put(MojasefConstants.REQUEST_ISRELATIVEPATH, BooleanUtils.toString(local && !path.startsWith("/")));
        if (hasQuery) context.put(MojasefConstants.REQUEST_QUERY, query);
    }
	
	public static void rebase(Repository context) {
		rebase(
				context,
				(String) context.getObject(MojasefConstants.REQUEST_PATHOBJECT),
				(String) context.getObject(MojasefConstants.MOUNTPOINT),
				(String) context.getObject(MojasefConstants.REQUEST_LOCALPATH),
				(String) context.getObject(MojasefConstants.REQUEST_PATHTAIL)
			);
	}
	
	public static void rebase(Repository context, String object, String mount, String local, String tail) {
	    String prefix = object + "/";
        context.put(MojasefConstants.MOUNTPOINT, mount + prefix);
        context.put(MojasefConstants.REQUEST_LOCALPATH, shift(local, prefix));
        context.put(MojasefConstants.REQUEST_PATHTAIL, shift(tail, prefix));
        context.put(MojasefConstants.REQUEST_PATHOBJECT, "");
	}
	
	public static Object shift(Object value, String prefix) {
	    if (!(value instanceof String)) return value;
	    String string = (String)value;
	    if (string.startsWith(prefix)) {
	        string = string.substring(prefix.length());
	    }
	    return string;
	}
}