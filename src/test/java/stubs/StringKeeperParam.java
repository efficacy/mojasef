package stubs;

import org.stringtree.finder.StringKeeper;

public class StringKeeperParam
{
    public String request(StringKeeper sk)
    {
        return "SK worked, got value '" + sk.get("ugh") + "'";
    }
}
