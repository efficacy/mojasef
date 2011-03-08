package org.stringtree.mojasef.local;

import java.util.HashMap;
import java.util.Map;

public class ArgumentRequest extends LocalRequest {

	public Map<String, Object> data;

	public ArgumentRequest(String path) {
		super(path);
		data = new HashMap<String, Object>();
	}

	public void putArgument(String key, Object value) {
		data.put(key, value);
	}

}
