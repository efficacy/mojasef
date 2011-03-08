package org.stringtree.mojasef.apps;

import org.stringtree.Fetcher;

public class BasicApplication
{
    protected Fetcher context;
    
    // warmup: take a note of the contextfor later use
    public Object warmup(Fetcher context)
    {
        this.context = context;
		return null;
    }
}
