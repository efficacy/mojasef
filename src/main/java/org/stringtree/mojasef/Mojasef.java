package org.stringtree.mojasef;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.stringtree.Fetcher;
import org.stringtree.Storer;
import org.stringtree.fetcher.FallbackFetcher;
import org.stringtree.fetcher.FetcherBooleanHelper;
import org.stringtree.fetcher.FetcherHelper;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.FetcherStringKeeper;
import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringFinderHelper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.template.ByteArrayStringCollector;
import org.stringtree.template.StringCollector;
import org.stringtree.template.Templater;
import org.stringtree.template.WrappedFetcher;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.logging.Logger;

public class Mojasef {
	public static final String DIRSEP = "/";
	public static boolean captureSysOut = true; 
    private static final String[] prototype = new String[] {};
	
	public static Object findTemplate(StringFinder context) {
        String requestMethod = context.get(HTTPConstants.REQUEST_METHOD);
        
	    // try a directly specified template name
	    String template = context.get(MojasefConstants.PAGE_TEMPLATE);
	
	    // if that doesn't work, look for a page class, and look that one up
	    if ("".equals(template)) {
		    String pageclass = context.get(MojasefConstants.PAGE_CLASS);
		    if (!"".equals(pageclass)) {
		        template = context.get(MojasefConstants.PAGE_CLASS + "." + pageclass);
		    }
		}
	    
	    // build a list of all the possible templates to use, in priority order
	    List<String> possible = new ArrayList<String>();
	    
	    // build a list of all the possible templates to use, in priority order
	    if (!"".equals(template)) {
	        possible.add(template);
	    }
	    
	    // peel back the local part of the URL
	    String url = context.get(MojasefConstants.REQUEST_URI);
	    if (url.endsWith(DIRSEP)) url += MojasefConstants.DEFAULT_LEAF;
	    String peel = url;
	    if (peel.startsWith(DIRSEP)) peel = peel.substring(1);
	    while (peel.length() > 0) {
            possible.add(peel + "/" + requestMethod);
	        possible.add(peel);
	        int sep = peel.lastIndexOf(DIRSEP);
	        if (sep <= 0) break;
	
	        peel = peel.substring(0,sep);
	    }
	    
	    int slash = url.lastIndexOf(DIRSEP);
	    if (slash > 0) {
	    	String tail = url.substring(slash+1); 
	        possible.add(tail);
	    }
        
        String responseCode = (String)context.getObject(HTTPConstants.RESPONSE_CODE);
        if (responseCode != null) {
            possible.add("code" + responseCode);
            possible.add("codegroup" + responseCode.charAt(0) + "00");
        }
        
        possible.add(requestMethod);
	    possible.add("default");
        
	    // look up each name in the list, return the first match
	    for (String candidate : possible) {
 	        Object tpl = getTemplate(context, candidate);
	        if (tpl != null) {
                return tpl;
            }
	    }
	
	    return null;
	}

    private static Object getTemplate(StringFinder context, String name) {
        Object ret = null;
        
        Fetcher fetcher = (Fetcher) context.getObject(Templater.TEMPLATE_SOURCE);
        if (null != fetcher) ret = fetcher.getObject(name);
        if (null != ret) return ret;
        
        String fullname = Templater.TEMPLATE + "." + name;
        return FetcherHelper.getPeelback(context.getUnderlyingFetcher(), fullname);
    }

	public static String methodName(StringFinder context) {
        return context.get(MojasefConstants.REQUEST_PATHOBJECT);
	}

	public static StringFinder createSubcontext(StringFinder context, String path, String mountpoint) {
	    MapFetcher repository = new MapFetcher();
	    Fetcher subcontext = new FallbackFetcher(repository, context.getUnderlyingFetcher());
	    repository.put(MojasefConstants.MOUNTCONTEXT, getMountContext(context));
	    repository.put(MojasefConstants.MOUNTPOINT, mountpoint);
        repository.put(MojasefConstants.REQUEST_LOCALPATH, path);
	    return new FetcherStringKeeper(subcontext);
	}

	private static String getMountContext(StringFinder context) {
		return 
			context.get(MojasefConstants.MOUNTCONTEXT) + 
			context.get(MojasefConstants.MOUNTPOINT);
	}

	private static Object dx(StringCollector collector, StringFinder xcontext, Object application) {
		Object ret = Token.CONTINUE;
        StringKeeper keeper = StringFinderHelper.ensureKeeper(xcontext);
		
		String path = keeper.get(MojasefConstants.REQUEST_LOCALPATH);
		String mountpoint = keeper.get(MojasefConstants.MOUNTPOINT) + keeper.get(MojasefConstants.MOUNTPOINT);

        if (path.startsWith(mountpoint)) {
			path = path.substring(mountpoint.length());
			keeper.put(MojasefConstants.REQUEST_LOCALPATH, path);
		}

        int sep = path.indexOf(DIRSEP); 
        keeper.put(MojasefConstants.REQUEST_PATHTAIL, 
            sep > 0 ? path.substring(sep+1) : "");

		if (application != null) {
		    Object warm = MethodCallUtils.call(application, "warmup", keeper);
		    if (warm instanceof Token) return warm;
		    if (warm != null) application = warm;
	
		    String command = keeper.get(HTTPConstants.REQUEST_METHOD);
		    String method = methodName(keeper);
            //keeper.put(MojasefConstants.REQUEST_PATHOBJECT, method); 
		    String dfl = keeper.get(MojasefConstants.DEFAULT_LEAFNAME);
	
			List<String> methods = new ArrayList<String>();
			if (method != null && method.trim().length() > 0) {
				methods.add(method);
			}
			if ((path.equals("") || path.endsWith(DIRSEP)) && dfl != null) {
				methods.add(dfl);
			}
			if (command != null) {
				methods.add(command);
			}
			methods.add("request");
		    
			String[] methodNames = methods.toArray(prototype);
		        
		    ret = MethodCallUtils.call(application, methodNames, keeper, Token.CONTINUE);
		}
		
		return ret;
	}

	
	public static Token delegate(StringCollector collector, StringFinder context, Object application) {
		StringKeeper keeper = StringFinderHelper.ensureKeeper(context);
		int initial = collector.length();
		
	    PrintStream outstream = collector.printStream();
	    keeper.put(MojasefConstants.RESPONSE_STREAM, outstream);
	    Token token = Token.DONE;
	
        PrintStream oldOutput = null;
	    if (captureSysOut) oldOutput = captureSystemOut(outstream, context);
	      Object ret = Mojasef.dx(collector, keeper, application);
	      if (ret instanceof Token) {
	    	  token = (Token)ret;
	      }
	    if (captureSysOut) restoreSystemOut(oldOutput, context);
	    outstream.flush();
	    
	    int added = collector.length() - initial; 
	    if (added == 0 && ret instanceof String) {
            collector.write((String)ret);
	    }
	    
	    return token;
	}

    private static PrintStream captureSystemOut(PrintStream outstream, StringFinder context) {
        boolean allowSysoutLogging = FetcherBooleanHelper.getBoolean(context, MojasefConstants.ALLOW_SYSOUT_LOGGING);
        PrintStream oldOutput = System.out;
        if (!allowSysoutLogging) {
            System.setOut(outstream);
        }
        oldOutput.flush();
        return oldOutput;
    }

    private static void restoreSystemOut(PrintStream oldOutput, StringFinder context) {
        if (null != oldOutput && !oldOutput.equals(System.out)) {
            System.setOut(oldOutput);
        }
    }
	
	public static Token delegate(StringCollector collector, 
			StringFinder context, Object application, Object dfl) {
		StringKeeper keeper = StringFinderHelper.ensureKeeper(context);
		
		Token token = delegate(collector, keeper, application);
		if (token == Token.CONTINUE && dfl != null) {
			token = delegate(collector, keeper, dfl);
		}

		return token;
	}

	public static Token delegate(StringCollector collector, 
			StringFinder context, String applicationKey, Object dfl) {
		return delegate(collector, context, context.getObject(applicationKey), dfl);
	}

	public static Token delegate(StringFinder context, Object application) {
		StringCollector collector = (StringCollector)context.getObject(MojasefConstants.OUTPUT_COLLECTOR);
		return delegate(collector, context, application, null);
	}
    
    public static Token delegate(StringCollector collector, String name, StringFinder context) {
        Object application = context.getObject(name);
        return (application != null) ? delegate(collector, context, application) : Token.DONE;
    }
	
	public static Token delegate(String template, StringFinder context) {
		StringCollector collector = (StringCollector)context.getObject(MojasefConstants.OUTPUT_COLLECTOR);
		return delegate(collector, template, context);
	}

	public static Token delegate(StringCollector collector, StringFinder context, 
			String path, String mountpoint, Object application) {
	    return delegate(collector, createSubcontext(context, path, mountpoint), application);
	}

	public static Token delegate(StringFinder context, 
			String path, String mountpoint, Object application) {
		StringCollector collector = (StringCollector)context.getObject(MojasefConstants.OUTPUT_COLLECTOR);
	    return delegate(collector, createSubcontext(context, path, mountpoint), application);
	}

	public static Token delegate(StringFinder context, Object sys, Object dfl) {
		StringCollector collector = (StringCollector)context.getObject(MojasefConstants.OUTPUT_COLLECTOR);
		return delegate(collector, context, sys, dfl);
	}
	
	
	public static void delegateAndExpand(StringCollector collector, 
			StringFinder context, Object application, Object dfl) throws IOException {
		
		String content = null;
		Token token = Token.CONTINUE;

		Fetcher underlying = context.getUnderlyingFetcher();
		Storer storer = StorerHelper.find(context);
        storer.put(Templater.BASE, context);
        
		if (application != null) {
			StringCollector buffer = new ByteArrayStringCollector();
			token = delegate(buffer, context, application);
			content = buffer.toString();
		}
		
    	Object template = findTemplate(context);
		if (template != null) {
			if (content != null) {
				context = new FetcherStringFinder(new WrappedFetcher(content, underlying));
			}
			Templater templater = (Templater)context.getObject(Templater.TEMPLATER);
			autoTemplate(context, templater, collector, MojasefConstants.PROLOGUE_TEMPLATE);
			templater.expandTemplate(context, template, collector);
            autoTemplate(context, templater, collector, MojasefConstants.EPILOGUE_TEMPLATE);
		} else if (token == Token.CONTINUE && dfl != null) {
            delegateAndExpand(collector, context, dfl, null);
		} else {
			collector.write(content);
		}
	}

    private static void autoTemplate(StringFinder context, Templater templater, StringCollector collector, String name) {
        String tplName = (String)context.getObject(name);
        if (!StringUtils.isBlank(tplName)) {
            Object template = getTemplate(context, tplName);
            if (!StringUtils.isBlank(template)) {
                templater.expandTemplate(context, template, collector);
            }
        }
    }

	public static void delegateAndExpand(StringCollector collector, 
    		StringFinder context, String applicationKey, Object dfl) 
        throws IOException {
        delegateAndExpand(collector, context, context.getObject(applicationKey), dfl);
    }

	public static void log(String message) {
	    Logger.logger.log(message);
	}

	public static void logPart(String message) {
	    Logger.logger.logPart(message);
	}
}
