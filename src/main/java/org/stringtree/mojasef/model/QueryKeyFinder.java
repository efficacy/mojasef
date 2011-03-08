package org.stringtree.mojasef.model;

import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.util.StringUtils;

public class QueryKeyFinder extends GenericKeyFinder {
    public QueryKeyFinder(String token) {
        super(token);
    }

    public String findKey(StringFinder context, String prefix) {
        int sp = prefix.indexOf(" ");
        if (sp >= 0) prefix = prefix.substring(0,sp);
        
        String fullname = prefix + "-" + token;
        String value = context.get(fullname);

        if (StringUtils.isBlank(value)) {
            value = nextValue();
            StorerHelper.put(context, fullname, value);
        }
        return context.get(HTTPConstants.REMOTE_ADDRESS)+":"+value;
    }
}
