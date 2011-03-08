package org.stringtree.mojasef.local;

import java.net.URLEncoder;
import java.util.Map;

import org.stringtree.mojasef.HTTPConstants;

public class LocalPostRequest extends LocalRequest {

    @SuppressWarnings("deprecation")
	private String encode(Map<String, String> params) {
        StringBuffer encoded = new StringBuffer();
        boolean had = false;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (had) encoded.append("&");
            encoded.append(URLEncoder.encode(entry.getKey()));
            encoded.append("=");
            encoded.append(URLEncoder.encode(entry.getValue()));
            had = true;
        }
        return encoded.toString();
    }

    public LocalPostRequest(String path) {
        super("", "POST", path);
    }

    public LocalPostRequest(String path, Map<String, String> params) {
        super("", "POST", path);
        setPostBody(params);
    }


    public LocalPostRequest(String path, String body, String type) {
        super("", "POST", path);
        setPostBody(body, type);
    }

    private void setPostBody(String body, String type) {
        put(HTTPConstants.REQUEST_CONTENT_TYPE, type);
        setContent(body);
    }

    private void setPostBody(Map<String, String> params) {
        setPostBody(encode(params), HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
    }
}
