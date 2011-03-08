package tests;

import java.io.IOException;

import org.stringtree.mojasef.apps.LiteralURLRouter;
import org.stringtree.util.MethodCallUtils;

public class FolderTest extends FolderTestCase {
    
    public void setUp() throws IOException {
        super.setUp();
        folder = new LiteralURLRouter("src/test/files/foldertest.spec");
        MethodCallUtils.call(folder, "init", context);
    }
}
