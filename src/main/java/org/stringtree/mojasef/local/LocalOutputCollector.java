package org.stringtree.mojasef.local;

import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.model.BasicOutputCollector;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.tract.ByteTract;

public class LocalOutputCollector extends BasicOutputCollector {

    private ByteTract response;

    public LocalOutputCollector(ByteTract response, RequestContext context) {
        super(context);
        this.response = response;
    }

    protected void collectBody() {
        response.setContentBytes(toByteArray());
    }

    public void setHeader(String name, String value) {
        response.put(name, value);
    }

    public void setCookie(String name, String value) {
        response.put(HTTPConstants.RESPONSE_COOKIE+name, value);
    }

    public void setResponse(int code, String type) {
        response.put(HTTPConstants.RESPONSE_CODE, Integer.toString(code));
        response.put(HTTPConstants.RESPONSE_CONTENT_TYPE, type);
    }
}
