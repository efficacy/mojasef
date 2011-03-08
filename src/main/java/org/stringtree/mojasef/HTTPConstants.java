package org.stringtree.mojasef;

public interface HTTPConstants {
    static final String REQUEST_HEADER = "http.request.header.";
    static final String REQUEST_COOKIE = "http.request.cookie.";
    static final String RESPONSE_HEADER = "http.response.header.";
    static final String RESPONSE_COOKIE = "http.response.cookie.";
    static final String RESPONSE_CODE = "http.response.code";
    static final String RESPONSE_MESSAGE = "http.response.message";

    static final String HTTP_REQUEST = "http.request";
    static final String SESSION_KEYFINDER = "http.app.keyfinder";

    static final String REQUEST_METHOD = "http.request.method";
    static final String REQUEST_PROTOCOL = "http.request.protocol";
    static final String RESPONSE_PROTOCOL = "http.response.protocol";

    static final String HTTP_SPEC_URL = "http.spec.URL";
	static final String REMOTE_ADDRESS = "server.remote.address";
    
    static final String WEB_EOL = "\r\n";

    static final String JSON_CONTENT_TYPE = "text/json";
    static final String XML_CONTENT_TYPE = "text/xml";
    static final String HTML_CONTENT_TYPE = "text/html";
    static final String WML_CONTENT_TYPE = "text/vnd.wap.wml";
    static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    static final String MULTIPART_FORM_DATA = "multipart/form-data";

    static final String CONTENT_TYPE = "Content-Type";
    static final String CONTENT_LENGTH = "Content-Length";
    
    static final String REQUEST_CONTENT_TYPE = REQUEST_HEADER+CONTENT_TYPE;
    static final String REQUEST_CONTENT_LENGTH = REQUEST_HEADER+CONTENT_LENGTH;
    static final String REQUEST_CONTENT_TYPE_CHARSET = REQUEST_CONTENT_TYPE+".charset";
    static final String REQUEST_HEADER_AUTHORIZATION = REQUEST_HEADER+"Authorization";
    static final String REQUEST_HEADER_USER_AGENT = REQUEST_HEADER+"user-agent";
    
    static final String RESPONSE_HEADER_LOCATION = RESPONSE_HEADER+"Location";
    static final String RESPONSE_HEADER_AUTHENTICATE = RESPONSE_HEADER+"WWW-Authenticate";
    static final String RESPONSE_CONTENT_TYPE = RESPONSE_HEADER+CONTENT_TYPE;
    static final String RESPONSE_CONTENT_LENGTH = RESPONSE_HEADER+CONTENT_LENGTH;
}
