package org.stringtree.mojasef.apps;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;

public class Code404
{
	public static Code404 code404 = new Code404();
    
    public String request(StringKeeper context)
    {
        context.put(HTTPConstants.RESPONSE_CODE, "404");
        context.put(HTTPConstants.RESPONSE_MESSAGE, "NOT FOUND");
        context.put(MojasefConstants.PAGE_TEMPLATE, "code404");
        return "Page '" + context.getObject(MojasefConstants.REQUEST_PREFIX+"path") + "' not found..";
    }
    
    public static String respond(StringKeeper context)
    {
        return code404.request(context);
    }
}
