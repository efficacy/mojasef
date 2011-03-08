package org.stringtree.mojasef.model;

import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.util.StringUtils;

public class CookieKeyFinder extends GenericKeyFinder {
    public CookieKeyFinder(String cookie) {
        super(cookie);
    }
    
    public String findKey(StringFinder context, String prefix) {
        int sp = prefix.indexOf(" ");
        if (sp >= 0) prefix = prefix.substring(0,sp);
        
        String fullname = prefix + "-" + token;
        String value = context.get(HTTPConstants.REQUEST_COOKIE + fullname);

        if (StringUtils.isBlank(value)) {
            value = nextValue();
            StorerHelper.put(context, HTTPConstants.RESPONSE_COOKIE+fullname, "" + value + "; path=/; expires=Thu, 15 Apr 2010 20:00:00 GMT");
            // TODO set the path and expiry more sensibly
        }
        return context.get(HTTPConstants.REMOTE_ADDRESS)+":"+value;
    }
}
