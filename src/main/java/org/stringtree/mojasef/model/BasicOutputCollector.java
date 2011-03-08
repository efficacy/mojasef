package org.stringtree.mojasef.model;

import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.ByteArrayStringCollector;

public abstract class BasicOutputCollector extends ByteArrayStringCollector 
	implements ModalOutputCollector {

	protected RequestContext context;
	
	public BasicOutputCollector(RequestContext context) {
		this.context = context;
	}
	
	public void start() {
		context.put(MojasefConstants.OUTPUT_COLLECTOR, this);
	}
	
	public void finish() {
		flush();
		collectheaders();
		collectBody();
	}
	
	protected abstract void collectBody();
	
	protected void collectheaders() {
		context.collectResponse(this);
		context.collectHeaders(this);
		context.collectCookies(this);
	}
}
