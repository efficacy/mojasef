package org.stringtree.mojasef.apps;

import org.stringtree.finder.StringKeeper;
import org.stringtree.util.Factory;
import org.stringtree.util.MethodCallUtils;

public class NewInstanceWrapper extends ApplicationWrapper {

    public NewInstanceWrapper(String classname) {
        super(classname);
    }

    public NewInstanceWrapper(Factory factory) {
        super(factory);
    }

    public Object warmup(StringKeeper requestContext) {
        Object application = createApplication(requestContext);
        Object redirect = MethodCallUtils.call(application, "warmup", requestContext);
        if (null != redirect) application = redirect;
        return application;
    }
}
