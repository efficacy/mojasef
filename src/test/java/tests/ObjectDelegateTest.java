package tests;

import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.Mojasef;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.PathInfoParser;

import stubs.DflOut;
import stubs.NoRet;
import stubs.StringRet;
import stubs.SysOut;

public class ObjectDelegateTest extends StringCollectionTest {
    Object dfl;

    public void setUp() {
    	super.setUp();
        dfl = new DflOut();
    }
    
    public void testStringRet() {
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet worked");
	}

	public void testStringRetByName() {
        context.put("a name", new StringRet());
        Mojasef.delegate(collector, keeper, "a name", dfl);
        assertCollected("StringRet worked");
	}

	public void testSysOut() {
        Mojasef.delegate(collector, keeper, new SysOut(), dfl);
        assertCollected("SysOut worked");
	}

	public void testSysOutByName() {
        context.put("another name", new SysOut());
        Mojasef.delegate(collector, keeper, "another name", dfl);
        assertCollected("SysOut worked");
	}

	public void testUnknowenName() {
        context.put("another name", new SysOut());
        Mojasef.delegate(collector, keeper, "wrong name", dfl);
        assertCollected("dropped through to DflOut");
	}
        
	public void testLeaf() {
        context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.REQUEST_LOCALPATH, "/");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet index worked");
	}
    
	public void testLeafWithPath() {
        context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.REQUEST_LOCALPATH, "/blah/whatever/");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet index worked");
	}
    
	public void testPath() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.REQUEST_PATHOBJECT, "ugh");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet ugh worked");
	}
    
	public void testPathFallbackToIndex()  {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.REQUEST_LOCALPATH, "");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet index worked");
	}
    
	public void testPathFallbackToDfl() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.REQUEST_LOCALPATH, "ughx");
        Mojasef.delegate(collector, keeper, new NoRet(), dfl);
        assertCollected("dropped through to DflOut");
	}

	public void testMountedPath() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.MOUNTCONTEXT, "/");
        context.put(MojasefConstants.MOUNTPOINT, "thing/what/");
        PathInfoParser.setContext("/thing/what/ugh", context);
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet ugh worked");
	}

	public void testMountedPathSlash() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.MOUNTCONTEXT, "/");
        context.put(MojasefConstants.MOUNTPOINT, "thing/what/");
        PathInfoParser.setContext("/thing/what/ugh", context);
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet ugh worked");
	}

	public void testUnmountedPath() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.MOUNTCONTEXT, "/");
        context.put(MojasefConstants.MOUNTPOINT, "");
        PathInfoParser.setContext("/ugh/thing/what", context);
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet ugh worked");
	}

	public void testMountedIndex() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.MOUNTCONTEXT, "/");
        context.put(MojasefConstants.MOUNTPOINT, "thing/what/ugh");
        PathInfoParser.setContext("/thing/what/ugh", context);
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet index worked");
	}

	public void testMountedIndexSlash() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(MojasefConstants.REQUEST_LOCALPATH, "/thing/what/ugh/");
        context.put(MojasefConstants.MOUNTPOINT, "/thing/what/ugh");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet index worked");
	}

	public void testCommand() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "xindex");
        context.put(HTTPConstants.REQUEST_METHOD, "GET");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet GET worked");
	}
    
	public void testCommandFallbackToIndex() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(HTTPConstants.REQUEST_METHOD, "POST");
        Mojasef.delegate(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet index worked");
	}
    
	public void testCommandFallbackToDfl() {
		context.put(MojasefConstants.DEFAULT_LEAFNAME, "index");
        context.put(HTTPConstants.REQUEST_METHOD, "POST");
        Mojasef.delegate(collector, keeper, new NoRet(), dfl);
        assertCollected("dropped through to DflOut");
	}
}
