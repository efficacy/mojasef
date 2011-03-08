package org.stringtree.mojasef.model;

import org.stringtree.Fetcher;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.Templater;

public class ContextTemplateFetcher implements Fetcher {

	private Fetcher context;

	public ContextTemplateFetcher(Fetcher context) {
		this.context = context;
	}

	public Object getObject(String name) {
		Object ret = context.getObject(Templater.TEMPLATE + "." + name);
        if (null == ret) {
            Fetcher fetcher = (Fetcher)context.getObject(MojasefConstants.TEMPLATE_SOURCE);
            if (null != fetcher) {
                ret = fetcher.getObject(name);
            }
        }
        
        return ret;
	}

}
