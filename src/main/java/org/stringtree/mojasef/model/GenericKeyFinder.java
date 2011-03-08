package org.stringtree.mojasef.model;

import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.KeyFinder;

public abstract class GenericKeyFinder implements KeyFinder {

    protected String token;
    protected int n = 0;

    public GenericKeyFinder(String token) {
        this.token = token;
    }

    public String findKey(StringFinder context, String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    public synchronized String nextValue() {
        return Integer.toString(++n);
    }

}
