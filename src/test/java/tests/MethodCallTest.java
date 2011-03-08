package tests;

import org.stringtree.mojasef.Mojasef;

import stubs.DflOut;
import stubs.NoRet;
import stubs.StringKeeperParam;
import stubs.StringRet;
import stubs.SysOut;

public class MethodCallTest extends StringCollectionTest {
    Object dfl;

    public void setUp() {
    	super.setUp();
        dfl = new DflOut();
    }
    
    public void assertCollected(String text) {
        assertEquals(text, collector.toString());
    }

	public void testStringRet() {
        Mojasef.delegate(keeper, new StringRet(), dfl);
        assertCollected("StringRet worked");
	}

    public void testSysOut() {
        Mojasef.delegate(keeper, new SysOut(), dfl);
        assertCollected("SysOut worked");
    }

    public void testFallThrough() {
        Mojasef.delegate(keeper, new NoRet(), dfl);
        assertCollected("dropped through to DflOut");
    }
    
    public void testCallKeeperArg() {
    	context.put("ugh", "123");
        Mojasef.delegate(keeper, new StringKeeperParam(), dfl);
        assertCollected("SK worked, got value '123'");
    }
    
    // TODO add tests for mapping to other methods
    // TODO add tests for falling back up the mount tree
    // TOTO add tests for finding the right template and expanding 
}
