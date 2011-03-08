package org.stringtree.mojasef.apps;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.routing.Mount;
import org.stringtree.mojasef.routing.URLMount;
import org.stringtree.tract.MapTract;
import org.stringtree.tract.StreamTractReader;
import org.stringtree.util.ContextClassUtils;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.iterator.BlankPaddedSpliterator;
import org.stringtree.util.iterator.SkipBlankAndCommentLineIterator;
import org.stringtree.util.iterator.Spliterator;
import org.stringtree.util.iterator.StringIterator;
import org.stringtree.util.spec.SpecReader;

public class LiteralURLRouter extends URLRouter {

    protected String specfile;

    public LiteralURLRouter(String args) {
        specfile = args;
    }
    
    public void init(StringKeeper context) {
        createMounts(context);
        loadSpecification(context);
    }

    private void loadSpecification(StringKeeper context) {
        try {
            MapTract spec = new MapTract();
            StreamTractReader.load(spec, specfile, context);
            SpecReader.load(context, spec);
            String mountspec = spec.getContent();
            parse(mountspec, mounts, context);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parse(String content, Collection<Mount> dest, StringKeeper context) {
        StringIterator lines = new SkipBlankAndCommentLineIterator(new StringReader(content), "#");
        while (lines.hasNext()) {
            String pattern = "";
            String application = "";
            String tail = "";
            
            String line = lines.nextString();
            Spliterator words = new BlankPaddedSpliterator(line);
            
            if (words.hasNext()) pattern = words.nextString(); 
            if (words.hasNext()) application = words.nextString(); 
            if (words.hasNext()) tail = words.tail();
            
            if (!StringUtils.isBlank(pattern) && !StringUtils.isBlank(application)) {
                String prefix = pattern;
                if ("*".equals(prefix)) prefix = "";
                Object app = ContextClassUtils.ensureObject(application, tail, context, false);
                if (app != null) {
                    dest.add(new URLMount(prefix, app));
                    MethodCallUtils.call(app, "init", context);
                    MethodCallUtils.call(app, "mojasef_init", context);
                }
            }
        }
    }
}
