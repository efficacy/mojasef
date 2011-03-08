package org.stringtree.mojasef.util;

import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;

public class MojasefUtils {

    public static void doNotCache(StringKeeper context) {
    	context.put(HTTPConstants.RESPONSE_HEADER + "Cache-control", "no-cache, must-revalidate");
    	context.put(HTTPConstants.RESPONSE_HEADER + "Cache-control", "no-store");
    	context.put(HTTPConstants.RESPONSE_HEADER + "Pragma", "no-cache");
    	context.put(HTTPConstants.RESPONSE_HEADER + "Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
    }

    public static void setCookie(StringKeeper context, String key, String value) {
        StorerHelper.put(context, HTTPConstants.RESPONSE_COOKIE+key, value);
    }

    public static void setCookie(StringKeeper context, String key, String value, int ttl) {
        StorerHelper.put(context, HTTPConstants.RESPONSE_COOKIE+key, value + "; path=/; ttl=" + ttl);
    }

    public static void setCookie(StringKeeper context, String key, String value, int ttl, String path) {
        StorerHelper.put(context, HTTPConstants.RESPONSE_COOKIE+key, value + "; path=" + path + "; ttl=" + ttl);
    }

    public static void clearCookie(StringKeeper context, String key) {
        StorerHelper.put(context, HTTPConstants.RESPONSE_COOKIE+key, "unknown; path=/; ttl=0");
    }

    public static void sendRedirect(StringKeeper context, String page) {
        if (!isURL(page)) {
            String baseURL = getBaseURL(context, true);
            page = baseURL + page;
        }
        
        context.put(HTTPConstants.RESPONSE_CODE, "303");
        context.put(HTTPConstants.RESPONSE_HEADER+"Location", page);
    }

    public static String getBaseURL(StringKeeper context, boolean includeMountPoint) {
        String baseURL = context.get(MojasefConstants.MOUNTCONTEXT);

        if (includeMountPoint) {
            baseURL += context.get(MojasefConstants.MOUNTPOINT);
        }
        
        return baseURL;
    }

    public static boolean isURL(String in) {
        return in.matches("[a-z]+://.*"); 
    }
}
