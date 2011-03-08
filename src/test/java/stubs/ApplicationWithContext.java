package stubs;

public class ApplicationWithContext {
    private Object context = new ExampleApplicationContext();
    public Object getContext() {
        return context;
    }
}
