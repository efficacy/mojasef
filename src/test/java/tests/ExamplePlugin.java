package tests;

import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.plugin.Plugin;
import org.stringtree.template.plugin.PluginManager;

public class ExamplePlugin implements Plugin {

	public boolean match(StringFinder context) {
		String leaf = context.get(MojasefConstants.REQUEST_LEAF);
		return "complex".equals(leaf);
	}

	public void ex(StringKeeper context) {
		context.put(PluginManager.PLUGIN_TEMPLATE, "plugin_example");
	}
}
