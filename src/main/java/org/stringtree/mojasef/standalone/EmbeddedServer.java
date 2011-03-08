package org.stringtree.mojasef.standalone;

import java.io.IOException;

public class EmbeddedServer extends WebServer {
    public EmbeddedServer() throws IOException {
        super("classpath:http.spec", false);
    }
    
    public static void main(String[] args) {
        try {
            new EmbeddedServer().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
