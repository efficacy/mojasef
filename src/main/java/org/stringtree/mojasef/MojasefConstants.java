package org.stringtree.mojasef;

import org.stringtree.template.Templater;

public interface MojasefConstants {
    static final String MOJASEF_CONFIG_MIME_MAP = "mojasef.config.mime.map";

    static final String MOJASEF_SERVER_NAME = "mojasef.server.name";
    static final String SYSTEM_TIMESTAMP = "mojasef.system.timestamp";
    static final String SYSTEM_CONTEXT = "mojasef.system.context";
    static final String RESPONSE_STREAM = "mojasef.response.stream";
    static final String REQUEST_STREAM = "mojasef.request.stream";
	static final String SERVER_LOGGER = "mojasef.logger";
	static final String ERROR_TEXT = "mojasef.error.text";
	static final String ERROR_EXCEPTION = "mojasef.exception";
    static final String ALLOW_SYSOUT_LOGGING = "mojasef.allow.sysout.logging";

	static final String MOUNTPOINT = "mojasef.mount.point";
	static final String MOUNTCONTEXT = "mojasef.mount.context";
	static final String DEFAULT_LEAFNAME = "mojasef.leafname";
    static final String DEFAULT_LEAF = "index";

	static final String REQUEST_PREFIX = "mojasef.request.path.";
    static final String REQUEST_URI = REQUEST_PREFIX+"URI";
    static final String REQUEST_SCHEME = REQUEST_PREFIX+"scheme";
    static final String REQUEST_HOST = REQUEST_PREFIX+"host";
    static final String REQUEST_PORT = REQUEST_PREFIX+"port";
    static final String REQUEST_PATH = REQUEST_PREFIX+"path";
    static final String REQUEST_LOCALPATH = REQUEST_PREFIX+"localpath";
    static final String REQUEST_ISFULLPATH = REQUEST_PREFIX+"isfullpath";
    static final String REQUEST_ISLOCALPATH = REQUEST_PREFIX+"islocalpath";
    static final String REQUEST_ISRELATIVEPATH = REQUEST_PREFIX+"isrelativepath";
    static final String REQUEST_QUERY = REQUEST_PREFIX+"query";
    static final String REQUEST_PATHOBJECT = REQUEST_PREFIX+"object";
    static final String REQUEST_PATHTAIL = REQUEST_PREFIX+"tail";

    static final String REQUEST_LEAF = REQUEST_PREFIX+"leaf";
    static final String REQUEST_LEAFPLUS = REQUEST_PREFIX+"leafplus";
    static final String REQUEST_LEAFQ = REQUEST_PREFIX+"leafq";
    
    static final String OUTPUT_COLLECTOR = "mojasef.output.collector";
    static final String TEMPLATE_SOURCE = Templater.TEMPLATE_SOURCE; // @deprecated, use the templater one
    static final String FILE_FETCHER = "public";
    static final String APPLICATION_NAME = "mojasef.application.name";
    static final String REQUEST_SELF = REQUEST_PREFIX+"self";
    static final String COMMAND_ARGS_OBJECT = "parameters";
    static final String MOJASEF_OUTPUT_ESCAPER = "mojasef.output.escaper";
    static final String PAGE_CLASS = "page.class";
    static final String PAGE_TEMPLATE = "page.template";
    static final String PROLOGUE_TEMPLATE = "prologue.template";
    static final String EPILOGUE_TEMPLATE = "epilogue.template";
    static final String APPLICATION_BASE_URL = "application.base.url";
    static final String SPEC_CONTENT = "mojasef.spec.content";

    static final String HTTP_APPLICATION = "http.application";

}
