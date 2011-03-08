package org.stringtree.mojasef.rest;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;

public class DefaultLookAndFeel {
    protected static final String HUMAN_FORMAT = "human";

    public void afterGet(StringKeeper context) {
        if (isHumanFormat(context)) {
            context.put(HTTPConstants.REQUEST_METHOD, "EDIT");
        }
    }

    public void afterPost(StringKeeper context) {
        String location = context.get(HTTPConstants.RESPONSE_HEADER_LOCATION);
        if (isHumanFormat(context)) {
            location += "?format=" + HUMAN_FORMAT;
        }
        context.put(HTTPConstants.RESPONSE_HEADER_LOCATION, location);
        context.put(MojasefConstants.PAGE_TEMPLATE, "303");
    }

    public void afterPut(StringKeeper context) {
        backToCollectionRoot(context);
    }

    public void afterDelete(StringKeeper context) {
        backToCollectionRoot(context);
    }

    protected void backToCollectionRoot(StringKeeper context) {
        if (isHumanFormat(context)) {
            context.put(HTTPConstants.RESPONSE_CODE, "303");
            context.put(HTTPConstants.RESPONSE_HEADER_LOCATION, context.get(MojasefConstants.APPLICATION_BASE_URL));
        }
    }

    protected boolean isHumanFormat(StringKeeper context) {
        return HUMAN_FORMAT.equals(context.get("format"));
    }
}
