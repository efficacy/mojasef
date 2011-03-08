package org.stringtree.mojasef.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.Repository;
import org.stringtree.SystemContext;
import org.stringtree.fetcher.DelegatedRepository;
import org.stringtree.fetcher.FallbackRepository;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.fetcher.PeelbackFetcher;
import org.stringtree.finder.live.LiveDate;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.ContextTemplater;
import org.stringtree.template.Templater;
import org.stringtree.util.ObjectToString;
import org.stringtree.util.SmartPathClassLoader;
import org.stringtree.util.logging.Logger;
import org.stringtree.util.spec.EnvironmentLoader;
import org.stringtree.util.spec.SpecProcessor;

public abstract class CommonContext
	extends DelegatedRepository
{
    protected Fetcher templates;
    protected Fetcher pub;
	protected boolean lock;

	public CommonContext(Fetcher templates, Fetcher pub, boolean lock) {
        super(new MapFetcher());
		this.templates = templates;
        this.pub = pub;
		this.lock = lock;
	}

	protected void load() {
	    loadDefaults();
        loadClasspath();
        readExternal();
        
        setApplicationContext();
	    setTemplateFinder();
        setFileFinder();

	    // init phase complete, lock the repository to prevent later changes confusing things
		if (lock) lock();
	}

    protected void loadDefaults() {
        put(HTTPConstants.HTTP_SPEC_URL, "http.spec");
        put(HTTPConstants.SESSION_KEYFINDER, new CookieKeyFinder("mojasess"));
        put(HTTPConstants.RESPONSE_CODE, "200");
		put(HTTPConstants.RESPONSE_CONTENT_TYPE, "text/html");
        put(MojasefConstants.APPLICATION_NAME, "Mojasef - Modular Java Server");
        put(MojasefConstants.DEFAULT_LEAFNAME, MojasefConstants.DEFAULT_LEAF);
        put(MojasefConstants.MOUNTCONTEXT, "/");
        put(MojasefConstants.MOUNTPOINT, "");
        put(MojasefConstants.PROLOGUE_TEMPLATE, "page_prologue");
        put(MojasefConstants.EPILOGUE_TEMPLATE, "page_epilogue");
	}

	protected void loadClasspath() {
        put(MojasefConstants.SERVER_LOGGER, Logger.logger);

        ClassLoader classloader = new SmartPathClassLoader("lib;.", getClass().getClassLoader());
	    put(SystemContext.SYSTEM_CLASSLOADER, classloader);

	    List<String> imports = new ArrayList<String>();
	    imports.add("org.stringtree.mojasef");
	    put(SystemContext.IMPORT_PACKAGES, imports);

	    put(MojasefConstants.SYSTEM_TIMESTAMP, new LiveDate());
	}
	
	protected void setTemplateFinder() {
        Fetcher source = (Fetcher) getObject(MojasefConstants.TEMPLATE_SOURCE);
        if (null == source) {
        	if (null != templates) {
            	source = templates;
        	} else {
        		source = this;
        	}
            put(MojasefConstants.TEMPLATE_SOURCE, source);
        }

        Templater templater = new ContextTemplater();

        if (null != getObject(MojasefConstants.MOJASEF_OUTPUT_ESCAPER)) {
            ObjectToString converter = (ObjectToString)getObject(MojasefConstants.MOJASEF_OUTPUT_ESCAPER);
            templater.setStringConverter(converter);
        }

        put(Templater.TEMPLATE, new ContextTemplateFetcher(this));
        put(Templater.TEMPLATER, templater);
	}
    
    protected void setFileFinder() {
        Fetcher fileFetcher = (Fetcher) getObject(MojasefConstants.FILE_FETCHER);
        if (null == fileFetcher) {
            if (pub == null) {
                pub = new ContextFileFetcher(this);
            }
            put(MojasefConstants.FILE_FETCHER, pub);
        }
    }

    protected void readExternal() {
        EnvironmentLoader.loadEnvironment(this);
        
        SpecProcessor spec = new SpecProcessor(this);
        // open the spec read session, gathering objects to initialise later
        spec.start();

        spec.read(System.getProperties());
    	readExternal(spec);
    
        // close the spec read session, and call the "init" method of any created objects
        spec.finish();
    }

    @SuppressWarnings("unchecked")
    private void setApplicationContext() {
        Repository original = realRepository();
        Fetcher peelback = new PeelbackFetcher(original);
        Object object = peelback.getObject("http.application.context");
        if (null != object) {
            if (object instanceof Fetcher) {
                setOther(new FallbackRepository((Fetcher)object, original));
            } else if (object instanceof Map) {
                setOther(new FallbackRepository(new MapFetcher((Map<String, Object>)object), original));
            }
        }
    }

    protected abstract void readExternal(SpecProcessor spec);

    public boolean contains(String name) {
        return null != getObject(name);
    }

    public void putAll(Map<String, Object> map) {
        ((MapFetcher)realRepository()).putAll(map);
    }
}
