package tests;

import java.io.IOException;

import org.stringtree.mojasef.apps.ContextURLRouter;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.spec.SpecReader;

public class ContextFolderTest extends FolderTestCase {
    
    public void setUp() throws IOException {
        super.setUp();
        SpecReader.load(context, "src/test/files/contextfolder.spec");
        folder = new ContextURLRouter("folder");
        MethodCallUtils.call(folder, "init", context);
    }

    public void testFolderSpecPresentInContext() {
        assertNotNull(context.getObject("folder"));
    }
}
