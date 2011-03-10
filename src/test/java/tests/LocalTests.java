package tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LocalTests extends TestCase {
	public static TestSuite suite() {
		TestSuite ret = new TestSuite();

        ret.addTestSuite(StandaloneCookieTest.class);
        ret.addTestSuite(StandalonePostTest.class);
        ret.addTestSuite(StandaloneInitTerminateTest.class);
        ret.addTestSuite(LocalServerTest.class);
        ret.addTestSuite(PluginTest.class);
        ret.addTestSuite(TractReturnTest.class);
		ret.addTestSuite(SimpleAppTest.class);
		ret.addTestSuite(NoAppTest.class);
		ret.addTestSuite(TemplateFallthroughTest.class);

        ret.addTestSuite(AliasTest.class);
        ret.addTestSuite(CommonContextFallbackTest.class);
        ret.addTestSuite(AuthRestTest.class);
        ret.addTestSuite(RESTTest.class);
        ret.addTestSuite(AuthTest.class);
        ret.addTestSuite(ContextFolderTest.class);
        ret.addTestSuite(FolderTest.class);
        ret.addTestSuite(PathInfoParserTest.class);
        ret.addTestSuite(ParameterTest.class);
        ret.addTestSuite(LiteralContextSimpleTest.class);
        ret.addTestSuite(CommonContextAccessorApplicationContextTest.class);
        ret.addTestSuite(CommonContextLiteralApplicationContextTest.class);
		ret.addTestSuite(CommonContextEmptyTest.class);
        ret.addTestSuite(CommonContextSimpleTest.class);
        ret.addTestSuite(CommonContextMountTest.class);
        ret.addTestSuite(DelegateAndExpandTest.class);
        ret.addTestSuite(ObjectDelegateTest.class);
        ret.addTestSuite(MethodCallTest.class);
		ret.addTestSuite(ParameterHeaderTest.class);
		ret.addTestSuite(EmptyTest.class);

		return ret;
	}
}
