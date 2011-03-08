package tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RemoteTests extends TestCase
{
	public static TestSuite suite()
	{
		TestSuite ret = new TestSuite();

		//ret.addTest(new TestSuite(Remo.class));

		return ret;
	}
}
