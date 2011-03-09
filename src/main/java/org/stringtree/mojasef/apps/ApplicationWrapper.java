package org.stringtree.mojasef.apps;

import org.stringtree.SystemContext;
import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.util.Factory;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.ParameterClassUtils;

public class ApplicationWrapper {

    protected StringFinder initContext = null;
    protected String classname = null;
    protected Factory factory = null;

    public ApplicationWrapper(String classname) {
        this.classname = classname;
    }

    public ApplicationWrapper(Factory factory) {
        this.factory = factory;
    }

    protected ClassLoader getClassLoader(StringFinder context) {
        ClassLoader loader = null;
        
        if (context != null) {
            Object cl = context.getObject(SystemContext.SYSTEM_CLASSLOADER);
            if (cl instanceof ClassLoader)
                loader = (ClassLoader) cl;
            }
        if (loader == null)
            loader = getClass().getClassLoader();
        return loader;
    }

    public void init(StringFinder context) {
        this.initContext = context;
    }

    protected Object createApplication(StringKeeper requestContext) {
        Object application = null;;
        
    	if (null != classname) {
	        application = ParameterClassUtils.createObject(classname, getClassLoader(requestContext));
    	}
    	
    	if (null != factory) {
    		application = factory.create();
    	}
    	
    	if (null != application) {
    		MethodCallUtils.call(application, "init", initContext);
    	}
    	
        return application;
    }

}
